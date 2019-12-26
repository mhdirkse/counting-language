package com.github.mhdirkse.countlang.lang.parsing;

interface TerminalFilterCallback {
    int getRequiredType();
    void setText(final String text);
}
