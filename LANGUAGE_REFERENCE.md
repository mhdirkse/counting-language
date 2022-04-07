# Data types

Counting-language is an interpreted language. It is statically typed, but types of variables are inferred. Types only need to be defined for parameters of functions, procedures and experiments. Each value or variable can have one of the following types: `int`, `bool`, `fraction`, `distribution`, `array` or `tuple`.

### bool

A `bool` value can only have one of the following two values: `false` or `true`. Relational operators (e.g. `==`, `<`, `>`) return values of type `bool`. You can calculate with `bool` values with the Boolean operators (e.g. `and`, `or`, `not`).

The argument of `if` or `while` statements is required to be of type `bool`. The following program illustrates this:

    x = 1;
    if(x == 1) {
        print x;
    };

Unlike in JavaScript, the second line should not be `if(x)` because `x` is of type `int`.

### int

Represents a whole number (e.g. -3, 0 or 5). Unlike in many programming language, there is no bound on `int` values. You can apply the binary arithmetic operators `+`, `-`, `*`, `div` to `int` values. Operator `div` rounds towards zero as can be illustrated by the following program:

    # Should be 3
    print 7 div 2;
    # Should be -3
    print -7 div 2;

There is also an operator `/`, but it does not do what you would expect from other programming languages. It produces values of type `fraction`.

You can construct negative numbers using the unary `-` operator, for example: `print -5`.

### fraction

Counting-language does not have floating-point numbers. It only works with exact numbers, including fractions. For example, a probability of 49.6% can be represented as the fraction `49 + 6 / 10`. Use operator `/` to construct fractions from integers.

You can apply the binary operators `+`, `-`, `*` and `/` to fractions. For example `print 3 + (3/2) / (3/2) - 1` produces `3`.

You can also apply the relational operators. Sinse fractions are exact, you do not have to care about rounding errors like you would have to with floating-point numbers. You can for example do `print (3/5) == (303/505);` and expect a result of `true`.

If you do a lot of calculations with fractions, the numerator and the denominator tend to become very large. Consider the following program:

    result = 1/1;
    for i in [1:20] {
        result = result * i / (2*i + 1);
    };
    print result;
    
The output is: `262144 / 1412926920405`. This looks impressive, but doesn't reveal the magnitude of the result. You can see the magnitude when you replace the last line by: `print approx result;`. The result becomes `185.5E-9`. For example, if this were a length in meters, you could pronounce it as 185.5 nanometer. `print approx` expresses results in so-called engineering-notation, which means that the exponent is always a multiple of three. The `print approx` statement can be applied to any value in counting-language.

Please mind the first line! If you would replace it with `result = 1`, you would get a type mismatch because `1` is of type `int`, not `fraction`.

### distribution

A distribution can be interpreted in two ways. It can be seen as a probability distribution and also as a special kind of set in which the same element can appear multiple times.

When you print a distribution, it looks like a probability distribution: The statement `print distribution 10, 20;` produces:

       10  1
       20  1
    --------
    total  2

If a distribution appears as the element of some composite type, it is formatted in a more simple way. The statement `print [distribution 1 total 2];` produces:

    [(1, unknown)]

A distribution type needs to specify the type of the elements. Construction a distribution and expressing a distribution type can be expressed by the following program:

    d = distribution 1, 2;
    
    function addOneAndThree(distribution<int> arg) {
        return arg.addAll(distribution 1, 3);
    };
    
    # Should be a distribution with two times 1, one time 2, one time 3.
    print addOneAndThree(d);
    # Should have only 1 and 3 once.
    print addOneAndThree(distribution<int>);

The last line illustrates how to express an empty distribution: you need to write the distribution type including the type of the elements.

A distributions also maintains how many elements it contains in total. Distributions can have unknown elements: elements for which no value has been specified. Here are some examples:

    # Contains value 1 once and one unknown value.
    print (distribution 1 total 2);
    # Should be true
    print (distribution 1 total 2) == (distribution 1 unknown 1);
    # Should contain element 1 once, no unknowns
    print (distribution 1 total 2).known();

Counting-language can work with sets: these are distributions without unknowns in which each element can appear only once. You can use the `isSet()` member function to check whether a distribution is a set:

    # Should be true
    print (distribution 1, 3).isSet();
    # Should be false
    print (distribution 1, 1).isSet();
    # Should be false
    print (distribution 1 unknown 1).isSet();
    # Should be true
    print (distribution 1 unknown 1).known().isSet();

