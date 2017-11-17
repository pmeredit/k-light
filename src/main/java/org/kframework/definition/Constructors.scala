// Copyright (c) 2014 K Team. All Rights Reserved.

package org.kframework.definition

import org.kframework.attributes.Att
import org.kframework.frontend._
import org.kframework.{attributes, definition}

import scala.collection._

/**
 *
 * Helper constructors for KORE definition.classes. The class is meant to be imported
 * statically.
 *
 */

object Constructors {

  def Definition(mainModule: Module, modules: Set[Module], att: Att) =
    definition.Definition(mainModule, modules, att)

  def Module(name: String, sentences: Set[Sentence]) = definition.Module(name, Set(), sentences, Att())
  def Module(name: String, sentences: Set[Sentence], att: attributes.Att) = definition.Module(name, Set(), sentences, att)
  def Module(name: String, imports: Set[Module], sentences: Set[Sentence], att: attributes.Att) = definition.Module(name, imports, sentences, att)

  def SyntaxSort(sort: Sort) = definition.SyntaxSort(sort)
  def SyntaxSort(sort: Sort, att: attributes.Att) = definition.SyntaxSort(sort, att)

  def Production(sort: Sort, items: Seq[ProductionItem]) = definition.Production(sort, items, Att())
  def Production(sort: Sort, items: Seq[ProductionItem], att: attributes.Att) = definition.Production(sort, items, att)
  def Production(klabel: String, sort: Sort, items: Seq[ProductionItem]) = definition.Production(sort, items, Att().add(klabel))
  def Production(klabel: String, sort: Sort, items: Seq[ProductionItem], att: attributes.Att) = definition.Production(sort, items, att.add(klabel))

  def Terminal(s: String) = definition.Terminal(s, Seq())
  def NonTerminal(sort: Sort) = definition.NonTerminal(sort)
  def RegexTerminal(regexString: String) = definition.RegexTerminal("#", regexString, "#")
  def RegexTerminal(precedeRegexString: String, regexString: String, followRegexString: String) = definition.RegexTerminal(precedeRegexString, regexString, followRegexString)

  def Tag(s: String) = definition.Tag(s)

  def SyntaxPriority(priorities: Seq[Set[Tag]]) = definition.SyntaxPriority(priorities)
  def SyntaxPriority(priorities: Seq[Set[Tag]], att: attributes.Att) = definition.SyntaxPriority(priorities, att)

  def SyntaxAssociativity(assoc: definition.Associativity.Value, tags: Set[Tag]) = definition.SyntaxAssociativity(assoc, tags)
  def SyntaxAssociativity(assoc: definition.Associativity.Value, tags: Set[Tag], att: attributes.Att) = definition.SyntaxAssociativity(assoc, tags, att)

  def Bubble(sentenceType: String, content: String, att: attributes.Att) =
    definition.Bubble(sentenceType, content, att)

  def Associativity = definition.Associativity

  def Att() = attributes.Att()
}
