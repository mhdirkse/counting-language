package com.github.mhdirkse.countlang.generator;

import com.github.mhdirkse.codegen.compiletime.MethodModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisitorMethodModel extends MethodModel {
    private boolean atomic;
    private MethodModel enterMethod;
    private MethodModel exitMethod;
    private MethodModel visitMethod;

    VisitorMethodModel(final MethodModel m) {
        super(m);
        atomic = false;
    }

    VisitorMethodModel(final VisitorMethodModel orig) {
        super(orig);
        atomic = orig.atomic;
        enterMethod = new MethodModel(orig.enterMethod);
        exitMethod = new MethodModel(orig.exitMethod);
        visitMethod = new MethodModel(orig.visitMethod);
    }
}
