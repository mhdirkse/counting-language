Apart from the probability theory features, counting-language supports the following:

* Integer +, -, *, /, with / such that the result is rounded towards zero.
* Unary - operator.
* You can manage the order of expression evaluation with brackets.
* Logical and, or, not.
* Relational operators <, >, <=, >=, ==, !=.
* You can define variables of type int, boolean or some distribution.
* Distribution types are defined like `distribution<int>`, `distribution<bool>` or `distribution<distribution<...>>` where the dots have to be replaced by any type.
* Values of type distribution are defined like `distribution 1, 2, 3` for a non-empty `distribution<int>` or just `distribution<int>` for the empty distribution containing integers.
* There are short-hand notations for distribution literals according to the following rules:
    * For values of type `distribution<bool>` you do not have to summarize both true and false. You can for example say `distribution 3 of false total 10` for a Bernoulli distribution with 30% chance of failure.
    * You can write literal values for incomplete distributions like `distribution 1 unknown 2`.
    * You can do the same by making the total explicit: `distribution 1 total 3`.
    * For values of type `distribution<bool>`, you can do `distribution false, true total 3` to make your distribution incomplete.
* Variables defined between `{` and `}` go out of scope when the statement block ends.
* You can define functions that are required to return a value (contrary to experiments).
* There is an ``if``-statement.
* There is a ``while`` statement.

For details, see the [grammar](/counting-language-base/src/main/antlr4/com/github/mhdirkse/countlang/lang/Countlang.g4).

Here are a few things to take care of when programming:
* Between two statements, you always need a `;`, also if the first statement ends with `}`.
* When you subtract a literal positive number, precede that number by a space. For example, the statement `print 1-1;` wont work. The text `-1` is interpreted as a negative number and then there is no operator between the numbers `1` and `-1`. The expression `print 1 - 1;` works. 
