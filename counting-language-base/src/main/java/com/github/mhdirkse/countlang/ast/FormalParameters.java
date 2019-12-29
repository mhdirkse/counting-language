package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.execution.CountlangType;
import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.Expression;
import com.github.mhdirkse.countlang.execution.ProgramException;

public class FormalParameters extends AstNode implements CompositeNode {
    private List<FormalParameter> formalParameters = new ArrayList<>();

    public FormalParameters(final int line, final int column) {
        super(line, column);
    }

    int size() {
        return formalParameters.size();
    }

    String getFormalParameterName(int i) {
        return formalParameters.get(i).getName();
    }

    CountlangType getFormalParameterType(int i) {
        return formalParameters.get(i).getCountlangType();
    }

    public void addFormalParameter(final FormalParameter formalParameter) {
        formalParameters.add(formalParameter);
    }

    @Override
    public void accept(Visitor v) {
        v.visitFormalParameters(this);
    }

    @Override
    public List<AstNode> getChildren() {
        List<AstNode> result = new ArrayList<>();
        result.addAll(formalParameters);
        return result;
    }

    public void fillNewStackFrame(
            final List<? extends Expression> actualParameters,
            final ExecutionContext ctx) {
        (new ParameterWalker(actualParameters, ctx)).run();
    }

    private class ParameterWalker {
        private final List<? extends Expression> actualParameters;
        private ExecutionContext ctx;

        ParameterWalker(final List<? extends Expression> actualParameters, final ExecutionContext ctx) {
            this.actualParameters = actualParameters;
            this.ctx = ctx;
        }

        void run() {
            checkParameterCount();
            fillStackFrame();
        }

        void checkParameterCount() {
            if (formalParameters.size() != actualParameters.size()) {
                String msg = String.format("In function call expected %d arguments, got %d",
                        formalParameters.size(), actualParameters.size());
                throw new ProgramException(getLine(), getColumn(), msg);
            }
        }

        private void fillStackFrame() {
            for (int i = 0; i < formalParameters.size(); ++i) {
                ctx.putSymbolInNewFrame(
                        formalParameters.get(i).getName(),
                        actualParameters.get(i).calculate(ctx));
            }
        }
    }
}
