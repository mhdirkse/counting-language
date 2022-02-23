package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

public class ProcedureCallStatement extends Statement implements Call {
    private String name;

    private List<ExpressionNode> arguments = new ArrayList<>();

	public ProcedureCallStatement(int line, int column) {
		super(line, column);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

    @Override
    public FunctionKey getKey() {
        return new FunctionKey(getName());
    }

	@Override
    public int getNumArguments() {
        return arguments.size();
    }

	@Override
    public ExpressionNode getArgument(int i) {
        return arguments.get(i);
    }

    @Override
	public void addArgument(final ExpressionNode expression) {
        arguments.add(expression);
    }

    @Override
    public List<AstNode> getChildren() {
        List<AstNode> result = new ArrayList<>();
        result.addAll(arguments);
        return result;
    }

	@Override
	public void accept(Visitor v) {
		v.visitProcedureCallStatement(this);
	}
}