Counting-language behaves specially when you create distributions with `bool` elements. When you use the `total` clause, the missing values are not unknowns but they are the opposite of the value that is added explicitly. Here is an example:

	print distribution 3 of true total 5;

You add the value `true` three times. The negative of `true` is `false`. The total is `5`, so there are `5 -3 == 2` implicit elements. Therefore, this distrubiton has the value `false` two times. This feature is very useful if you want to sample from Bernoulli experiments.

The syntax with `of` does not only apply for `bool` elements. The statement `print distribution 3 of 10, 2 of 20;` produces:

       10  3
       20  2
    --------
    total  5

You can convert a `distribution` to an `array` by sorting the elements, for example:

    # Should be [1, 1, 3]
    print (distribution 1, 1, 3).ascending();
    # Should be [3, 1, 1]
    print (distribution 1, 3, 1).descending();

### array

An array is a list of elements that all have the same type. Array indices start with one. To express an array type, you need to mention the type of the elements. All of this can be illustrated by the following program:

    a = [10, 5, 20];
    
    function incAll(array<int> arg) {
        result = int[];
        for element in arg {
            result = result.add(element + 1);
        };
        return result;
    };
    
    # Should be [11, 6, 21]
    print incAll(a);

This program also illustrates a few other features of counting-language. First, there are no pointers like in Java or Python. In Java, for example, an array variable is actually a pointer to the list of elements. In Java, if you assign an array variable `a` to an array variable `b`, updating `b` implicitly updates `a` because they both reference the same memory. This is not the case in counting-language. Second, all values in counting-language are immutable. You cannot change an array after creating it. This is illustrated by the line: `result = result.add(element + 1);`. The member function `add` does not change `result`, but creates a new array. You need to assign that new array to the variable `result`, otherwise the statement would have no effect. Counting-language helps you by producing an error message if you make this mistake.

You can use the `[]` operator to get elements from an array, for example:

    a = [10, 20, 30];
    # Should be 10
    print a[1];
    # Should be 30
    print a[3];

Please note that indices start with one.

### tuple

A tuple is like a record, but without field names. A tuple is a list of fixed length in which all elements can have a different type. Tuples allow you to define functions with multiple return values. Tuples are also important when you are calculating probability distributions about complex events.

Functions with multiple return values can be illustrated with the following program:

    function next(tuple<bool, int> current) {
        toggleEven, number = current;
        return not toggleEven, number+1;
    };
    
    # Should be (false, 1)
    print next(tuple true, 0);
    # Should be (true, 2)
    print next(next(tuple true, 0));
    # Should be 1
    print next(tuple true, 0)[2];

    t = next(tuple false, 5);
    # Should be true
    print t[1];
    # Should be 6
    print t[2];

To define distributions about complex events, you typically create distributions of tuples. This is illustrated in the case studies [risk](/counting-language-impl/scripts/cases/risk) and [game of the goose](counting-language-impl/scripts/cases/gooseGame).

### Using ranges

Counting-language allows you to express ranges of values with the `:` operator. You cannot assign ranges to variables, but you can use ranges to:
* Instantiate arrays.
* Instantiate distributions.
* Dereference arrays or tuples.

Here are some examples:

    a = [4:6];
    # Should be [4, 5, 6]
    print a;
    # Should be [5, 6]
    print a[2, 3];

	d = distribution 2 of 4:6;
	# Should have values 4, 5 and 6, all two times, making a total of 6
    print d;

You can also define the step in a range, for example:

    # Should be [3, 5, 7]
    print [3:2:7];

The step appears between two `:` operators with the start and the end left and right to it. Please note that a range includes the end you define. This is unlike Python: In Pythone, the statement `print range(3, 5)` produces `[3, 4]`.

Ranges can also be created from fractions:

	# Should be [1 / 2, 5 / 6, 1 + 1 / 6]
	print [(1/2):(1/3):(8/6)];

# Functions, procedures and experiments

The difference between functions, procedures and experiments can be summarized as follows:

---------- --------- --------------------------- ---------------------------------
kind       return    Value after `return`        Caller gets
---------- --------- --------------------------- ---------------------------------
function   mandatory mandatory, say of type `T`  value of type `T`
experiment optional  optional, say of type `T`   value of type `distribution<T>`
procedure  optional  prohibited                  no value
---------- --------- --------------------------- ---------------------------------

* Multiple return values.
* Return statement optional / mandatory.
* Value returned versus return type.
* Sampling, sampling multiple values.

# Repetition and conditional execution

# Operators

# Predefined functions

# Output

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
