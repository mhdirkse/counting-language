package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.ReturnHandler;
import com.github.mhdirkse.countlang.execution.StackFrameAccess;

import lombok.Getter;

public final class StatementGroup extends Statement implements CompositeNode {
    public enum StackStrategy {
        NO_NEW_FRAME(new StackStrategyNoNewFrame()),
        NEW_FRAME_SHOWING_PARENT(new StackStrategyNewFrame(StackFrameAccess.SHOW_PARENT)),
        NEW_FRAME_HIDING_PARENT(new StackStrategyNewFrame(StackFrameAccess.HIDE_PARENT));
        private AbstractStackStrategy stackStrategy;
        
        StackStrategy(final AbstractStackStrategy stackStrategy) {
            this.stackStrategy = stackStrategy;
        }

        public void before(final ExecutionContext ctx) {
            stackStrategy.before(ctx);
        }

        public void after(final ExecutionContext ctx) {
            stackStrategy.after(ctx);
        }

        public StackFrameAccess getStackFrameAccess() {
            return stackStrategy.getStackFrameAccess();
        }
    }

    private interface AbstractStackStrategy {
        void before(final ExecutionContext ctx);
        void after(final ExecutionContext ctx);
        StackFrameAccess getStackFrameAccess();
    }
    
    private static final class StackStrategyNoNewFrame implements AbstractStackStrategy {
        @Override
        public void before(final ExecutionContext ctx) {}
        
        @Override
        public void after(final ExecutionContext ctx) {}

        @Override
        public StackFrameAccess getStackFrameAccess() {
            throw new UnsupportedOperationException();
        }
    }

    private static final class StackStrategyNewFrame implements AbstractStackStrategy {
        private final StackFrameAccess stackFrameAccess;

        StackStrategyNewFrame(StackFrameAccess stackFrameAccess) {
            this.stackFrameAccess = stackFrameAccess;
        }
        
        @Override
        public void before(final ExecutionContext ctx) {
            ctx.startPreparingNewFrame(stackFrameAccess);
            ctx.pushNewFrame();
        }

        @Override
        public void after(final ExecutionContext ctx) {
            ctx.popFrame();
        }

        @Override
        public StackFrameAccess getStackFrameAccess() {
            return stackFrameAccess;
        }
    }

    private final StackStrategy stackStrategy;

    private List<Statement> statements = new ArrayList<Statement>();

    public StatementGroup(final StackStrategy stackStrategy, final int line, final int column) {
        super(line, column);
        this.stackStrategy = stackStrategy;
    }

    public Statement getStatement(final int index) {
        return statements.get(index);
    }

    public void addStatement(final Statement statement) {
        statements.add(statement);
    }

    public int getSize() {
        return statements.size();
    }

    public StackStrategy getStackStrategy() {
        return stackStrategy;
    }

    public void execute(final ExecutionContext ctx, final ReturnHandler returnHandler) {       
        stackStrategy.before(ctx);
        try {
            for(Statement statement : statements) {
                statement.execute(ctx, returnHandler);
            }
        }
        finally {
            stackStrategy.after(ctx);
        }
    }

    @Override
    public void accept(final Visitor v) {
        v.visitStatementGroup(this);
    }

    @Override
    public List<AstNode> getChildren() {
        List<AstNode> result = new ArrayList<>();
        result.addAll(statements);
        return result;
    }
}
