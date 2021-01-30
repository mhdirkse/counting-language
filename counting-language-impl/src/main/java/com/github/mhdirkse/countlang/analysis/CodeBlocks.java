package com.github.mhdirkse.countlang.analysis;

import java.util.List;

import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.utils.Stack;

class CodeBlocks {
    private final Memory memory;
    private Stack<CodeBlock> codeBlocks = new Stack<>();
    private StatementHandler statementHandler = null;

    CodeBlocks(final Memory memory) {
        this.memory = memory;
    }

    void start() {
        codeBlocks.push(new CodeBlockRoot());
        statementHandler = new StatementHandler.Idle();
    }

    void stop() {
        codeBlocks.pop();
    }

    // Only for testing purposes
    CodeBlock getLastAddedBlock() {
        return codeBlocks.peek();
    }

    void startSwitch() {
        CodeBlock newBlock = codeBlocks.peek().createChildForSwitch();
        codeBlocks.push(newBlock);
        memory.startSwitch(newBlock);
    }

    void stopSwitch() {
        memory.stopSwitch(commonStop());
    }

    private CodeBlock commonStop() {
        CodeBlock stoppedBlock = codeBlocks.pop();
        statementHandler = new StatementHandler.AfterBlock(stoppedBlock);
        return stoppedBlock;
    }

    void startBranch() {
        CodeBlock newBlock = codeBlocks.peek().createChildForBranch();
        codeBlocks.push(newBlock);
        memory.startBranch(newBlock);
    }

    void stopBranch() {
        memory.stopBranch(commonStop());
    }

    void startRepetition() {
        CodeBlock newBlock = codeBlocks.peek().createChildForRepetition();
        codeBlocks.push(newBlock);
        memory.startRepetition(newBlock);
    }

    void stopRepetition() {
        memory.stopRepetition(commonStop());
    }

    void handleReturn(int line, int column) {
        statementHandler = codeBlocks.peek().handleReturn(line, column);
    }

    void handleStatement(int line, int column) {
        statementHandler = statementHandler.handleStatement(line, column);
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
