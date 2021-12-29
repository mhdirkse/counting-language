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

package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

public class ForInRepetitionStatement extends Statement implements CompositeNode {
	private ExpressionNode fromArray;
    private StatementGroup statement;
    private AbstractLhs lhs;

    public ForInRepetitionStatement(int line, int column) {
        super(line, column);
    }

	public ExpressionNode getFromArray() {
		return fromArray;
	}

	public void setFromArray(ExpressionNode fromArray) {
		this.fromArray = fromArray;
	}

	public StatementGroup getStatement() {
		return statement;
	}

	public void setStatement(StatementGroup statement) {
		this.statement = statement;
	}

	public AbstractLhs getLhs() {
		return lhs;
	}

	public void setLhs(AbstractLhs lhs) {
		this.lhs = lhs;
	}

    @Override
    public void accept(Visitor v) {
        v.visitForInRepetitionStatement(this);
    }

    @Override
    public List<AstNode> getChildren() {
        List<AstNode> result = new ArrayList<>();
        result.add(fromArray);
        result.add(lhs);
        result.add(statement);
        return result;
    }
}
