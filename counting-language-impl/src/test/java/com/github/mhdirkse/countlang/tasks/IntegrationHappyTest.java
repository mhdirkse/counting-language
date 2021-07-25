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

import static com.github.mhdirkse.countlang.tasks.Constants.DECREMENT_OF_MIN_INT;
import static com.github.mhdirkse.countlang.tasks.Constants.DECREMENT_OF_MIN_INT_PLUS_ONE;
import static com.github.mhdirkse.countlang.tasks.Constants.INCREMENT_OF_MAX_INT;
import static com.github.mhdirkse.countlang.tasks.Constants.INCREMENT_OF_MAX_INT_MINUS_ONE;
import static com.github.mhdirkse.countlang.tasks.Constants.MAX_INT;
import static com.github.mhdirkse.countlang.tasks.Constants.MIN_INT;

import java.math.BigInteger;
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
import com.github.mhdirkse.countlang.algorithm.testtools.TestConstants;
import com.github.mhdirkse.countlang.algorithm.testtools.TestFactory;
import com.github.mhdirkse.countlang.algorithm.testtools.TestFactory.DistributionBuilderInt2Bigint;
import com.github.mhdirkse.countlang.execution.Stepper;

@RunWith(Parameterized.class)
public class IntegrationHappyTest extends IntegrationHappyTestBase
{
    private static final TestFactory tf = new TestFactory();

    @Parameters(name = "{0}, expect output {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {"", "<no output>"},
            {"print 5 + 3", "8"},
            {"print 5 - 3", "2"},
            {"print 5 * 3", "15"},
            {"print 15 / 3", "5"},
            {"print 5 / 3", "1"}, // Round towards zero.
            {"print -5 / 3", "-1"}, // Round towards zero.
            {"print -5 / -3", "1"}, // Round towards zero.
            {"print 5 / -3", "-1"}, // Round towards zero.
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

            // Bigint
            {"print 12345678901234567890", "12345678901234567890"},
            {INCREMENT_OF_MAX_INT, new Long(((long) Integer.MAX_VALUE) + 1).toString()},
            {DECREMENT_OF_MIN_INT, new Long(((long) Integer.MIN_VALUE) - 1).toString()},
            {"print 1000000 * 1000000", "1000000000000"},
            {getProgramCausingOverflow(), getProgramCausingOverflowExpectedValue()},
            {"possibility counting " + getProgramCausingOverflow(), getProgramCausingOverflowExpectedValue()},
            {getProgramThatBuildsBigintDistribution(), getProgramThatBuildsBigintDistributionExpected()},

            // Test literal distributions
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
            {"print distribution false, true", getDistribution(false, true)},
            {"print distribution 2 of false, 3 of true", getDistribution(false, false, true, true, true)},
            {"print distribution<int>;", getDistribution(new int[] {})},
            {"print distribution true total 2", getDistribution(false, true)},
            {"print distribution false total 3", getDistribution(false, true, true)},
            {"print distribution false, true total 4", getDistributionWithUnknown(2, false, true)},
            {"print distribution 2 of false total 3", getDistribution(false, false, true)},
            {"print distribution 1 unknown 2", getDistributionWithUnknown(1, 2)},
            {"print distribution 1 total 3", getDistributionWithUnknown(1, 2)},
            // Check that all subexpressions within a literal distribution are type-checked
            {"print distribution 1 * 2 of 1 * 3 total 1 * 4", getDistributionWithUnknown(3, 3, 2)},
            {"print distribution 1 * 2 of 1 * 3 unknown 1 * 2", getDistributionWithUnknown(3, 3, 2)},

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

            // Result of experiment is normalized
            {"experiment exp() {sample x from distribution 2 of 10, 4 of 11; return x}; print exp();", getDistribution(10, 11, 11)},
            // Distribution literal is not normalized
            {"print distribution 2 of 10, 4 of 11;", getDistribution(10, 10, 11, 11, 11, 11)},
            // Not normalized if counting possibilities
            {"possibility counting experiment exp() {sample x from distribution 2 of 10, 4 of 11; return x}; print exp()", getDistribution(10, 10, 11, 11, 11, 11)},
            // Sample statement scores unknown when distribution has unknown
            {getProgramCoveringSampleStatementScoringUnknown(), getProgramCoveringSampleStatementScoringUnknownExpected()},
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
            {"function fun() {if(true) {return 1} else {}; return 0}; print fun();", "1"}, // Allow empty else
            {"function fun() {if(false) {} else {return 1}; return 0}; print fun();", "1"}, // Allow empty then

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
            {getProgramTwoCoins(), getTwoCoinsExpectedValue()},
            {getProgramThatForksWhile(), getDistribution(false, false, false, true)},
            {getProgramThatCountsPossibilities(), getProgramThatCountsPossibilitiesExpectedValue()},
            {"experiment exp() {}; print exp();", getDistributionOnceUnknown()}
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
            "distSick = distribution 1 of true total 10000;",
            "distDiagnosedIfSick = distribution 1 of false total 100;",
            "distDiagnosedIfNotSick = distribution 1 of true total 100;",
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
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(false, 9999);
        b.add(true, 99);
        b.addUnknown(989902);
        return b.build().format();
    }

