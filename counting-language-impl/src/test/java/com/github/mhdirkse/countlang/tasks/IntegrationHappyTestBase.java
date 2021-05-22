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

import org.junit.After;
import org.junit.Before;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.algorithm.OutputStrategy;
import com.github.mhdirkse.countlang.algorithm.TestFactory;
import com.github.mhdirkse.countlang.algorithm.TestFactory.DistributionBuilderInt2Bigint;
import com.github.mhdirkse.countlang.algorithm.TestOutputStrategy;

public class IntegrationHappyTestBase implements OutputStrategy {
    private static final TestFactory tf = new TestFactory();

    TestOutputStrategy outputStrategy;

    @Before
    public void setUp() {
        StatusCode.setTestMode(true);
        outputStrategy = new TestOutputStrategy();
    }

    @After
    public void tearDown() {
        StatusCode.setTestMode(false);
    }

    @Override
    public void output(final String output) {
        outputStrategy.output(output);
    }

    @Override
    public void error(final String error) {
        outputStrategy.error(error);
    }

    static Distribution getSimpleDistribution() {
        DistributionBuilderInt2Bigint builder = tf.distBuilder();
        builder.add(1);
        builder.add(1);
        builder.add(3);
        return builder.build();
    }

    static Distribution getDistributionWithUnknown() {
        DistributionBuilderInt2Bigint builder = tf.distBuilder();
        builder.add(1);
        builder.addUnknown(2);
        return builder.build();
    }

    void compileAndRun(final String programText) {
        try {
            compileAndRunUnchecked(programText);
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void compileAndRunUnchecked(final String programText) throws IOException {
        StringReader reader = new StringReader(programText);
        try {
            new ProgramExecutor(reader).run(this);
        }
        finally {
            reader.close();
        }
    }
}
