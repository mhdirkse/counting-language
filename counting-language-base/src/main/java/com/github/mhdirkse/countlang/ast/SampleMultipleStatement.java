/*
 * Copyright Martijn Dirkse 2022
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

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class SampleMultipleStatement extends AbstractSampleStatement {
	@Getter
	@Setter
	private SimpleLhs lhs;

    @Getter
    @Setter
    private ExpressionNode numSampled;

    public SampleMultipleStatement(int line, int column) {
        super(line, column);
    }

    @Override
    public void accept(Visitor v) {
        v.visitSampleMultipleStatement(this);
    }

    @Override
    public List<AstNode> getChildren() {
        return Arrays.asList(numSampled, getSampledDistribution(), lhs);
    }
}
