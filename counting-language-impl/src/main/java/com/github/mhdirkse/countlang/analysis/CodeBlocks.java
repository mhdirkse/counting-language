package com.github.mhdirkse.countlang.analysis;

import com.github.mhdirkse.countlang.utils.Stack;

class CodeBlocks {
    private final BlockListener memory;
    private Stack<CodeBlock> codeBlocks = new Stack<>();

    CodeBlocks(final BlockListener memory) {
        this.memory = memory;
    }

    void start() {
        codeBlocks.push(new CodeBlock());
    }

    void stop() {
        codeBlocks.pop();
    }

    CodeBlock getLastAddedBlock() {
        return codeBlocks.peek();
    }

    void startSwitch() {
        CodeBlock newBlock = codeBlocks.peek().createChild();
        codeBlocks.push(newBlock);
        memory.startSwitch(newBlock);
    }

    void stopSwitch() {
        CodeBlock stoppedBlock = codeBlocks.pop();
        memory.stopSwitch(stoppedBlock);
    }

    void startBranch() {
        CodeBlock newBlock = codeBlocks.peek().createChild();
        codeBlocks.push(newBlock);
        memory.startBranch(newBlock);
    }

    void stopBranch() {
        CodeBlock stoppedBlock = codeBlocks.pop();
        memory.stopBranch(stoppedBlock);
    }

    void startRepetition() {
        CodeBlock newBlock = codeBlocks.peek().createChild();
        codeBlocks.push(newBlock);
        memory.startRepetition(newBlock);
    }

    void stopRepetition() {
        CodeBlock stoppedBlock = codeBlocks.pop();
        memory.stopRepetition(stoppedBlock);
    }
}
