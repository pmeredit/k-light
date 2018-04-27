// Copyright (c) 2014 K Team. All Rights Reserved.

package org.kframework.definition

import org.kframework.attributes.{Location, Source}
import org.kframework.definition
import org.kframework.utils.errorsystem.KEMException

import collection._
import scala.collection.immutable.Stack

object ModuleTransformer {
  def fromSentenceTransformer(sentenceTransformer: Sentence => Sentence, passName: String): MemoizingModuleTransformer =
    new SentenceBasedModuleTransformer {
      override val name = passName
      override def process(s: Sentence) = sentenceTransformer(s)
    }

  def fromSentenceTransformer(sentenceTransformer: (Module, Sentence) => Sentence, passName: String): BasicModuleTransformer =
    new SentenceBasedModuleTransformer {
      override val name = passName
      override def process(s: Sentence, inputModule: Module) = sentenceTransformer(inputModule, s)
    }

  def fromHybrid(f: Module => Module, name: String): HybridMemoizingModuleTransformer = {
    val lName = name
    new HybridMemoizingModuleTransformer {
      override def processHybridModule(hybridModule: Module): Module = f(hybridModule)
      override val name: String = lName
    }
  }
}

class ModuleTransformerException(moduleTransformerName: String, cause: Throwable) extends Exception(cause) {
  override def toString = moduleTransformerName + " : " + cause.toString
}

/**
  * Any overriding class should call wrapExceptions to make sure its
  * errors are correctly wrapped in a ModuleTransformerException
  */
abstract class ModuleTransformer extends (Module => Module) {
  val name: String = this.getClass.getName
  def wrapExceptions(f: => Module): Module = try {
    f
  } catch {
    case e: ModuleTransformerException => throw e
    case e: Throwable => throw new ModuleTransformerException(name, e)
  }
}

/**
  * A module transformer with memoization
  */
abstract class MemoizingModuleTransformer extends ModuleTransformer {
  val memoization = mutable.Map[Module, Module]()
  var currentProcessedModules = Stack[Module]()

  override def apply(input: Module): Module = this.synchronized {
    if (currentProcessedModules.contains(input))
      throw new AssertionError("Found a cycle on: " + input.name + " with chain: " + currentProcessedModules.map(_.name).toList.reverse.mkString(" -> "))
    currentProcessedModules = currentProcessedModules.push(input)
    val res = wrapExceptions(memoization.getOrElseUpdate(input, {processModule(input)}))
    currentProcessedModules = currentProcessedModules.pop
    res
  }

  protected def processModule(inputModule: Module): Module

  def lift = DefinitionTransformer(this)

  def apply(d: Definition): Definition = lift(d)
}

/**
  * Marker trait for a ModuleTransformer having access to the entire original definition
  */
trait WithInputDefinition {
  val inputDefinition: Definition
}

/**
  * The processHybridModule function take a module with all the imported modules already transformed,
  * and uses it to create the updated module.
  *
  * Natural to use but risky as the "hybrid" module may not be consistent. It is better not to use it as we might
  * deprecate it in the future.
  */
trait HybridModuleTransformer extends ModuleTransformer {
  def apply(input: Module): Module = wrapExceptions({
    val newImports = input.imports map this
    if (newImports != input.imports)
      processHybridModule(Module(input.name, newImports, input.localSentences, input.att, input.location, input.source))
    else
      processHybridModule(input)
  })

  protected def processHybridModule(hybridModule: Module): Module
}

abstract class BasicModuleTransformer extends MemoizingModuleTransformer {
  final def processModule(input: Module): Module = wrapExceptions({
    process(input, input.imports map this)
  })

  protected def process(inputModule: Module, alreadyProcessedImports: Set[Module]): Module
}

/**
  * Define a [[Module]] transformer by overriding any of the [[process]] methods.
  * All other wiring (e.g., recursion over [[Module]]s is already handled by the class.
  */
abstract class SentenceBasedModuleTransformer extends BasicModuleTransformer {
  def process(s: Sentence, inputModule: Module, alreadyProcessedModules: Set[Module]): Sentence = process(s, inputModule)

  def process(s: Sentence, inputModule: Module): Sentence = process(s)

  def process(s: Sentence): Sentence = s

  override def process(inputModule: Module, alreadyProcessedImports: Set[Module]): Module = {
    val newSentences = inputModule.localSentences map {
      s =>
        try {
          process(s, inputModule, alreadyProcessedImports)
        } catch {
          case e: KEMException =>
            e.exception.addTraceFrame("while executing phase \"" + name + "\" on sentence at"
              + "\n\t" + s.att.get("Source").map(_.toString).getOrElse("<none>")
              + "\n\t" + s.att.get("Location").map(_.toString).getOrElse("<none>"))
            throw e
        }
    }
    Module(inputModule.name, alreadyProcessedImports, newSentences, inputModule.att, inputModule.location, inputModule.source)
  }
}

abstract class WithInputDefinitionModuleTransformer(val inputDefinition: Definition) extends BasicModuleTransformer {
  def apply(moduleName: String): Module = this (inputDefinition.getModule(moduleName).get)
  def outputDefinition = new DefinitionTransformer(this).apply(inputDefinition)
}


abstract class HybridMemoizingModuleTransformer extends MemoizingModuleTransformer with HybridModuleTransformer {
  override def apply(input: Module): Module = super[MemoizingModuleTransformer].apply(input)

  protected def processModule(inputModule: Module): Module = super[HybridModuleTransformer].apply(inputModule)
}

object DefinitionTransformer {
  def fromSentenceTransformer(f: Sentence => Sentence, name: String): DefinitionTransformer =
    new DefinitionTransformer(ModuleTransformer.fromSentenceTransformer(f(_), name))

  def fromSentenceTransformer(f: (Module, Sentence) => Sentence, name: String): DefinitionTransformer =
    DefinitionTransformer(ModuleTransformer.fromSentenceTransformer(f, name))

  def fromHybrid(f: Module => Module, name: String): DefinitionTransformer = DefinitionTransformer(ModuleTransformer.fromHybrid(f, name))

  def apply(f: MemoizingModuleTransformer): DefinitionTransformer = new DefinitionTransformer(f)

  def fromWithInputDefinitionTransformerClass(c: Class[_]): (Definition => Definition) = (d: Definition) => c.getConstructor(classOf[Definition]).newInstance(d).asInstanceOf[WithInputDefinitionModuleTransformer].outputDefinition

}

class DefinitionTransformer(moduleTransformer: MemoizingModuleTransformer) extends (Definition => Definition) {
  override def apply(d: Definition): Definition = {
    //    definition.Definition(
    //      moduleTransformer(d.mainModule),
    //      d.entryModules map moduleTransformer,
    //      d.att)
    // commented above such that the regular transformer behaves like the SelectiveDefinitionTransformer
    // this avoids a bug in the configuration concretization functionality
    d
  }
}
