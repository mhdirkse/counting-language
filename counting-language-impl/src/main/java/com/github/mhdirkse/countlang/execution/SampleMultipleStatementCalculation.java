package com.github.mhdirkse.countlang.execution;

import java.math.BigInteger;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.algorithm.Samplable;
import com.github.mhdirkse.countlang.algorithm.SampleContext;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.ProgramException;
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
        	checkNumSampled();
        } else {
        	distribution = (Distribution) value;
        	if(distribution.getTotal().compareTo(BigInteger.ZERO) <= 0) {
        		throw new ProgramException(getAstNode().getLine(), getAstNode().getColumn(), "Cannot sample from empty distribution");
        	}
        }
    }

    private void checkNumSampled() {
    	if(numSampled.compareTo(BigInteger.ZERO) <= 0) {
    		throw new ProgramException(getAstNode().getLine(), getAstNode().getColumn(), "You have to sample at least one copy from a distribution, got " + numSampled.toString());
    	}
    	if(numSampled.compareTo(Samplable.MAX_NUM_SAMPLED) > 0) {
    		throw new ProgramException(getAstNode().getLine(), getAstNode().getColumn(), "Too many samples from distribution, got " + numSampled.toString());
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
