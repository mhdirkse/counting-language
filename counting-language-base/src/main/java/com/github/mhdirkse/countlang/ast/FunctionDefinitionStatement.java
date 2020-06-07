package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.execution.CountlangType;
import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.Expression;
import com.github.mhdirkse.countlang.execution.ProgramException;
import com.github.mhdirkse.countlang.execution.RunnableFunction;

import lombok.Getter;
import lombok.Setter;

public class FunctionDefinitionStatement extends Statement implements RunnableFunction, CompositeNode {
    @Getter
    @Setter
    private String name = null;

    @Getter
    @Setter
    private CountlangType returnType = CountlangType.UNKNOWN;

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

    public void execute(final ExecutionContext ctx) {
        ctx.putFunction(this);
    }

    @Override
    public Object runFunction(
            final List<? extends Expression> actualParameters,
            final ExecutionContext ctx) {
        return new FunctionRun(actualParameters, ctx).run();
    }

    void throwError(final String message) {
        throw new ProgramException(getLine(), getColumn(), message);
    }

    private interface Callback {
        void onNormalStatement(final Statement statement);
        void onReturnStatement(final ReturnStatement statement);
        void onStatementWithoutEffect(final Statement statement);
        void onNoReturn();
    }

    private class FunctionRun implements Callback {
        private ExecutionContext ctx;
        private List<? extends Expression> actualParameters;

        private Object result = null;
 
        FunctionRun(final List<? extends Expression> actualParameters, final ExecutionContext ctx) {
            this.actualParameters = actualParameters;
            this.ctx = ctx;
        }

        public Object run() {
        	ctx.startPreparingNewFrame();
            formalParameters.fillNewStackFrame(actualParameters, ctx);
            StatementWalker statementWalker = new StatementWalker(statements);
            ctx.pushNewFrame();
            statementWalker.run(this);
            ctx.popFrame();
            return result;
        }

        @Override
        public void onNormalStatement(final Statement statement) {
            statement.execute(ctx);
        }

        @Override
        public void onReturnStatement(final ReturnStatement returnStatement) {
            result = returnStatement.getExpression().calculate(ctx);
        }

        @Override
        public void onStatementWithoutEffect(final Statement statement) {
            throw new ProgramException(
                    statement.getLine(), statement.getColumn(), "Statement has no effect");
        }

        @Override
        public void onNoReturn() {
            throwError("No return statement in function");
        }
    }

    private static class StatementWalker {
        private final StatementGroup statements;
        private Callback callback = null;
        private boolean didReturn = false;

        StatementWalker(final StatementGroup statements) {
            this.statements = statements;
        }

        void run(final Callback callbackInput) {
            this.callback = callbackInput;
            iterateStatements();
            if (!didReturn) {
                callback.onNoReturn();
            }
        }

        private void iterateStatements() {
            for(int i = 0; (i < statements.getChildren().size()) && (!didReturn); ++i) {
                handleStatement(i, statements.getStatement(i));
            }
        }

        private void handleStatement(final int index, final Statement statement) {
            if (!(statements.getStatement(index) instanceof ReturnStatement)) {
                callback.onNormalStatement(statements.getStatement(index));
            } else {
                handleReturnStatement(index, (ReturnStatement) statements.getStatement(index));
            }
        }

        private void handleReturnStatement(final int index, final ReturnStatement statement) {
            callback.onReturnStatement(statement);
            didReturn = true;
            checkForStatementsWithoutEffect(index);
        }

        private void checkForStatementsWithoutEffect(final int i) {
            if (i < (statements.getChildren().size() - 1)) {
                callback.onStatementWithoutEffect(statements.getStatement(i+1));
            }
        }
    }
}
