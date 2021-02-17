package com.github.mhdirkse.countlang.algorithm;

public interface Scope {
    ScopeAccess getAccess();
    boolean has(String symbolName);
}
