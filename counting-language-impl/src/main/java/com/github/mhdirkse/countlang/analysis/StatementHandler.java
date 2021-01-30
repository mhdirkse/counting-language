package com.github.mhdirkse.countlang.analysis;

interface StatementHandler {
    StatementHandler handleStatement(int line, int column);

    static class Idle implements StatementHandler {
        @Override
        public StatementHandler handleStatement(int line, int column) {
            return this;
        }
    }

    static class AfterBlock implements StatementHandler {
        private final CodeBlock codeBlock;

        AfterBlock(CodeBlock codeBlock) {
            this.codeBlock = codeBlock;
        }

        @Override
        public StatementHandler handleStatement(int line, int column) {
            codeBlock.handleStatementAfterStopped(line, column);
            return new StatementHandler.Idle();
        }
    }

    static class AfterReturn implements StatementHandler {
        private final CodeBlockEvents.Return returnStatement;

        AfterReturn(CodeBlockEvents.Return returnStatement) {
            this.returnStatement = returnStatement;
        }

        @Override
        public StatementHandler handleStatement(int line, int column) {
            returnStatement.setAfter(new CodeBlockEvents.Statement(line, column));
            return new StatementHandler.Idle();
        }
    }
}
