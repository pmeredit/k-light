// Copyright (c) 2012-2016 K Team. All Rights Reserved.
package org.kframework.frontend.kil;

public abstract class ModuleItem extends ASTNode {
    public ModuleItem(ModuleItem s) {
        super(s);
    }

    public ModuleItem() {
        super();
    }

    public java.util.List<String> getLabels() {
        return null;
    }

    public java.util.List<String> getKLabels() {
        return null;
    }

    public java.util.List<Sort> getAllSorts() {
        return null;
    }
}
