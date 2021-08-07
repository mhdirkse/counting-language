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

import com.github.mhdirkse.countlang.type.CountlangType;

public abstract class AbstractDistributionExpression extends ExpressionNode implements CompositeNode {
    private CountlangType countlangType = CountlangType.unknown();
    private List<AbstractDistributionItem> scoredValues = new ArrayList<>();

    public AbstractDistributionExpression(int line, int column) {
        super(line, column);
    }

    @Override
    public CountlangType getCountlangType() {
        return countlangType;
    }

    public void setCountlangType(CountlangType countlangType) {
        this.countlangType = countlangType;
    }

    public void addScoredValue(final AbstractDistributionItem expression) {
        scoredValues.add(expression);
    }

    public List<AbstractDistributionItem> getScoredValues() {
        return scoredValues;
    }

    public int getNumScoredValues() {
        return scoredValues.size();
    }

    @Override
    public List<AstNode> getChildren() {
        List<AstNode> result = new ArrayList<>();
        result.addAll(scoredValues);
        return result;
    }

    public int getNumSubExpressions() {
        return scoredValues.size();
    }
}
