package com.github.mhdirkse.countlang.analysis;

import java.util.Iterator;

interface BlockListener {
    default void startSwitch() {
        getChildren().forEachRemaining(BlockListener::startSwitch);
    }
    
    default void stopSwitch() {
        getChildren().forEachRemaining(BlockListener::stopSwitch);
    }
    
    default void startBranch() {
        getChildren().forEachRemaining(BlockListener::startBranch);
    }
    
    default void stopBranch() {
        getChildren().forEachRemaining(BlockListener::stopBranch);
    }

    default void startRepetition(CodeBlock codeBlock) {
        getChildren().forEachRemaining(c -> c.startRepetition(codeBlock));
    }

    default void stopRepetition() {
        getChildren().forEachRemaining(BlockListener::stopRepetition);
    }

    Iterator<? extends BlockListener> getChildren();
}
