package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public abstract class FunctionDefinitionStatementBase extends Statement implements CompositeNode {
    @Getter
    @Setter
    private String name = null;

    @Getter
    private final FormalParameters formalParameters;

    @Getter
    @Setter
    private StatementGroup statements;

    public abstract CountlangType getReturnType();

    public FunctionDefinitionStatementBase(final int line, final int column) {
        super(line, column);
        formalParameters = new FormalParameters(line, column);
    }

    public int getNumParameters() {
        return formalParameters.size();
    }

    public String getFormalParameterName(int i) {
        return formalParameters.getFormalParameterName(i);
    }

    public CountlangType getFormalParameterType(int i) {
        return formalParameters.getFormalParameterType(i);
    }

    public void addFormalParameter(final String parameterName, final CountlangType countlangType) {
        formalParameters.addFormalParameter(
                new FormalParameter(getLine(), getColumn(), parameterName, countlangType));
    }

    public void addStatement(final Statement statement) {
        statements.addStatement(statement);
    }

    public void addStatements(final List<Statement> statements) {
        for(Statement s: statements) {
            this.statements.addStatement(s);
        }
    }

    @Override
    public List<AstNode> getChildren() {
        List<AstNode> result = new ArrayList<>();
        result.add(formalParameters);
        result.add(statements);
        return result;
    }
}
