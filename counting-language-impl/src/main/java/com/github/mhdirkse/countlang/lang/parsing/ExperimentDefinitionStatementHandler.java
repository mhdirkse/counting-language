/*
 * Copyright Martijn Dirkse 2020
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

package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.ExperimentDefinitionStatement;
import com.github.mhdirkse.countlang.ast.Statement;
import com.github.mhdirkse.countlang.ast.StatementGroup;

class ExperimentDefinitionStatementHandler extends FunctionDefinitionStatementHandlerBase
implements StatementSource {
    private ExperimentDefinitionStatement statement;

    @Override
    public Statement getStatement() {
        return statement;
    }

    @Override
    void addStatementGroup(StatementGroup statements) {
        statement.setStatements(statements);
    }

    @Override
    public void setText(final String text) {
        statement.setName(text);
    }

    ExperimentDefinitionStatementHandler(final int line, final int column) {
        super(line, column);
        statement = new ExperimentDefinitionStatement(line, column);
    }

    @Override
    void addFormalParameter(String name, CountlangType countlangType) {
        statement.addFormalParameter(name, countlangType);
    }
}
