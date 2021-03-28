# counting-language

## Introduction

### The basics

Simple programming language that is designed to solve problems of probability theory.
This language is still under development.

The following sample program illustrates the main idea of this project:

    experiment twoDice() {
      dice = distribution 1, 2, 3, 4, 5, 6;
      sample d1 from dice;
      sample d2 from dice;
      return d1 + d2;
    };
    print twoDice();

The output of running this counting-language program is:

        2   1
        3   2
        4   3
        5   4
        6   5
        7   6
        8   5
        9   4
       10   3
       11   2
       12   1
    ---------
    total  36

The language introduces a datatype called "distribution".
A distribution is a multiset. The output you see is a distribution,
the left-column showing a value in the multi-set and the right column
showing how many times the value is in. A distribution can also
be interpreted as a probability distribution. For example, the shown
output means that the value 7 has a probability of 6 / 36, while
the value 12 has probability 1 / 36. counting-language does not work
with real-valued probabilities, but gives exact results.

An experiment looks like a function but it has a different meaning
that is specific to probability theory. An experiment contains "sample"
statements. The statement `sample d1 from distribution 1, 2, 3, 4, 5, 6`
means: Draw a stochastic variable from the probability distribution
`distribution 1, 2, 3, 4, 5, 6`, which means a uniform distribution
from 1 to 6, and store the sampled value in variable `d1`.
The interpreter iterates over all possible values for `d1` and maintains
the probability of each possibility.

The iterations over `1 ... 6` are independent because the interpreter copies its state
before continuing with the next statement. The interpreter copies its state before setting
`d1 = 1`. The next statement is `sample d2 from dice;`. The interpreter copies its state
again before setting `d2 = 1`. The next statement is `return d1 + d2;`. This means the
following: If the stochastic variables sampled in integer variables `d1` and `d2` are 1
 and 1, then the result of the experiment being defined is `d1 + d1 = 2`.

A "return" statement signals the interpreter to throw away the running copy of its state.
Execution continues with the old copy, which is still executing the statement
`sample d2 from dice;`. The interpreter sets `d2 = 2` and makes a new copy.
When all possible values for `d2` are exhausted, the copy running
`sample d2 from dice;` is thrown away. Execution continues with the original
copy of the execution state, which is executing `sample d1 from dice;`.
The interpreter sets `d1 = 2`, copies its state and continues execution.
This way, calculations with one sampled value `d1` do not influence results
that come from another sampled value `d1`. The samples `d2` are also
independent of each other and they only depend on the sampled value `d1`.

The shown result is the probability distribution of stochastic variable `S = D1 + D2`, 
where D1 has a uniform distribution on `1 .. 6` and where `P( D2 | D1 = d1)` is
also a uniform distribution on `1 .. 6` for each possible `d1`.

### Incomplete probability distributions

counting-language supports incomplete probability distributions for which the probabilities
do not add up to one. Consider the following example program:

    experiment incomplete() {
      sample coin1 from distribution 0, 1;
      if(coin1 == 0) {
        sample coin2 from distribution 0, 1;
        return coin2;
      };
    };
    print incomplete();

Note that there is no "else" in this if and that there is no return value in case `coin1` == 1.
The output is as follows:

          0  1
          1  1
    unknown  2
    ----------
      total  4

counting-language assigns probability 1 / 4 to the outcome 0 and also to the outcome 1, because
these result from sampled combinations (0, 0) and (0, 1). But the outcome is unspecified
when `coin1` == 1. That possibility has probability 2 / 4, and it is assigned to an event
named "unknown". You can extend this program with other experiments that sample from experiment
`incomplete`. Those experiments will have the unknown event as outcome when they sample
the unknown event from experiment `incomplete`.

If the unknown event is not interesting to you, you can also calculate the conditional
probability distribution that assumes that the unknown event does not happen. To do
this, replace the last line of the example code by:

    print known of incomplete();

Now the unknown event is omitted. The result is:

        0  1
        1  1
    --------
    total  2


## Other features of the language

Apart from the probability theory features, counting-language supports the following:

* Integer +, -, *, /, with / such that the remainder of the division would be positive.
* Unary - operator.
* You can manage the order of expression evaluation with brackets.
* Logical and, or, not.
* Relational operators <, >, <=, >=, ==, !=.
* You can define variables of type int, boolean or some distribution.
* Distribution types are defined like `distribution<int>`, `distribution<bool>` or `distribution<distribution<...>>` where the dots have to be replaced by any type.
* Values of type distribution are defined like `distribution 1, 2, 3` for a non-empty `distribution<int>` or just `distribution<int>` for the empty distribution containing integers.
* Variables defined between `{` and `}` go out of scope when the statement block ends.
* You can define functions that are required to return a value (contrary to experiments).
* There is an ``if``-statement.
* There is a ``while`` statement.

