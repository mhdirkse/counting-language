/*
 * Copyright Martijn Dirkse 2022
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
