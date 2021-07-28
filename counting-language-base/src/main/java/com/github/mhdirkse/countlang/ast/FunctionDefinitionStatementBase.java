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

package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public abstract class FunctionDefinitionStatementBase extends Statement implements CompositeNode, FunctionDefinition {
    @Getter(onMethod = @__({@Override}))
    @Setter
    private FunctionKey key = null;

    @Getter
    private final FormalParameters formalParameters;

    @Getter
    @Setter
    private StatementGroup statements;

    @Getter(onMethod = @__({@Override}))
    @Setter
    private CountlangType returnType = CountlangType.unknown();

    public FunctionDefinitionStatementBase(final int line, final int column) {
        super(line, column);
        formalParameters = new FormalParameters(line, column);
    }

    @Override
    public int getNumParameters() {
        return formalParameters.size();
    }

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
    public List<AstNode> getChildren() {
        List<AstNode> result = new ArrayList<>();
        result.add(formalParameters);
        result.add(statements);
        return result;
    }
}
