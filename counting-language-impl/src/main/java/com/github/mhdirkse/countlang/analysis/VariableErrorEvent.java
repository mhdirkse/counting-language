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

import com.github.mhdirkse.countlang.tasks.StatusCode;
import com.github.mhdirkse.countlang.tasks.StatusReporter;
import com.github.mhdirkse.countlang.type.CountlangType;

import lombok.Getter;

class VariableErrorEvent {
    static enum Kind {
        DOES_NOT_EXIST,
        DUPLICATE_PARAMETER,
        TYPE_MISMATCH;
    }

    private final @Getter Kind kind;
    private final @Getter String name;
    private final @Getter int line;
    private final @Getter int column;
    private final @Getter CountlangType variableType;
    private final @Getter CountlangType typeMismatch;

    VariableErrorEvent(Kind kind, String name, int line, int column) {
        if(kind == Kind.TYPE_MISMATCH) {
            throw new IllegalArgumentException("Do not use this constructor for type mismatches - you need to set the types involved");
        }
        this.kind = kind;
        this.name = name;
        this.line = line;
        this.column = column;
        this.variableType = null;
        this.typeMismatch = null;
    }

    VariableErrorEvent(String name, int line, int column, CountlangType variableType, CountlangType typeMismatch) {
        this.kind = Kind.TYPE_MISMATCH;
        this.name = name;
        this.line = line;
        this.column = column;
        this.variableType = variableType;
        this.typeMismatch = typeMismatch;
    }

    public void report(StatusReporter reporter) {
        switch(kind) {
        case DOES_NOT_EXIST:
            reporter.report(StatusCode.VAR_UNDEFINED, line, column, name);
            break;
        case DUPLICATE_PARAMETER:
            reporter.report(StatusCode.DUPLICATE_PARAMETER, line, column, name);
            break;
        case TYPE_MISMATCH:
            reporter.report(StatusCode.VAR_TYPE_CHANGED, line, column, name);
            break;
        }
    }
}
