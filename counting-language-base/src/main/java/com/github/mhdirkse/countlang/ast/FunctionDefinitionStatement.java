package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.Expression;
import com.github.mhdirkse.countlang.execution.ProgramRuntimeException;
import com.github.mhdirkse.countlang.execution.RunnableFunction;
import com.github.mhdirkse.countlang.execution.StackFrame;
import com.github.mhdirkse.countlang.execution.Value;

import lombok.Getter;
import lombok.Setter;

public class FunctionDefinitionStatement extends Statement implements RunnableFunction, CompositeNode {
    @Getter
    @Setter
    private String name = null;

    private final FormalParameters formalParameters;
    private List<Statement> statements = new ArrayList<Statement>();

    public FunctionDefinitionStatement(final int line, final int column) {
        super(line, column);
        formalParameters = new FormalParameters(line, column);
    }

    public void addFormalParameter(final String parameterName) {
        formalParameters.addFormalParameter(
                new FormalParameter(getLine(), getColumn(), parameterName));
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

    @Override
    public List<AstNode> getChildren() {
        List<AstNode> result = new ArrayList<>();
        result.add(formalParameters);
        result.addAll(statements);
        return result;
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
        void onNormalStatement(final Statement statement);
        void onReturnStatement(final ReturnStatement statement);
        void onStatementWithoutEffect(final Statement statement);
        void onNoReturn();
    }

    private class FunctionRun implements Callback {
        private ExecutionContext ctx;
        private List<? extends Expression> actualParameters;

        private Value result = null;
 
        FunctionRun(final List<? extends Expression> actualParameters, final ExecutionContext ctx) {
            this.actualParameters = actualParameters;
            this.ctx = ctx;
        }

        public Value run() {
            StackFrame stackFrame = formalParameters.checkedGetStackFrame(actualParameters, ctx);
            StatementWalker statementWalker = new StatementWalker(statements);
            ctx.pushFrame(stackFrame);
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
            throw new ProgramRuntimeException(
                    statement.getLine(), statement.getColumn(), "Statement has no effect");
        }

        @Override
        public void onNoReturn() {
            throwError("No return statement in function");
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
            for(int i = 0; (i < statements.size()) && (!didReturn); ++i) {
                handleStatement(i, statements.get(i));
            }
        }

        private void handleStatement(final int index, final Statement statement) {
            if (!(statements.get(index) instanceof ReturnStatement)) {
                callback.onNormalStatement(statements.get(index));
            } else {
                handleReturnStatement(index, (ReturnStatement) statements.get(index));
            }
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
