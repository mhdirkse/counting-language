package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.Expression;
import com.github.mhdirkse.countlang.execution.ProgramRuntimeException;
import com.github.mhdirkse.countlang.execution.RunnableFunction;
import com.github.mhdirkse.countlang.execution.StackFrame;
import com.github.mhdirkse.countlang.execution.Value;

public class FunctionDefinitionStatement extends Statement implements RunnableFunction {
    private String name = null;
    private List<String> formalParameters = new ArrayList<String>();
    private List<Statement> statements = new ArrayList<Statement>();

    public FunctionDefinitionStatement(final int line, final int column) {
        super(line, column);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void addFormalParameter(final String parameterName) {
        formalParameters.add(parameterName);
    }

    public void addStatement(final Statement statement) {
        statements.add(statement);
    }

    public void addStatements(final List<Statement> statements) {
        this.statements.addAll(statements);
    }

    @Override
    public void accept(final AstNode.Visitor v) {
        v.visitFunctionDefinitionStatement(this);
    }

    public void execute(final ExecutionContext ctx) {
        ctx.putFunction(this);
    }

    @Override
    public Value runFunction(
            final List<? extends Expression> actualParameters,
            final ExecutionContext ctx) {
        return new FunctionRun(actualParameters, ctx).run();
    }

    void throwError(final String message) {
        throw new ProgramRuntimeException(getLine(), getColumn(), message);
    }

    private interface Callback {
        void onParameter(final String parameterName, final Expression actualParameter);
        void onInvalidParameterCount(final int numFormal, final int numActual);
        void onNormalStatement(final Statement statement);
        void onReturnStatement(final ReturnStatement statement);
        void onStatementWithoutEffect(final Statement statement);
        void onNoReturn();
    }

    private class FunctionRun implements Callback {
        private ExecutionContext ctx;
        private List<? extends Expression> actualParameters;

        private StackFrame frame = new StackFrame();
        private Value result = null;
 
        FunctionRun(final List<? extends Expression> actualParameters, final ExecutionContext ctx) {
            this.actualParameters = actualParameters;
            this.ctx = ctx;
        }

        public Value run() {
            ParameterWalker parameterWalker = new ParameterWalker(formalParameters, actualParameters);
            parameterWalker.checkParameterCount(this);
            parameterWalker.walk(this);
            StatementWalker statementWalker = new StatementWalker(statements);
            ctx.pushFrame(frame);
            statementWalker.run(this);
            ctx.popFrame();
            return result;
        }

        @Override
        public void onParameter(String parameterName, Expression actualParameter) {
            frame.putSymbol(parameterName, actualParameter.calculate(ctx));
        }

        @Override
        public void onInvalidParameterCount(int numFormal, int numActual) {
            throwError(String.format("In function call expected %d arguments, got %d", numFormal, numActual));
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
            throw new ProgramRuntimeException(
                    statement.getLine(), statement.getColumn(), "Statement has no effect");
        }

        @Override
        public void onNoReturn() {
            throwError("No return statement in function");
        }
    }

    private static class ParameterWalker {
        private final List<String> formalParameters;
        private final List<? extends Expression> actualParameters;

        ParameterWalker(final List<String> formalParameters, final List<? extends Expression> actualParameters) {
            this.formalParameters = formalParameters;
            this.actualParameters = actualParameters;
        }

        void checkParameterCount(final Callback callback) {
            if (formalParameters.size() != actualParameters.size()) {
                callback.onInvalidParameterCount(formalParameters.size(), actualParameters.size());
            }
        }

        void walk(final Callback callback) {
            for (int i = 0; i < formalParameters.size(); ++i) {
                callback.onParameter(formalParameters.get(i), actualParameters.get(i));
            }
        }
    }

    private static class StatementWalker {
        private final List<Statement> statements;
        private Callback callback = null;
        private boolean didReturn = false;

        StatementWalker(final List<Statement> statements) {
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
            for(int i = 0; i < statements.size(); ++i) {
                if (handleStatement(i, statements.get(i))) {
                    break;
                }
            }
        }

        private boolean handleStatement(final int index, final Statement statement) {
            boolean stop = false;
            if (!(statements.get(index) instanceof ReturnStatement)) {
                callback.onNormalStatement(statements.get(index));
            } else {
                handleReturnStatement(index, (ReturnStatement) statements.get(index));
                stop = true;
            }
            return stop;
        }

        private void handleReturnStatement(final int index, final ReturnStatement statement) {
            callback.onReturnStatement(statement);
            didReturn = true;
            checkForStatementsWithoutEffect(index);
        }

        private void checkForStatementsWithoutEffect(final int i) {
            if (i < (statements.size() - 1)) {
                callback.onStatementWithoutEffect(statements.get(i+1));
            }
        }
    }
}
