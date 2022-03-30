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
      sample coin1 from distribution false, true;
      if(coin1) {
        sample coin2 from distribution false, true;
        return coin2;
      };
    };

    print incomplete();

Note that there is no `else` in this if and that there is no return value in case `coin1` is `false`.
The output is as follows:

      false  1
       true  1
    unknown  2
    ----------
      total  4

counting-language assigns probability `1 / 4` to the outcome `false` and also to the outcome `true`, because
these result from sampled combinations (`true`, `false`) and (`true`, `true`). But the outcome is unspecified
when `coin1` is `false`. That possibility has probability `2 / 4`, and it is assigned to an event
named `unknown`. You can extend this program with other experiments that sample from distribution
`incomplete()`. Those experiments will have the unknown event as outcome when they sample
the unknown event from distribution `incomplete()`.

If the unknown event is not interesting to you, you can also calculate the conditional
probability distribution that assumes that the unknown event does not happen. To do
this, replace the last line of the example code by:

    print incomplete().known();

Now the unknown event is omitted. The result is:

    false  1
     true  1
    --------
    total  2

### Counting possibilities

If you are teaching mathematics, you may want to tell your students that probability
is about counting possibilities. Please consider the following counting-language program:

    experiment exp() {
        labeledDice = distribution 1, 1, 1, 2, 2, 2;
        sample d1 from labeledDice;
        sample d2 from labeledDice;
        return d1 + d2;
    };
    print exp();

The output is:

        2  1
        3  2
        4  1
    --------
    total  4

You see that counting-language has reduced the fractions to lowest terms. For each dice, each
possiblie value has probability 3/6 = 1/2. Each combination (sequence important) thus has probability
(1/2) * (1/2) = 1/4. This may not be what you want. A unique possibility arises as the combination
of two specific sides of the dice. If you want to take this into account, you can change the first
line to be `possibility counting experiment exp() {`. The program becomes:

    possibility counting experiment exp() {
        labeledDice = distribution 1, 1, 1, 2, 2, 2;
        sample d1 from labeledDice;
        sample d2 from labeledDice;
        return d1 + d2;
    };
    print exp();

The output becomes:

        2   9
        3  18
        4   9
    ---------
    total  36

Now we are counting combinations of dice sides, totalling 6 * 6 = 36 possibilities.

There is a caveat here. Consider the following counting-language program:

    possibility counting experiment exp() {
        labeledDice = distribution 1, 1, 1, 2, 2, 2;
        coin = distribution 1, 2;
        sample d1 from labeledDice;
        d2 = 0;
        if(d1 == 1) {
            sample d2 from coin;
        } else {
            sample d2 from labeledDice;
        };
        return d1 + d2;
    };
    print exp();

Literally counting possibilities would be an erroneous application of probability theory.
For every possibility of throwing the first dice, there are two possibilities to throw a
coin and six possibilities to throw a second dice. You cannot compare these. Counting
possibilities here does not make sense. You should manipulate conditional probabilities,
which are all 1/2. counting-language can identify this error. It produces the following
message:

    ERROR: (9, 8): Tried to sample from 6 possibilities, but only 2 possibilities are allowed, because from that amount was sampled at (7, 8)

The program works again when you replace the first line by the following:
`experiment exp() {`. Now no error is produced and the output again is:

        2  1
        3  2
        4  1
    --------
    total  4


## Case studies

There are two case studies that show how counting-language can be used. See [risk](/counting-language-impl/scripts/cases/risk) and [game of the goose](counting-language-impl/scripts/cases/gooseGame).

## Language reference

Reference information about this language can be found [here](LANGUAGE_REFERENCE.md).

# Usage instructions

## Installation

For every release, an execuable .jar file is uploaded that can be executed with Java 8. To use it,
please do the following:

* Download the executable and put it in some directory, the work directory.
* Change directory to the working directory.
* Make a test input file, for `example.txt` in the work directory.
* Fill `example.txt` with the test `print 5+3;`.
* Run the following command: `java -jar &lt;name of executable&gt; example.txt`. This works both on Windows and on Linux, provided that the Java executable is on your path.

## Building the source code

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
* If you want to develop with Eclipse, you may want to add a Java 8 JDK (Windows | Preferences | Java | Installed JREs). If you don't, you may see too many warings because Eclipse is applying higher Java version.

# Implementation

counting-language is implemented using ANTLR 4 to parse the input. The syntax of the language is in `counting-language-base/src/main/antlr4/com/github/mhdirkse/countlang/lang/Countlang.g4`. ANTLR 4 generates interface `CountlangListener` and ensures that the methods of this interface are called. Please note that generated code appears in the `target` folders in Eclipse, each project having its own `target` folder.

The dependency `codegenPlugin` generates code that sets up a chain of responsibilities. Calls to CountlangListener are processed by the implementation `CountlangListenerDelegator`. This class calls instances of `CountlangListenerHandler` until a handler returns true. This chain of responsbilies is used to handle nested structures in the language. Almost each non-leaf node in the parse tree has its own handler. Using these structures an Abstract Syntax Tree is built. The AST does not do calculations and it also has no logic in it to verify validity of the parsed program. All these tasks are done with listeners which can be found in subproject `counting-language-impl`. Boiler-plate code for these listeners is also in code generated by `codegenPlugin`. See `counting-language-impl/target/generated-sources/codegen`.

For the grammar, two plugins have to generate code. First, ANTLR 4 has to create a listener and then `codegenPlugin` sets up a chain of responsibilities based on this listener. The following sub-projects are created to set this up:

* `counting-language-base`: Holds the grammar and the classes of the AST (abstract syntax tree).
* `counting-language-impl`: Implements parsing the language, sanity-checking the AST and doing the calculations.
* `counting-language-generator`: Dependency required by `codegenPlugin`. This code is not packaged in the final .jar, but applied by `codegenPlugin` to produce the generated code.
* `counting-language-generator-test`, `counting-language-generator-test-input`: Test code for counting-language-generator.
* `counting-language-base-testtools`. Common test code, used for testing `counting-language-base` and `counting-language-impl`
* `counting-language-base-test`. Holds the unit tests of `counting-language-base`. Both `counting-language-base-test` and `counting-language-impl` use dependency `counting-language-base-testtools` as a scope test dependency. `counting-language-base-testtools` uses dependency `counting-language-base`.

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

For details, see the [JavaDoc](https://mhdirkse.github.io/counting-language/).
