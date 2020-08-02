# counting-language
Simple programming language to manipulate integer values. In counting-language, you can write programs that add, subtract, multiply and divide integers. You can declare scalar variables and functions. Clear error messages are produced when your program contains errors. My intention is to extend the language into a domain specific language (DSL) for probability theory.

# Usage instructions

* Ensure that git and Maven are installed on your PC.
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

Here are a few things to take care of when programming:
* Between two statements, you always need a `;`, also if the first statement ends with `}`.
* If you have an `else` clause in an `if`-statement, then it may not be empty. But if you have no need for `else`, you can omit it alltogether.
 
# Implementation

counting-language is implemented using ANTLR 4 to parse the input. The syntax of the language is in `counting-language-base/src/main/antlr4/com/github/mhdirkse/countlang/lang/Countlang.g4`. ANTLR 4 generates interface `CountlangListener` and ensures that the methods of this interface are called. Please note that generated code appears in the `target` folders in Eclipse, each project having its own `target` folder.

The dependency `codegenPlugin` generates code that sets up a chain of responsibilities. Calls to CountlangListener are processed by the implementation `CountlangListenerDelegator`. This class calls instances of `CountlangListenerHandler` until a handler returns true. This chain of responsbilies is used to handle nested structures in the language. Almost each non-leaf node in the parse tree has its own handler. Using these structures an Abstract Syntax Tree is built. The AST does not do calculations and it also has no logic in it to verify validity of the parsed program. All these tasks are done with listeners which can be found in subproject `counting-language-impl`. Boiler-plate code for these listeners is also in code generated by `codegenPlugin`. See `counting-language-impl/target/generated-sources/codegen`.

For the grammar, two plugins have to generate code. First, ANTLR 4 has to create a listener and then `codegenPlugin` sets up a chain of responsibilities based on this listener. The following sub-projects are created to set this up:

* `counting-language-base`: Holds the grammar and the classes of the AST.
* `counting-language-impl`: Implements parsing the language, sanity-checking the AST and doing the calculations.
* `counting-language-generator`: Dependency required by `codegenPlugin`. This code is not packaged in the final .jar, but applied by `codegenPlugin` to produce the generated code.
* `counting-language-generator-test`, `counting-language-generator-test-input`: Test code for counting-language-generator.

Project `counting-language-base` has the following packages:
* `com.github.mhdirkse.countlang.ast`: Holds the AST.
* `com.github.mhdirkse.countlang.utils`: Utilities.
* `com.github.mhdirkse.countlang.lang`: Code generated by ANTLR 4 from the grammar.

Project `counting-language-impl` has the following packages:
* `com.github.mhdirkse.countlang.execution`: Helper classes to support executing an AST.
* `com.github.mhdirkse.countlang.cmd`: The command line interface of counting-language.
* `com.github.mhdirkse.countlang.lang.parsing`: Implements parsing source code written in the counting-language language.
* `com.github.mhdirkse.countlang.tasks`: Tasks to be performed by the counting-language software, independent from the user interface applied to call them.
