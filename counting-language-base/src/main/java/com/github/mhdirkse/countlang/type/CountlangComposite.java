package com.github.mhdirkse.countlang.type;

import java.util.List;

public interface CountlangComposite {
    int size();
    Object get(int i);
    List<Object> getAll();
}
