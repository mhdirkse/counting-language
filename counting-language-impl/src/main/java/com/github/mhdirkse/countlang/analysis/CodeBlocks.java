package com.github.mhdirkse.countlang.analysis;

import java.util.List;

import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.tasks.StatusReporter;
import com.github.mhdirkse.countlang.utils.Stack;

class CodeBlocks {
    private final Memory memory;
    private Stack<CodeBlock> codeBlocks = new Stack<>();
    private CodeBlockRoot rootBlock;
    private StatementHandler statementHandler = null;

    CodeBlocks(final Memory memory) {
        this.memory = memory;
    }

    void start() {
        rootBlock = new CodeBlockRoot();
        codeBlocks.push(rootBlock);
        statementHandler = new StatementHandler.Idle();
    }

    void stop() {
        codeBlocks.pop();
    }

    boolean isAtRootLevel() {
        return memory.isAtRootScope() && (codeBlocks.size() == 1);
    }

    // Only for testing purposes
    CodeBlock getLastAddedBlock() {
        return codeBlocks.peek();
    }

    void startSwitch() {
        statementHandler = new StatementHandler.Idle();
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
        statementHandler = new StatementHandler.Idle();
        CodeBlock newBlock = codeBlocks.peek().createChildForBranch();
        codeBlocks.push(newBlock);
        memory.startBranch(newBlock);
    }

    void stopBranch() {
        memory.stopBranch(commonStop());
    }

    void startRepetition() {
        statementHandler = new StatementHandler.Idle();
        CodeBlock newBlock = codeBlocks.peek().createChildForRepetition();
        codeBlocks.push(newBlock);
        memory.startRepetition(newBlock);
    }

    void stopRepetition() {
        memory.stopRepetition(commonStop());
    }

    void startFunction(final int line, final int column, final String functionName) {
        statementHandler = new StatementHandler.Idle();
        CodeBlock newBlock = codeBlocks.peek().createChildForFunction(line, column, functionName);
        codeBlocks.push(newBlock);
    }

    void startExperiment(final int line, final int column, final String functionName) {
        statementHandler = new StatementHandler.Idle();
        CodeBlock newBlock = codeBlocks.peek().createChildForExperiment(line, column, functionName);
        codeBlocks.push(newBlock);        
    }

    void stopFunction() {
        codeBlocks.pop();
        statementHandler = new StatementHandler.Idle();
    }

    void handleReturn(int line, int column) {
        statementHandler = codeBlocks.peek().handleReturn(line, column);
    }

    void handleStatement(int line, int column) {
        statementHandler = statementHandler.handleStatement(line, column);
    }

    // Only for testing purposes
    List<VariableErrorEvent> getVariableErrorEvents() {
        return memory.getVariableErrorEvents();
    }

    void report(StatusReporter reporter) {
        getVariableErrorEvents().forEach(ev -> ev.report(reporter));
        rootBlock.report(reporter);
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