    private static String getProgramTwoCoins() {
        List<String> lines = new ArrayList<>();
        lines.add("experiment twoCoins() {");
        lines.add("    coinDistribution = distribution false, true;");
        lines.add("    sample c1 from coinDistribution;");
        lines.add("    sample c2 from coinDistribution;");
        lines.add("    return distribution c1, c2;");
        lines.add("};");
        lines.add("");
        lines.add("print twoCoins();");
        return lines.stream().collect(Collectors.joining("\n"));
    }

    private static String getTwoCoinsExpectedValue() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        DistributionBuilderInt2Bigint v = tf.distBuilder();
        v.add(false, 2);
        b.add(v.build());
        v = tf.distBuilder();
        v.add(false);
        v.add(true);
        b.add(v.build(), 2);
        v = tf.distBuilder();
        v.add(true, 2);
        b.add(v.build());
        return b.build().format();
    }

    private static String getProgramThatForksWhile() {
        return Arrays.asList(
                "experiment exp() {",
                "  result = true;",
                "  i = 2;",
                "  while(i > 0) {",
                "    sample b from distribution false, true;",
                "    result = result and b;",
                "    i = i - 1",
                "  };",
                "  return result;",
                "};",
                "print exp()")
                .stream().collect(Collectors.joining("\n"));
    };

    private static String getProgramThatCountsPossibilities() {
        return Arrays.asList(
                "possibility counting experiment exp() {",
                "labeledDice = distribution 1, 1, 1, 2, 2, 2;",
                "sample d1 from labeledDice;",
                "sample d2 from labeledDice;",
                "return d1 + d2;",
                "};",
                "print exp();")
                .stream().collect(Collectors.joining("\n"));
    }

    private static String getProgramThatCountsPossibilitiesExpectedValue() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(2, 9);
        b.add(3, 18);
        b.add(4, 9);
        return b.build().format();
    }

    private static String getProgramCausingOverflow() {
        return  "experiment overflow() {\n" + 
                "    big = distribution 1 of true total 1000000;\n" + 
                "    sample x from big;\n" + 
                "    sample y from big;\n" + 
                "    return x and y;\n" + 
                "};\n" + 
                "print overflow();";
    }

    private static String getProgramCoveringSampleStatementScoringUnknown() {
        return Arrays.asList(
                "experiment exp() {",
                "    sample x from distribution 2 of 10, 3 of 11 total 10;",
                "    y = 0;",
                "    if(x == 10) {",
                "        sample y from distribution 100, 2 of 200;",
                "    };",
                "    return x + y;",
                "};",
                "print exp();").stream().collect(Collectors.joining("\n"));
    }

    private static String getProgramThatBuildsBigintDistribution() {
        return Arrays.asList(
                "function fun(int x) {",
                "  return distribution x of x, 2*x of 3*x total 8*x",
                "};",
                "print fun(1000000000000);").stream().collect(Collectors.joining("\n"));
    }

    private static String getProgramThatBuildsBigintDistributionExpected() {
        Distribution.Builder b = new Distribution.Builder();
        BigInteger TRILLION = new BigInteger("1000000000000");
        b.add(TRILLION, TRILLION);
        b.add(TRILLION.multiply(TestConstants.THREE), TRILLION.multiply(TestConstants.TWO));
        b.addUnknown(TRILLION.multiply(TestConstants.FIVE));
        return b.build().format();
    }

    private static String getProgramCoveringSampleStatementScoringUnknownExpected() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(11, 9);
        b.add(110, 2);
        b.add(210, 4);
        b.addUnknown(15);
        return b.build().format();
    }

    private static String getProgramCausingOverflowExpectedValue() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(true, BigInteger.ONE);
        b.add(false, new BigInteger("999999999999"));
        return b.build().format();
    }

    private static String getDistribution(int ...values) {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        for(int v: values) {
            b.add(v);
        }
        return b.build().format();
    }

    private static String getDistribution(boolean ...values) {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        for(boolean v: values) {
            b.add(v);
        }
        return b.build().format();
    }
    
    private static String getDistributionWithUnknown(int ...values) {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        for(int i = 0; i < values.length; i++) {
            if(i < (values.length - 1)) {
                b.add(values[i]);
            } else {
                b.addUnknown(values[i]);
            }
        }
        return b.build().format();
    }

    private static String getDistributionWithUnknown(int unknown, boolean ...values) {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        for(int i = 0; i < values.length; i++) {
            b.add(values[i]);
        }
        b.addUnknown(unknown);
        return b.build().format();        
    }

    private static String getDistributionOnceUnknown() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.addUnknown(1);
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
        String actualResult = "<no output>";
        if(outputStrategy.getNumLines() >= 1) {
            actualResult = outputStrategy.getLine(0);
        }
        Assert.assertEquals(expectedResult, actualResult);
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
