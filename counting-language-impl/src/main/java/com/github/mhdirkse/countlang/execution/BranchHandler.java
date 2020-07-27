package com.github.mhdirkse.countlang.execution;

public interface BranchHandler {
    void onSwitchOpened();
    void onSwitchClosed();
    void onBranchOpened();
    void onBranchClosed();
}
