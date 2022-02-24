package com.github.mhdirkse.countlang.ast;

public interface Call extends CompositeNode {
	// Every Call is also an AstNode. Therefore, getLine() and getColumn() are implemented
	// in every implementation even though these implementations do not themselves have
	// the code.
	int getLine();
	int getColumn();
	String getName();
	void setName(String name);
	FunctionKey getKey();
	int getNumArguments();
	ExpressionNode getArgument(int index);
	void addArgument(ExpressionNode expr);
}
