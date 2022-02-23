package com.github.mhdirkse.countlang.ast;

public interface Call extends CompositeNode {
	String getName();
	void setName(String name);
	FunctionKey getKey();
	int getNumArguments();
	ExpressionNode getArgument(int index);
	void addArgument(ExpressionNode expr);
}
