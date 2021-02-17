package com.github.mhdirkse.countlang.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.mhdirkse.countlang.tasks.StatusCode;
import com.github.mhdirkse.countlang.tasks.StatusReporter;

import lombok.Getter;

abstract class CodeBlock {
    private final CodeBlock parent;
    private final List<CodeBlock> children = new ArrayList<>();

    final List<CodeBlock> getChildren() {
        return Collections.unmodifiableList(children);
    }

    final List<CodeBlock> getNonSubfunctionChildren() {
        return children.stream().filter(c -> ! c.isRootOrFunction()).collect(Collectors.toList());
    }

    private final Set<CodeBlock> descendants = new HashSet<>();

    final Set<CodeBlock> getDescendants() {
        return Collections.unmodifiableSet(descendants);
    }

    private List<CodeBlockEvent.Return> returnStatements = new ArrayList<>();

    final List<CodeBlockEvent.Return> getReturnStatements() {
        return Collections.unmodifiableList(returnStatements);
    }

    private @Getter CodeBlockEvent.Statement statementAfter = null;

    private final List<VariableWrite> variableWrites = new ArrayList<>();

    final List<VariableWrite> getVariableWrites() {
        return Collections.unmodifiableList(variableWrites);
    }

    CodeBlock() {
        parent = null;
    }

    final CodeBlock createChildForSwitch() {
        CodeBlock result = new CodeBlockParallel(this);
        registerChild(result);
        return result;
    }

    final CodeBlock createChildForBranch() {
        CodeBlock result = new CodeBlockSerialPart(this);
        registerChild(result);
        return result;
    }

    final CodeBlock createChildForRepetition() {
        CodeBlock result = new CodeBlockRepeated(this);
        registerChild(result);
        return result;
    }

    final CodeBlock createChildForFunction(final int line, final int column, final String functionName) {
        CodeBlock result = new CodeBlockFunctionBase.Function(this, line, column, functionName);
        registerChild(result);
        return result;
    }

    final CodeBlock createChildForExperiment(final int line, final int column, final String functionName) {
        CodeBlock result = new CodeBlockFunctionBase.Experiment(this, line, column, functionName);
        registerChild(result);
        return result;        
    }

    final private void registerChild(CodeBlock child) {
        children.add(child);
        this.addDescendant(child);
    }

    // Only use in derived classes. From outside use the createChildForXxx methods.
    CodeBlock(CodeBlock parent) {
        this.parent = parent;
    }

    final private void addDescendant(CodeBlock descendant) {
        descendants.add(descendant);
        if(parent != null) {
            parent.addDescendant(descendant);
        }
    }

    final void addVariableWrite(VariableWrite variableWrite) {
        variableWrites.add(variableWrite);
    }

    final boolean contains(CodeBlock other) {
        return (this == other) || descendants.contains(other);
    }

    /**
     * @throws IllegalArgumentException When a statement appears unexpectedly after the block has ended.
     */
    void handleStatementAfterStopped(int line, int column) {
        statementAfter = new CodeBlockEvent.Statement(line, column);
    }

    /**
     * @throws IllegalStateException When a return statement appears in a code block it is not expected in.
     */
    StatementHandler handleReturn(int line, int column) {
        CodeBlockEvent.Return returnStatement = new CodeBlockEvent.Return(line, column);
        returnStatements.add(returnStatement);
        return new StatementHandler.AfterReturn(returnStatement);
    }

    /**
     * @throws IllegalStateException When a CodeBlockParallel does not have children for branches, which
     * would be a programming error.
     */
    final ReturnStatus getReturnStatus() {
        ReturnStatus result = getSpecificReturnStatus();
        if((result == ReturnStatus.WEAK_ALL_RETURN) && (getStatementAfter() != null)) {
            result = ReturnStatus.SOME_RETURN;
        }
        return result;
    }

    void reportStatementHasNoEffect(final StatusReporter reporter, String functionName) {
        returnStatements.stream()
            .filter(ret -> ret.getAfter() != null)
            .forEach(ret -> reporter.report(StatusCode.FUNCTION_STATEMENT_WITHOUT_EFFECT, ret.getAfter().getLine(), ret.getAfter().getColumn(), functionName));
        if((getReturnStatus() == ReturnStatus.STRONG_ALL_RETURN) && (getStatementAfter() != null)) {
            reporter.report(StatusCode.FUNCTION_STATEMENT_WITHOUT_EFFECT, getStatementAfter().getLine(), getStatementAfter().getColumn(), functionName);
        }
    }

    abstract ReturnStatus getSpecificReturnStatus();
    abstract boolean isRootOrFunction();
}
