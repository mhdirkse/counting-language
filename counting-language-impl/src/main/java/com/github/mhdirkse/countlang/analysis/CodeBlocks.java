package com.github.mhdirkse.countlang.analysis;

import java.util.List;

import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.utils.Stack;

class CodeBlocks {
    private final Memory memory;
    private Stack<CodeBlock> codeBlocks = new Stack<>();

    CodeBlocks(final Memory memory) {
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

    List<VariableErrorEvent> getVariableErrorEvents() {
        return memory.getVariableErrorEvents();
    }

    void pushScope(Scope scope) {
        memory.pushScope(scope);
    }

    Scope popScope() {
        return memory.popScope();
    }

    CountlangType read(String name, int line, int column) {
        return memory.read(name, line, column, codeBlocks.peek());
    }

    void write(String name, int line, int column, CountlangType countlangType) {
        memory.write(name, line, column, countlangType, codeBlocks.peek());
    }

    void addParameter(String name, int line, int column, CountlangType countlangType) {
        memory.addParameter(name, line, column, countlangType, codeBlocks.peek());
    }
}
