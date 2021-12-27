package com.github.mhdirkse.countlang.ast;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractSampleStatement extends Statement implements CompositeNode {
	public AbstractSampleStatement(int line, int column) {
		super(line, column);
	}
	
	@Getter
    @Setter
    private ExpressionNode sampledDistribution;
}
