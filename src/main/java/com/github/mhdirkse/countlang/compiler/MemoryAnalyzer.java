package com.github.mhdirkse.countlang.compiler;

import java.util.HashSet;
import java.util.Set;

import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.Program;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;

class MemoryAnalyzer implements AstNode.Visitor {
    private int numValues = 0;
    private int numSymbols = 0;

    Set<String> symbols = new HashSet<String>();

    int getNumValues() {
        return numValues;
    }

    int getNumVariables() {
        return symbols.size();
    }

    public void run(Program program) {
        program.accept(this);
    }

    @Override
    public void visitProgram(final Program program) {
        for (int i = 0; i < program.getSize(); ++i) {
            program.getStatement(i).accept(this);
        }
    }

    @Override
    public void visitAssignmentStatement(final AssignmentStatement statement) {
        if(! symbols.contains(statement.getLhs().getName())) {
            statement.getLhs().setSeq(numSymbols++);
        }
        symbols.add(statement.getLhs().getName());
        statement.getRhs().accept(this);
    }

    @Override
    public void visitPrintStatement(final PrintStatement statement) {
        statement.getExpression().accept(this);
    }

    @Override
    public void visitCompositeExpression(final CompositeExpression expression) {
        for(int i = 0; i < expression.getNumSubExpressions(); ++i) {
            expression.getSubExpression(i).accept(this);
        }
    }

    @Override
    public void visitSymbolExpression(final SymbolExpression expression) {
    }

    @Override
    public void visitValueExpression(final ValueExpression expression) {
        expression.getValue().setSeq(numValues++);
    }
}
