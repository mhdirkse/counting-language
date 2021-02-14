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

package com.github.mhdirkse.countlang.algorithm;

import java.util.ArrayList;
import java.util.List;

public final class TestOutputStrategy implements OutputStrategy {
    private final List<String> lines = new ArrayList<String>();
    private final List<String> errors = new ArrayList<String>();

    @Override
    public void output(final String s) {
        lines.add(s);
    }

    public int getNumLines() {
        return lines.size();
    }

    public String getLine(final int index) {
        return lines.get(index);
    }

    @Override
    public void error(final String s) {
        errors.add(s);
    }

    public int getNumErrors() {
        return errors.size();
    }

    public String getError(final int index) {
        return errors.get(index);
    }
}
