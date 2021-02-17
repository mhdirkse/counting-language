package com.github.mhdirkse.countlang.analysis;

import java.util.Iterator;

interface BlockListener {
    default void startSwitch(CodeBlock codeBlock) {
        getChildren().forEachRemaining(c -> c.startSwitch(codeBlock));
    }
    
    default void stopSwitch(CodeBlock codeBlock) {
        getChildren().forEachRemaining(c -> c.stopSwitch(codeBlock));
    }
    
    default void startBranch(CodeBlock codeBlock) {
        getChildren().forEachRemaining(c -> c.startBranch(codeBlock));
    }
    
    default void stopBranch(CodeBlock codeBlock) {
        getChildren().forEachRemaining(c -> c.stopBranch(codeBlock));
    }

    default void startRepetition(CodeBlock codeBlock) {
        getChildren().forEachRemaining(c -> c.startRepetition(codeBlock));
    }

    default void stopRepetition(CodeBlock codeBlock) {
        getChildren().forEachRemaining(c -> c.stopRepetition(codeBlock));
    }

    Iterator<? extends BlockListener> getChildren();
}
