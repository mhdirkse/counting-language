package com.github.mhdirkse.countlang.execution;

import java.math.BigInteger;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.algorithm.Samplable;
import com.github.mhdirkse.countlang.algorithm.SampleContext;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.SampleMultipleStatement;

class SampleMultipleStatementCalculation extends AbstractSampleStatementCalculation {
	private BigInteger numSampled;

	SampleMultipleStatementCalculation(SampleMultipleStatement statement, SampleContext sampleContext) {
		super(statement, sampleContext);
	}

    @Override
    public void acceptChildResult(Object value, ExecutionContext context) {
        if(numSampled == null) {
        	numSampled = (BigInteger) value;
        } else {
        	distribution = (Distribution) value;
        }
    }

	@Override
	void startSampledVariable() {
		Samplable samplable = Samplable.multiple(distribution, numSampled);
		sampleContext.startSampledVariable(((AstNode) statement).getLine(), ((AstNode) statement).getColumn(), samplable);
	}

	@Override
	public AstNodeExecution fork() {
		return new SampleMultipleStatementCalculationForked(this);
	}
}
