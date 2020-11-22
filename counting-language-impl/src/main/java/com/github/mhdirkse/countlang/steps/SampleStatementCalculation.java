package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.AFTER;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.RUNNING;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.SampleStatement;
import com.github.mhdirkse.countlang.types.Distribution;

class SampleStatementCalculation implements AstNodeExecution {
    SubExpressionStepper delegate;
    final SampleStatement statement;
    Distribution distribution;
    boolean isSamplingStarted = false;
    int value;

    SampleStatementCalculation(SampleStatement statement) {
        this.statement = statement;
        this.delegate = new SubExpressionStepper(statement.getSubExpressions());
    }

    @Override
    public AstNode getAstNode() {
        return statement;
    }

    @Override
    public final AstNode step(ExecutionContext context) {
        if(distribution == null) {
            AstNode result = delegate.step(context);
            if(result != null) {
                return result;
            }
            if(! delegate.isDone()) {
                return null;
            } else {
                distribution = (Distribution) delegate.getSubExpressionResults().get(0);
            }
        }
        if(! isSamplingStarted) {
            context.startSampledVariable(distribution);
        }
        if(context.hasNextValue()) {
            value = context.nextValue();
            context.forkExecutor();
            return null;
        }
        context.stopSampledVariable();
        context.stopExecutor();
        return null;
    }

    @Override
    public AstNodeExecutionState getState() {
        AstNodeExecutionState delegateState = delegate.getState();
        if(delegateState == AFTER) {
            return RUNNING;
        } else {
            return delegateState;
        }
    }

    @Override
    public AstNodeExecution fork() {
        return new SampleStatementCalculationForked(this);
    }
}
