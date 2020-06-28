package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class FunctionCallExpression extends ExpressionNode implements CompositeNode {
    @Getter
    @Setter
    private String functionName = null;

    @Getter
    @Setter
    private CountlangType countlangType;
    
    private List<ExpressionNode> arguments = new ArrayList<>();

    public FunctionCallExpression(final int line, final int column) {
        super(line, column);
    }

    public int getNumArguments() {
        return arguments.size();
    }

    public ExpressionNode getArgument(int i) {
        return arguments.get(i);
    }

    public void addArgument(final ExpressionNode expression) {
        arguments.add(expression);
    }

    @Override
    public void accept(final Visitor v) {
        v.visitFunctionCallExpression(this);
    }

    @Override
    public List<AstNode> getChildren() {
        List<AstNode> result = new ArrayList<>();
        result.addAll(arguments);
        return result;
    }
}
