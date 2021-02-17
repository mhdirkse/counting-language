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

import com.github.mhdirkse.countlang.algorithm.OutputStrategy;
import com.github.mhdirkse.utils.AbstractStatusCode;

class StatusReporterImpl implements StatusReporter {
    private final OutputStrategy output;
    private boolean hasErrors;

    StatusReporterImpl(final OutputStrategy output) {
        this.output = output;
        hasErrors = false;
    }

    @Override
    public void report(
            final AbstractStatusCode status,
            final int line,
            final int column,
            final String... others) {
        hasErrors = true;
        output.error(Status.forLine(status, line, column, others).format());
    }

    @Override
    public boolean hasErrors() {
        return hasErrors;
    }
}
