// Copyright (c) 2012-2016 K Team. All Rights Reserved.
package org.kframework.frontend.kil;

import org.kframework.frontend.kil.visitors.Visitor;
import org.kframework.utils.errorsystem.KExceptionManager;

import java.io.File;
import java.util.*;


/**
 * Represents a language definition.
 * Includes contents from all {@code required}-d files.
 * @see DefinitionLoader
 */
public class Definition extends ASTNode implements Interfaces.MutableList<DefinitionItem, Enum<?>> {

    private List<DefinitionItem> items;
    private File mainFile;
    private String mainModule;
    /** An index of all modules in {@link #items} by name */
    private String mainSyntaxModule;
    public Map<String, ASTNode> locations = new HashMap<>();

    public Definition() {
        super();
    }

    public Definition(Definition d) {
        super(d);
        this.mainFile = d.mainFile;
        this.mainModule = d.mainModule;
        this.mainSyntaxModule = d.mainSyntaxModule;
        this.items = d.items;
        this.locations = d.locations;
    }

    @Override
    public String toString() {
        String content = "";
        List<DefinitionItem> sortedItems = new ArrayList<>(items);
        sortedItems.sort(new Comparator<DefinitionItem>() {
            @Override
            public int compare(DefinitionItem o1, DefinitionItem o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        for (DefinitionItem di : sortedItems)
            content += di + " \n";

        return "DEF: " + mainFile + " -> " + mainModule + "\n" + content;
    }

    public void setItems(List<DefinitionItem> items) {
        this.items = items;
    }

    public List<DefinitionItem> getItems() {
        return items;
    }

    public void setMainFile(File mainFile) {
        this.mainFile = mainFile;
    }

    public File getMainFile() {
        return mainFile;
    }

    public void setMainModule(String mainModule) {
        this.mainModule = mainModule;
    }

    public String getMainModule() {
        return mainModule;
    }

    public void setMainSyntaxModule(String mainSyntaxModule) {
        this.mainSyntaxModule = mainSyntaxModule;
    }

    public String getMainSyntaxModule() {
        return mainSyntaxModule;
    }

    public Module getSingletonModule() {
        List<Module> modules = new LinkedList<Module>();
        for (DefinitionItem i : this.getItems()) {
            if (i instanceof Module)
                modules.add((Module) i);
        }
        if (modules.size() != 1) {
            String msg = "Should have been only one module when calling this method.";
            throw KExceptionManager.internalError(msg, this);
        }
        return modules.get(0);
    }

    @Override
    public Definition shallowCopy() {
        return new Definition(this);
    }

    @Override
    protected <P, R, E extends Throwable> R accept(Visitor<P, R, E> visitor, P p) throws E {
        return visitor.complete(this, visitor.visit(this, p));
    }

    @Override
    public List<DefinitionItem> getChildren(Enum<?> _void) {
        return items;
    }

    @Override
    public void setChildren(List<DefinitionItem> children, Enum<?> _void) {
        this.items = children;
    }

}
