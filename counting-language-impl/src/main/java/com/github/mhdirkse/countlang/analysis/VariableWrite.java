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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.tasks.StatusCode;
import com.github.mhdirkse.countlang.tasks.StatusReporter;

import lombok.Getter;

class VariableWrite {
    private final @Getter Variable variable;
    private final @Getter int line;
    private final @Getter int column;
    private final @Getter CountlangType countlangType;
    private final @Getter VariableWriteKind variableWriteKind;
    private final @Getter CodeBlock codeBlock;
    private final @Getter boolean initial;

    private final Set<CodeBlock> readBy = new HashSet<>();

    Set<CodeBlock> getReadBy() {
        return Collections.unmodifiableSet(readBy);
    }

    private final Set<CodeBlock> overwrittenBy = new HashSet<>();

    VariableWrite(Variable variable, int line, int column, CountlangType countlangType, VariableWriteKind variableWriteKind, CodeBlock codeBlock, boolean initial) {
        this.variable = variable;
        this.line = line;
        this.column = column;
        this.countlangType = countlangType;
        this.variableWriteKind = variableWriteKind;
        this.codeBlock = codeBlock;
        this.initial = initial;
    }

    void read(CodeBlock codeBlock) {
        readBy.add(codeBlock);
    }

    void overwrite(VariableWrite overwritingWrite) {
        overwrittenBy.add(overwritingWrite.getCodeBlock());
    }

    boolean isRead() {
        return ! readBy.isEmpty();
    }

    boolean isOverwritten() {
        return ! overwrittenBy.isEmpty();
    }

    void report(StatusReporter reporter) {
        if(! readBy.isEmpty()) {
            return;
        }
        if(variableWriteKind == VariableWriteKind.PARAMETER) {
            issueVarNotUsed(reporter);
            return;
        }
        if(! isInitial()) {
            issueVarNotUsed(reporter);
            return;
        }
        if(! isOverwrittenFromDescendantBlock()) {
            issueVarNotUsed(reporter);
        }
    }

    private void issueVarNotUsed(StatusReporter reporter) {
        reporter.report(StatusCode.VAR_NOT_USED, line, column, variable.getName());
    }

    private boolean isOverwrittenFromDescendantBlock() {
        Set<CodeBlock> relevantOverwriters = new HashSet<>(overwrittenBy);
        relevantOverwriters.remove(codeBlock);
        return ! relevantOverwriters.isEmpty();
    }
}
