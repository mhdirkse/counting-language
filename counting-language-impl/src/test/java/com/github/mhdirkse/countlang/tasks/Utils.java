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

import java.io.IOException;
import java.io.StringReader;

import com.github.mhdirkse.countlang.algorithm.OutputStrategy;
import com.github.mhdirkse.countlang.execution.Stepper;

class Utils {
    static Stepper compileAndGetStepper(final String programText, OutputStrategy outputStrategy) {
        try {
            return compileAndGetStepperUnchecked(programText, outputStrategy);
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }        
    }

    private static Stepper compileAndGetStepperUnchecked(
            final String programText, OutputStrategy outputStrategy) throws IOException {
        StringReader reader = new StringReader(programText);
        try {
            return new ProgramExecutor(reader).getStepper(outputStrategy);
        }
        finally {
            reader.close();
        }
    }
}
