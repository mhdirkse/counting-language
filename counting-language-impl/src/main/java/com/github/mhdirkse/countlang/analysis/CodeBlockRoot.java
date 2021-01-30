package com.github.mhdirkse.countlang.analysis;

class CodeBlockRoot extends CodeBlockSerial {
    CodeBlockRoot() {
        super();
    }

    @Override
    boolean isRootOrFunction() {
        return true;
    }
}
