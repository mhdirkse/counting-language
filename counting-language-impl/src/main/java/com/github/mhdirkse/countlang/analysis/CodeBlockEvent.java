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

import java.util.Comparator;

import lombok.Getter;
import lombok.Setter;

class CodeBlockEvent implements Comparable<CodeBlockEvent> {
    private final @Getter int line;
    private final @Getter int column;
    static final Comparator<CodeBlockEvent> COMPARATOR = Comparator.comparingInt(CodeBlockEvent::getLine).thenComparing(CodeBlockEvent::getColumn);

    CodeBlockEvent(int line, int column) {
        this.line = line;
        this.column = column;
    }

    @Override
    public int compareTo(final CodeBlockEvent other) {
        return COMPARATOR.compare(this, other);
    }

    static class Return extends CodeBlockEvent {
        private @Getter @Setter Statement after = null;

        Return(int line, int column) {
            super(line, column);
        }
    }

    static class Statement extends CodeBlockEvent {
        Statement(int line, int column) {
            super(line, column);
        }
    }
}
