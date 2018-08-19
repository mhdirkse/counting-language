# counting-language
Simple programming language to manipulate integer values. In counting-language, you can write programs that add, subtract, multiply and divide integers. You can declare scalar variables and functions. Clear error messages are produced when your program contains errors. My intention is to extend the language into a domain specific language (DSL) for probability theory.

# Usage instructions

* Ensure that git and Maven are installed on your PC.
* Clone counting-language and codegen.
* Choose the version of counting-language that you want to build. I recommend using the latest tagged version.
* In your counting-language checkout, checkout the version you want. Check it out using `git checkout`.
* In your counting-language pom.xml file, check what version of codegen is required.
* In your codegen copy, checkout that version.
* Build codegen using `mvn clean install`.
* Build counting-language using `mvn clean install`.
* In your counting-language checkout, go to `counting-language-impl/target/scripts`.
* Give yourself executable permission on the script `run.sh`.
* Run the program, for example as `./run.sh inputs/simpleAdd`.
* See the `inputs` directory for more examples.

# Implementation

counting-language is implemented using ANTLR 4 to parse the input. The syntax of the language is in `counting-language-base/src/main/antlr4/com/github/mhdirkse/countlang/lang/Countlang.g4`. ANTLR 4 ensures that the methods of interface `CountlangListener` are called. The dependency `codegenPlugin` generates code that sets up a chain of responsibilities. Calls to CountlangListener are processed by the implementation `CountlangListenerDelegator`. This class calls instances of `CountlangListenerHandler` until a handler returns true. This chain of responsbilies is used to handle nested structures in the language. Almost each non-leaf node in the parse tree has its own handler. Using these structures I build an Abstract Syntax Tree. The AST does the calculations.
