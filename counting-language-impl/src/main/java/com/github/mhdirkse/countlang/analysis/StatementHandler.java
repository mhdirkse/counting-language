/*
 * Copyright Martijn Dirkse 2021
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
        private final CodeBlockEvent.Return returnStatement;

        AfterReturn(CodeBlockEvent.Return returnStatement) {
            this.returnStatement = returnStatement;
        }

        @Override
        public StatementHandler handleStatement(int line, int column) {
            returnStatement.setAfter(new CodeBlockEvent.Statement(line, column));
            return new StatementHandler.Idle();
        }
    }
}
