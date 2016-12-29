package com.github.mhdirkse.countlang.compiler;

interface MemoryMapper {
    int getValuePosition(int valueSeq);
    int getVariablePosition(int variableSeq);
}
