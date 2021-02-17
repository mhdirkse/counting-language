package com.github.mhdirkse.countlang.algorithm;

public interface CountlangStackItem {
    StackFrameAccess getAccess();
    boolean has(String symbolName);
}
