package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.execution.CountlangType;
import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.Expression;
import com.github.mhdirkse.countlang.execution.ProgramException;
import com.github.mhdirkse.countlang.execution.ReturnHandler;
import com.github.mhdirkse.countlang.execution.RunnableFunction;
import com.github.mhdirkse.countlang.execution.StackFrameAccess;

import lombok.Getter;
import lombok.Setter;

public class FunctionDefinitionStatement extends Statement implements RunnableFunction, CompositeNode {
    @Getter
    @Setter
    private String name = null;

    @Getter
    @Setter
    private CountlangType returnType = CountlangType.UNKNOWN;

    @Getter
    private final FormalParameters formalParameters;

    @Getter
    @Setter
    private StatementGroup statements;

    public FunctionDefinitionStatement(final int line, final int column) {
        super(line, column);
        formalParameters = new FormalParameters(line, column);
    }

    public int getNumParameters() {
        return formalParameters.size();
    }

    @Override
    public String getFormalParameterName(int i) {
        return formalParameters.getFormalParameterName(i);
    }

    @Override
    public CountlangType getFormalParameterType(int i) {
        return formalParameters.getFormalParameterType(i);
    }

    public void addFormalParameter(final String parameterName, final CountlangType countlangType) {
        formalParameters.addFormalParameter(
                new FormalParameter(getLine(), getColumn(), parameterName, countlangType));
    }

    public void addStatement(final Statement statement) {
        statements.addStatement(statement);
    }

    public void addStatements(final List<Statement> statements) {
        for(Statement s: statements) {
            this.statements.addStatement(s);
        }
    }

    @Override
    public void accept(final Visitor v) {
        v.visitFunctionDefinitionStatement(this);
    }

    @Override
    public List<AstNode> getChildren() {
        List<AstNode> result = new ArrayList<>();
        result.add(formalParameters);
        result.add(statements);
        return result;
    }

    public void execute(final ExecutionContext ctx, final ReturnHandler returnHandler) {
        ctx.putFunction(this);
    }

    @Override
    public Object runFunction(
            final List<? extends Expression> actualParameters,
            final ExecutionContext ctx) {
        return new FunctionRun(statements, actualParameters, ctx).run();
    }

    void throwError(final String message) {
        throw new ProgramException(getLine(), getColumn(), message);
    }

    private class FunctionRun implements ReturnHandler {
        private ExecutionContext ctx;
        private List<? extends Expression> actualParameters;
        private final StatementGroup statements;

        private Object result = null;
 
        FunctionRun(final StatementGroup statements, final List<? extends Expression> actualParameters, final ExecutionContext ctx) {
            this.statements = statements;
            this.actualParameters = actualParameters;
            this.ctx = ctx;
        }

        public Object run() {
        	ctx.startPreparingNewFrame(StackFrameAccess.HIDE_PARENT);
            formalParameters.fillNewStackFrame(actualParameters, ctx);
            ctx.pushNewFrame();
            statements.execute(ctx, this);
            ctx.popFrame();
            return result;
        }

        @Override
        public void handleReturnValue(final Object value, int line, int column) {
            if(result != null) {
                throw new ProgramException(
                        line, column, String.format("Extra return statement encountered, value returned is %s", value.toString()));
            }
            else {
                result = value;
            }
        }
    }
}