For details, see the [grammar](/counting-language-base/src/main/antlr4/com/github/mhdirkse/countlang/lang/Countlang.g4).

Here are a few things to take care of when programming:
* Between two statements, you always need a `;`, also if the first statement ends with `}`.
* If you have an `else` clause in an `if`-statement, then it may not be empty. But if you have no need for `else`, you can omit it alltogether.

# Usage instructions

* You need Java 8 and Maven 3.6.
* Clone counting-language, codegen and utils.
* Choose the version of counting-language that you want to build. I recommend using the latest tagged version.
* In your counting-language checkout, checkout the version you want. Check it out using `git checkout`.
* In your counting-language pom.xml file, check what versions of codegen and utils are required.
* In your utils copy, checkout the version you found in the counting-language pom.
* Build utils using `mvn clean install`.
* In your codegen copy, checkout the relevant codegen version.
* Build codegen using `mvn clean install`.
* Build counting-language using `mvn clean package`.
* In your counting-language checkout, go to `counting-language-impl/target/scripts`.
* Check whether the `run.sh` script contains `${project.version}`. If so, you have to rebuild.
* Give yourself executable permission on the script `run.sh`.
* Run the program, for example as `./run.sh inputs/simpleAdd`.
* See the `inputs` directory for more examples.
* Javadocs are in `counting-language/target/site/apidocs`.
* There are also detailed Javadocs, see `counting-language/target/site/detaildocs`.

# Implementation

counting-language is implemented using ANTLR 4 to parse the input. The syntax of the language is in `counting-language-base/src/main/antlr4/com/github/mhdirkse/countlang/lang/Countlang.g4`. ANTLR 4 generates interface `CountlangListener` and ensures that the methods of this interface are called. Please note that generated code appears in the `target` folders in Eclipse, each project having its own `target` folder.

The dependency `codegenPlugin` generates code that sets up a chain of responsibilities. Calls to CountlangListener are processed by the implementation `CountlangListenerDelegator`. This class calls instances of `CountlangListenerHandler` until a handler returns true. This chain of responsbilies is used to handle nested structures in the language. Almost each non-leaf node in the parse tree has its own handler. Using these structures an Abstract Syntax Tree is built. The AST does not do calculations and it also has no logic in it to verify validity of the parsed program. All these tasks are done with listeners which can be found in subproject `counting-language-impl`. Boiler-plate code for these listeners is also in code generated by `codegenPlugin`. See `counting-language-impl/target/generated-sources/codegen`.

For the grammar, two plugins have to generate code. First, ANTLR 4 has to create a listener and then `codegenPlugin` sets up a chain of responsibilities based on this listener. The following sub-projects are created to set this up:

* `counting-language-base`: Holds the grammar and the classes of the AST (abstract syntax tree).
* `counting-language-impl`: Implements parsing the language, sanity-checking the AST and doing the calculations.
* `counting-language-generator`: Dependency required by `codegenPlugin`. This code is not packaged in the final .jar, but applied by `codegenPlugin` to produce the generated code.
* `counting-language-generator-test`, `counting-language-generator-test-input`: Test code for counting-language-generator.

Project `counting-language-base` has the following packages:
* `com.github.mhdirkse.countlang.ast`: Holds the AST.
* `com.github.mhdirkse.countlang.algorithm`: Holds algorithms that do not depend on the AST or the grammar.
* `com.github.mhdirkse.countlang.utils`: Utilities.
* `com.github.mhdirkse.countlang.lang`: Code generated by ANTLR 4 from the grammar.

Project `counting-language-impl` has the following packages:
* `com.github.mhdirkse.countlang.analysis`: Verifies the validity of counting-language input files.
* `com.github.mhdirkse.countlang.cmd`: The command line interface of counting-language.
* `com.github.mhdirkse.countlang.lang.parsing`: Implements parsing source code written in the counting-language language.
* `com.github.mhdirkse.countlang.tasks`: Tasks to be performed by the counting-language software, independent from the user interface applied to call them.
* `com.github.mhdirkse.countlang.execution`: Executes counting-language programs.
