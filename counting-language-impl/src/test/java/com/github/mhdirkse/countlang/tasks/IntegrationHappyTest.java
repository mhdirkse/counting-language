package com.github.mhdirkse.countlang.tasks;

import static com.github.mhdirkse.countlang.tasks.Constants.DECREMENT_OF_MIN_INT_PLUS_ONE;
import static com.github.mhdirkse.countlang.tasks.Constants.INCREMENT_OF_MAX_INT_MINUS_ONE;
import static com.github.mhdirkse.countlang.tasks.Constants.MAX_INT;
import static com.github.mhdirkse.countlang.tasks.Constants.MIN_INT;

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

import com.github.mhdirkse.countlang.steps.Stepper;
import com.github.mhdirkse.countlang.types.Distribution;

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
            
            // Test literal distributions
            {"print distribution 1, 1, 3", getSimpleDistribution().format()},
            {"print distribution 1 total 3", getDistributionWithUnknown().format()},
            {"print distribution 1 unknown 2", getDistributionWithUnknown().format()},
            {"function fun() {return distribution 1, 1, 3}; print fun()", getSimpleDistribution().format()},
            {"d = distribution 1, 1, 3;\nfunction fun(distribution arg) {print arg; return 0};\nx = fun(d); print x",
                getSimpleDistribution().format()},
            {"d = distribution; print d", (new Distribution.Builder().build().format())},
            {"print distribution 1, 2 - 1, 5 - 2", getSimpleDistribution().format()},
            {INCREMENT_OF_MAX_INT_MINUS_ONE, MAX_INT}, // No overflow.
            {DECREMENT_OF_MIN_INT_PLUS_ONE, MIN_INT}, // No underflow.
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

            {"experiment exp() {sample x from distribution 1, 2; return 2*x}; print exp()", getDistribution(2, 4)},
            {"experiment exp(distribution d1, distribution d2) {sample x from d1; sample y from d2; return x + y}; print exp((distribution 1, 2), (distribution 1, 2, 3));",
                getDistribution(2, 3, 4, 3, 4, 5)},
            {getProgramExperimentUsesExperiment(), getDistribution(1, 1, 1, 3, 4, 5)},
            {"experiment exp() {sample x from distribution 1, 2; if(x == 1) {return 3;};}; print exp();", getDistributionWithUnknown(3, 1)}
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
        Assert.assertEquals(0, outputStrategy.getNumErrors());
        Assert.assertEquals(expectedResult, outputStrategy.getLine(0));
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
