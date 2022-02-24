/*
 * Copyright Martijn Dirkse 2021
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.mhdirkse.countlang.analysis;

import java.util.Iterator;
import java.util.List;

import com.github.mhdirkse.countlang.ast.FunctionKey;
import com.github.mhdirkse.countlang.ast.NonValueReturnStatement;
import com.github.mhdirkse.countlang.ast.Statement;
import com.github.mhdirkse.countlang.ast.ValueReturnStatement;
import com.github.mhdirkse.countlang.tasks.StatusReporter;
import com.github.mhdirkse.countlang.type.CountlangType;
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

    void startFunction(final int line, final int column, final FunctionKey functionKey) {
        statementHandler = new StatementHandler.Idle();
        CodeBlock newBlock = codeBlocks.peek().createChildForFunction(line, column, functionKey);
        codeBlocks.push(newBlock);
    }

    void startProcedure(final int line, final int column, final FunctionKey functionKey) {
        statementHandler = new StatementHandler.Idle();
        CodeBlock newBlock = codeBlocks.peek().createChildForProcedure(line, column, functionKey);
        codeBlocks.push(newBlock);    	
    }

    void startExperiment(final int line, final int column, final FunctionKey functionKey) {
        statementHandler = new StatementHandler.Idle();
        CodeBlock newBlock = codeBlocks.peek().createChildForExperiment(line, column, functionKey);
        codeBlocks.push(newBlock);        
    }

    void stopFunction() {
        codeBlocks.pop();
        statementHandler = new StatementHandler.Idle();
    }

    void handleReturn(int line, int column, Class<? extends Statement> statementType) {
    	checkIsReturnStatement(statementType);
    	statementHandler = codeBlocks.peek().handleReturn(line, column);
        checkTypeOfReturnStatement(line, column, statementType);
    }

    private void checkIsReturnStatement(Class<? extends Statement> statementType) {
    	boolean isValueReturn = (statementType.isAssignableFrom(ValueReturnStatement.class));
    	boolean isNonValueReturn = (statementType.isAssignableFrom(NonValueReturnStatement.class));
    	if(! (isValueReturn || isNonValueReturn)) {
    		throw new IllegalArgumentException("Expected a return statement");
    	}
    }

	private void checkTypeOfReturnStatement(int line, int column, Class<? extends Statement> returnStatementType) {
		Iterator<CodeBlock> it = codeBlocks.topToBottomIterator();
        while(it.hasNext()) {
        	CodeBlock current = it.next();
        	if(current instanceof RootOrFunctionCodeBlock) {
        		((RootOrFunctionCodeBlock) current).checkReturnStatementType(line, column, returnStatementType);
        		return;
        	}
        }
        throw new IllegalStateException("Every code block should be inside a function or the program root");
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

    void pushScope(AnalysisScope analysisScope) {
        memory.pushScope(analysisScope);
    }

    AnalysisScope popScope() {
        return memory.popScope();
    }

    CountlangType read(String name, int line, int column) {
        return memory.read(name, line, column, codeBlocks.peek());
    }

    void write(String name, int line, int column, CountlangType countlangType, boolean isLoopVariable) {
        memory.write(name, line, column, countlangType, codeBlocks.peek(), isLoopVariable);
    }

    void addParameter(String name, int line, int column, CountlangType countlangType) {
        memory.addParameter(name, line, column, countlangType, codeBlocks.peek());
    }
}
