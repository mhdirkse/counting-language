package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.ProgramException;
import com.github.mhdirkse.countlang.execution.Value;

public class FunctionCallExpression extends ExpressionNode implements CompositeNode {
    private String functionName = null;
    private List<ExpressionNode> arguments = new ArrayList<>();

    public FunctionCallExpression(final int line, final int column) {
        super(line, column);
    }

    public void setFunctionName(final String functionName) {
        this.functionName = functionName;
    }

    public void addArgument(final ExpressionNode expression) {
        arguments.add(expression);
    }

    @Override
    public Value calculate(final ExecutionContext ctx) {
        if (ctx.hasFunction(functionName)) {
            return ctx.getFunction(functionName).runFunction(arguments, ctx);
        } else {
            throw new ProgramException(getLine(), getColumn(),
                    String.format("Function not found: %s", functionName));
        }
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
