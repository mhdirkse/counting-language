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

import java.util.stream.Collectors;

class CodeBlockParallel extends CodeBlock {
    CodeBlockParallel(CodeBlock parent) {
        super(parent);
    }

    @Override
    ReturnStatus getSpecificReturnStatus() {
        if(getNonSubfunctionChildren().isEmpty()) {
            throw new IllegalStateException("A CodeBlockParallel is assumed to have children because it branches");
        }
        ReturnStatus lowest = getNonSubfunctionChildren().stream().map(CodeBlock::getReturnStatus).collect(Collectors.minBy(ReturnStatus.COMPARATOR)).get();
        ReturnStatus highest = getNonSubfunctionChildren().stream().map(CodeBlock::getReturnStatus).collect(Collectors.maxBy(ReturnStatus.COMPARATOR)).get();
        ReturnStatus result = null;
        if(highest == ReturnStatus.NONE_RETURN) {
            result = ReturnStatus.NONE_RETURN;
        } else if(lowest == ReturnStatus.STRONG_ALL_RETURN) {
            result = ReturnStatus.STRONG_ALL_RETURN;
        } else if(lowest == ReturnStatus.WEAK_ALL_RETURN) {
            result = ReturnStatus.WEAK_ALL_RETURN;
        } else {
            result = ReturnStatus.SOME_RETURN;
        }
        return result;
    }

    @Override
    boolean isRootOrFunction() {
        return false;
    }

    @Override
    StatementHandler handleReturn(int line, int column) {
        throw new IllegalStateException("A branch is always followed by a new branch or by ending the switch, so a return statement cannot appear here");
    }
}
