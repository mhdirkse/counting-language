package com.github.mhdirkse.countlang.execution;

import com.github.mhdirkse.countlang.ast.SampleMultipleStatement;

class SampleMultipleStatementCalculationForked extends AbstractSampleStatementCalculationForked {
	SampleMultipleStatementCalculationForked(SampleMultipleStatementCalculation orig) {
		super(orig);
	}

	@Override
	void assign(ExecutionContext context) {
		SampleMultipleStatement asSampleMultipleStatement = (SampleMultipleStatement) sampleStatement;
		context.writeSymbol(asSampleMultipleStatement.getLhs().getSymbol(), value, asSampleMultipleStatement.getSampledDistribution());
	}
}
