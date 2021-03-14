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

import static com.github.mhdirkse.countlang.tasks.Constants.DECREMENT_OF_MIN_INT_PLUS_ONE;
import static com.github.mhdirkse.countlang.tasks.Constants.INCREMENT_OF_MAX_INT_MINUS_ONE;
import static com.github.mhdirkse.countlang.tasks.Constants.MAX_INT;
import static com.github.mhdirkse.countlang.tasks.Constants.MIN_INT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.execution.Stepper;

@RunWith(Parameterized.class)
public class IntegrationHappyTest extends IntegrationHappyTestBase
{
    @Parameters(name = "{0}, expect output {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {"print 5 + 3", "8"},
            {"print 5 - 3", "2"},
            {"print 5 * 3", "15"},
            {"print 15 / 3", "5"},
            {"print 5 / 3", "1"}, // Round down.
            {"print -5 / 3", "-2"}, // Round down.
            {"print -5 / -3", "1"}, // Round down.
            {"print 5 / -3", "-2"}, // Round down.
            {"print 0 / 3", "0"}, // Do not round unnecessarily.
            {"print 0 / -3", "0"}, // Do not round unnecessarily.
            {"print -5--3", "-2"},
            {"print true", "true"}, // Boolean operator tests
            {"print false", "false"},
            {"print not true", "false"},
            {"print true and true", "true"},
            {"print true and false", "false"},
            {"print true or false", "true"},
            {"print false or false", "false"},
            {"print 3 < 5", "true"},
            {"print 5 < 5", "false"},
            {"print 7 < 5", "false"},
            {"print 3 <= 5", "true"},
            {"print 5 <= 5", "true"},
            {"print 7 <= 5", "false"},
            {"print 3 > 5", "false"},
            {"print 5 > 5", "false"},
            {"print 7 > 5", "true"},
            {"print 3 >= 5", "false"},
            {"print 5 >= 5", "true"},
            {"print 7 >= 5", "true"},
            {"print true == true", "true"},
            {"print true == false", "false"},
            {"print 3 == 3", "true"},
            {"print 3 == 5", "false"},
            {"print true != true", "false"},
            {"print true != false", "true"},
            {"print 3 != 3", "false"},
            {"print 5 != 3", "true"},
            {INCREMENT_OF_MAX_INT_MINUS_ONE, MAX_INT}, // No overflow.
            {DECREMENT_OF_MIN_INT_PLUS_ONE, MIN_INT}, // No underflow.
            
            // Test literal distributions
            // TODO: Test empty distribution
            {"print distribution 1, 1, 3", getSimpleDistribution().format()},
            {"print distribution 1 total 3", getDistributionWithUnknown().format()},
            {"print distribution 1 unknown 2", getDistributionWithUnknown().format()},
            {"function fun() {return distribution 1, 1, 3}; print fun()", getSimpleDistribution().format()},
            {"d = distribution 1, 1, 3;\nfunction fun(distribution<int> arg) {print arg; return 0};\nx = fun(d); print x",
                getSimpleDistribution().format()},
            {"print distribution 1, 2 - 1, 5 - 2", getSimpleDistribution().format()},
            {"print distribution 2 of 3;", getDistribution(3, 3)},
            {"print distribution 2 of 3, 4;", getDistribution(3, 3, 4)},
            {"print distribution 4, 2 of 3;", getDistribution(3, 3, 4)},
            {"print distribution 2 of 3, 4 total 5", getDistributionWithUnknown(3, 3, 4, 2)},
            {"print distribution 4, 2 of 3 unknown 12", getDistributionWithUnknown(3, 3, 4, 12)},
            {"print distribution 2 of -1", getDistribution(-1, -1)},
            {"print known of distribution 1, 2 total 5", getDistribution(1, 2)},
            
            {"print testFunction(4)", "9"},
            {"print 2 + testFunction(4)", "11"},
            {"print testFunction(testFunction(4))", "14"},
            {"function myFun(int x, int y) {z = x - y; return z}; print myFun(5, 3)", "2"},
            {"function myFun(int x) {return x}; print myFun(2 + 3)", "5"},
            {"function myFun() {return 10}; print myFun()", "10"}, // Function without arguments.
            {"x = 5; print x;", "5"}, // Allow extra ; outside functions.
            {"function fun() {return 5;}; print 5", "5"}, // Allow extra ; within function.
            {"x = 5; print -x", "-5"}, // The unary minus.
            {"x = 5; y = 3; print x - - y", "8"}, // Combine unary minus with ordinary minus.
            {"x = 5; y = 3; print x*-y", "-15"}, // Combine unary minus with multiplication.
            {"x = 5; y = 3; print -x-y", "-8"}, // Have unary minus as first child of binary operator.
            {"x = true; print x", "true"}, // Symbol reference of type boolean
            {"function fun(bool x) {return not x and true}; print fun(true)", "false"}, // Formal boolean argument
            {"function fun(bool x) {return not x and true}; print fun(false)", "true"},
            {"function fun(int x, int y) {return x < y}; print fun(3, 5)", "true"}, // Handle return type with relop.
            {"function fun(int x, int y) {return x < y}; print fun(5, 5)", "false"},
            {"function fun(int x, int y) {return x < y}; print fun(7, 5)", "false"},
            {"print true and 3 < 5", "true"}, // Operator precedence, comparison before boolop
            {"print 3 == 5 or 5 == 5", "true"}, // Operator precedence, equality before boolop

            // Compound statements
            {"{print 3}", "3"}, // Program can be compound statement
            {"x = 3; markUsed x; {x = 5;}; print x", "5"}, // When global exists, you access it
            {"y = 5; markUsed y; {x = 3; y = x}; print y", "3"}, // Instantiate global from local
            {"function fun() { {x = 3; return x} }; print fun()", "3"}, // Return in compound statement

            // if statement
            {"if(true) {print 3}", "3"}, // Only then clause, execute it.
            {"if(false) {print 3}; print 5", "5"}, // Only then clause, not executed
            {"if(true) {print 3} else {print 5}", "3"}, // Then and else, execute then.
            {"if(false) {print 3} else {print 5}", "5"}, // Then and else, execute else

            // while statement
            {"i = 0; while(i < 2) {i = i + 1}; print i", "2"},
            {"i = 0; while(i < 0) {i = i + 1}; print i", "0"},
            // no return within the body.
            {"function factorial(int n) {result = 1; i = 1; while(i <= n) {result = result * i; i = i + 1}; return result}; print factorial(3)", "6"},
            // with return in the body 
            {"function factorial(int n) {result = 1; i = 1; while(true) {result = result * i; i = i + 1; if(i > n) {return result}; }; }; print factorial(3)", "6"},
            {"experiment exp() {sample x from distribution 1, 2; return 2*x}; print exp()", getDistribution(2, 4)},
            {"experiment exp(distribution<int> d1, distribution<int> d2) {sample x from d1; sample y from d2; return x + y}; print exp((distribution 1, 2), (distribution 1, 2, 3));",
                getDistribution(2, 3, 4, 3, 4, 5)},
            {getProgramExperimentUsesExperiment(), getDistribution(1, 1, 1, 3, 4, 5)},
            {"experiment exp() {sample x from distribution 1, 2; if(x == 1) {return 3;};}; print exp();", getDistributionWithUnknown(3, 1)},
            {getProgramAboutDisease(), getProgramAboutDiseaseExpectedResult()},
            {"experiment exp() {sample x from distribution 1, 2; if(x == 1) {return 3;};}; print known of exp();", getDistribution(3)},
        });
    }

    private static String getProgramExperimentUsesExperiment() {
        List<String> lines = Arrays.asList(
                "experiment two() {",
                "  sample x from distribution 1, 2;",
                "  return x;",
                "};",
                "experiment three() {",
                "  sample x from distribution 1, 2, 3;",
                "  return x;",
                "};",
                "experiment combine() {",
                "  sample x from two();",
                "  if(x == 2) {",
                "    sample y from three();",
                "    x = x + y;",
                "  };",
                "  return x;",
                "};",
                "print combine();");
        return lines.stream().collect(Collectors.joining("\n"));
    }

    private static String getProgramAboutDisease() {
        return Arrays.asList("experiment sick() {",
            "distSick = distribution 9999 of false, 1 of true;",
            "distDiagnosedIfSick = distribution 1 of false, 99 of true;",
            "distDiagnosedIfNotSick = distribution 99 of false, 1 of true;",
            "diagnosed = false;",
            "sample sick from distSick;",
            "if(sick) {",
            "    sample diagnosed from distDiagnosedIfSick;",
            "} else {",
            "    sample diagnosed from distDiagnosedIfNotSick;",
            "};",
            "if(diagnosed) {",
            "    return sick;",
            "};",
            "};",
            "print sick();").stream().collect(Collectors.joining("\n"));
    }

    private static String getProgramAboutDiseaseExpectedResult() {
        // I did this with a Spreadsheet.
        Distribution.Builder b = new Distribution.Builder();
        b.add(0, 9999);
        b.add(1, 99);
        b.addUnknown(989902);
        return b.build().format();
    }

    private static String getDistribution(int ...values) {
        Distribution.Builder b = new Distribution.Builder();
        for(int v: values) {
            b.add(v);
        }
        return b.build().format();
    }

    private static String getDistributionWithUnknown(int ...values) {
        Distribution.Builder b = new Distribution.Builder();
        for(int i = 0; i < values.length; i++) {
            if(i < (values.length - 1)) {
                b.add(values[i]);
            } else {
                b.addUnknown(values[i]);
            }
        }
        return b.build().format();
    }

    @Parameter(0)
    public String input;

    @Parameter(1)
    public String expectedResult;

    @Test
    public void testResult() {
        compileAndRun(input);
        Assert.assertEquals(getOutputStrategyErrors(), 0, outputStrategy.getNumErrors());
        Assert.assertEquals(expectedResult, outputStrategy.getLine(0));
    }

    private String getOutputStrategyErrors() {
        final List<String> lines = new ArrayList<>();
        for(int i = 0; i < outputStrategy.getNumErrors(); i++) {
            lines.add(outputStrategy.getError(i));
        }
        return lines.stream().collect(Collectors.joining(". "));
    }

    @Test
    public void testExecutionPointsValid() {
        Stepper stepper = Utils.compileAndGetStepper(input, this);
        Assert.assertTrue(stepper.getExecutionPoint().isValid());
        while(stepper.hasMoreSteps()) {
            stepper.step();
            Assert.assertTrue(stepper.getExecutionPoint().isValid());
        }
    }
}
