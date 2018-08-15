package com.github.mhdirkse.countlang.generator;

import com.github.mhdirkse.codegen.compiletime.MethodModel;

public class MethodExtraModel {
    private boolean atomic;

    private MethodModel enterMethod;

    private MethodModel exitMethod;

    private MethodModel visitMethod;

    public boolean isAtomic() {
        return atomic;
    }

    public void setAtomic(boolean atomic) {
        this.atomic = atomic;
    }

    public MethodModel getEnterMethod() {
        return enterMethod;
    }

    public void setEnterMethod(MethodModel enterMethod) {
        this.enterMethod = enterMethod;
    }

    public MethodModel getExitMethod() {
        return exitMethod;
    }

    public void setExitMethod(MethodModel exitMethod) {
        this.exitMethod = exitMethod;
    }

    public MethodModel getVisitMethod() {
        return visitMethod;
    }

    public void setVisitMethod(MethodModel visitMethod) {
        this.visitMethod = visitMethod;
    }

    MethodExtraModel() {
        atomic = false;
    }

    MethodExtraModel(final MethodExtraModel orig) {
        atomic = orig.atomic;
        enterMethod = new MethodModel(orig.enterMethod);
        exitMethod = new MethodModel(orig.exitMethod);
        visitMethod = new MethodModel(orig.visitMethod);
    }
}
