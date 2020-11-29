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

package com.github.mhdirkse.countlang.tasks;

import java.util.Arrays;

import com.github.mhdirkse.utils.AbstractStatusCode;

class Status {
    private final AbstractStatusCode statusCode;
    private final String[] args;

    private Status(final AbstractStatusCode statusCode, final String... args) {
        this.statusCode = statusCode;
        this.args = Arrays.copyOf(args, args.length);
    }

    String format() {
        return statusCode.format(args);
    }

    static Status forLine(
            final AbstractStatusCode statusCode, final int line, final int column, final String... others) {
        String[] args = new String[others.length + 2];
        args[0] = Integer.valueOf(line).toString();
        args[1] = Integer.valueOf(column).toString();
        System.arraycopy(others, 0, args, 2, others.length);
        return new Status(statusCode, args);
    }
}
