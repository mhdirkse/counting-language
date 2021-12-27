package com.github.mhdirkse.countlang.ast;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class SampleMultipleStatement extends AbstractSampleStatement {
	@Getter
	@Setter
	private SimpleLhs lhs;

    @Getter
    @Setter
    private ExpressionNode numSampled;

    public SampleMultipleStatement(int line, int column) {
        super(line, column);
    }

    @Override
    public void accept(Visitor v) {
        v.visitSampleMultipleStatement(this);
    }

    @Override
    public List<AstNode> getChildren() {
        return Arrays.asList(numSampled, getSampledDistribution(), lhs);
    }
}
