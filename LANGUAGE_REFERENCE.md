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

    a = [4:7];
    # Should be [4, 5, 6, 7]
    print a;
    # Should be [5, 5, 6, 7]
    print a[2, 2:4];

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

kind       | `return`  | Value after `return`        | Caller gets
---------- | --------- | --------------------------- | ---------------------------------
function   | mandatory | mandatory, say of type `T`  | value of type `T`
procedure  | optional  | prohibited                  | no value
experiment | optional  | optional, say of type `T`   | value of type `distribution<T>`

If this is not clear, please read on. Everything is explained below.

### Functions and procedures

Functions are like functions in other programming language, but they are required to return a value.
If you need a to call a block of code without a return value, then put it in a `procedure`.

Here are a few examples:

    function inc(int v) {
        return v + 1;
    };
    
    procedure printIncValue(int v) {
        print v + 1;
    };

    # Should be 1
    print inc(0);
    # Should print 1
    printIncValue(0);

Within a procedure, you can use `return` statements without a value:

    procedure printAbs(int v) {
        if(v < 0) {
            print -v;
            return;
        };
        print v;
    };

    # Should print 1
    printAbs(-1);
    # Should print 1
    printAbs(1);

This is not allowed in a function as is shown in the table at the top of this section.

### Experiments

Experiments are specific to counting-language. When you call an experiment, you
get a probability distribution. Within the body of the experiment, you can have
a `return` statement. If it returns a value, the value is an *element* of
the returned distribution, not the complete result as would be the case for
a `function`. Counting-language calculates the count of the element for
you, based on the rules of probability theory.

An experiment lets you sample probability variables from give probability
distributions. The experiment body expresses what transformation happens to
the sampled values. When a return statement is encountered, the value
returned expresses the event you have for the values sampled so far.
Counting-language keeps track of the probability of the event and
updates the result probability distribution. This result distribution
is the output the caller gets from calling an experiment.

Here is a simple example. Say we throw to coins and say we want
to know the probability of having two times "head". We sample
two times from `distribution false, true`. The event we are
interested in is: do we have two heads yes or no, or two times
`true`. The transformation of the values is just the `and`
operator. We have:

    experiment twoTimesHead() {
        sample first from distribution false, true;
        sample second from distribution false, true;
        return first and second;
    };

	print twoTimesHead();

This program prints the following:

    false  3
     true  1
    --------
    total  4

There are four possibilities with equal probability: (false, false), (false, true), (true, false), (true, true).
Only one of them satisfies `first and second`, so `true` is scored one time. The other combinations
produce `false`, three times. The bottom line shows that there are four combinations. You can interpret
this by saying that result event `true` has a probability of `1/4`.

You can allow unknown values in your result distribution by using `return`
statements without a value or by omitting the return statement. Say we are throwing
a coin and that head produces `1` and tail produces `-1`. We want to know:
how many rolls does it take until the sum of the obtained number has reaches `2`.
Or more precisely: we want the probability distribution of the amount of rolls.

We could express this in the following experiment:

    experiment throwCoins(int numTries) {
        coinSum = 0;
        numRolls = 0;
        repeat(numTries) {
            sample coin from distribution -1, 1;
            coinSum = coinSum + coin;
            numRolls = numRolls + 1;
            if(coinSum == 2) {
                return numRolls;
            };
        };
    };

    print throwCoins(1);

If coins are sampled that never add up to `2`, no `return` statement is executed.
Therefore the unknown event is scored. The program below produces a distribution
in which the unknown event has probability `1`. With one throw, you will never
reach `2` because the coin only has values `1` and `-1`.

If we replace the last line by `print throwCoins(4);`, we throw at
most four coins and we want the number of rolls until we reach `2`.
The result is:

          2  2
          4  1
    unknown  5
    ----------
      total  8

The probability that two rolls is enough is `2/8`, which is `1/4`. This is the
result we found earlier when we considered the probability of having two times
"head". The probability that we need four rolls is `1/8`,
because value `4` is scored once.

The probability of `unknown` is `5/8`. This is: a total of `2` is not reached
within four rolls.

Please note that the bottom line shows a total of `8`. This may surprise you,
because with four coins you have `2 ^ 4 = 16` combinations. This can
be explained, because counting-language 'normalizes the fractions` by default.
If you are want to see the number of combinations without fraction normalization,
you can replace the first line with: `possibility counting experiment throwCoins(int numTries) {`.

When you execute:

    possibility counting experiment throwCoins(int numTries) {
        coinSum = 0;
        numRolls = 0;
        repeat(numTries) {
            sample coin from distribution -1, 1;
            coinSum = coinSum + coin;
            numRolls = numRolls + 1;
            if(coinSum == 2) {
                return numRolls;
            };
        };
    };

    print throwCoins(4);

you get:

          2   4
          4   2
    unknown  10
    -----------
      total  16

The probabilities are the same: `4/16 = 1/4`, `2/16 = 1/8` and `10/16 = 5/8`.

We finish this section with another syntax of the sample statement.
You can sample multiple independent values from the same distribution
as can be shown by rewriting our example:

    experiment throwCoins(int numTries) {
        # Produces an array of four values
        sample coins as numTries from distribution -1, 1;
        for numTriesConsidered in [1:numTries] {
            # Take numTriesConsidered coins and add them
            coinSum = coins[1:numTriesConsidered].unsort().sum();
            if(coinSum == 2) {
                return numTriesConsidered;
            };            
        };
    };
    print throwCoins(4);

Do not mind the call to `unsort()`. It just transforms an array to a distribution,
because only a distribution has a `sum()` function. This is something that should
be improved in the language.

### Returning and assigning multiple values

A `return` statement can return multiple values. They are implicitly grouped in a tuple as can be illustrated as follows:

    function fun() {
        return 3, true;
    };
    
    v = fun();
    
    procedure proc(tuple<int, bool> arg) {
        print arg;
    };

    # Should print 3, true
    proc(v);

Tuples can be dereferenced with the `[]` operator as was explained in the section on type `tuple`.
You can also use a special syntax of the assignment statement:

    function fun() {
        return 3, true;
    };

    intValue, boolValue = fun();

    # Should be 3
    print intValue;
    # Should be true
    print boolValue
    
This tuple dealing syntax can also be used in `sample` statements:

    experiment exp() {
        d = distribution (tuple 1, false), (tuple 2, true);
        sample intValue, boolValue from d;
        if(boolValue) {
            return intValue;
        };
    };

    # Should produce a distribution with two values: 2 and unknown.
    print exp();

Counting-language checks that every variable you write is used. If you do not use a value you
take from a tuple, using a dummy variable is an error. Instead, use `_` as a placeholder:

    experiment exp() {
        d = distribution (tuple 1, false), (tuple 2, true);
        sample intValue, _ from d;
        return intValue;
    };

    # Should produce a distribution with values 1 and 2
    print exp();

# Repetition and conditional execution

Repetition and conditional execution is quite straightforward. These features can be summarized by the following table:

Statement                                        | Explanation
------------------------------------------------ | -----------
`if (` bool value `) {` ... `};`                 | Conditional execution
`if (` bool value `) {` ... `} else {` ... `};`  | Conditional execution
`while (` bool value `) {` ... `};`              | Repeat while `bool` condition; check before executing body
`for` variable `in` array `{` ... `};`           | Repeat for each value in array
`repeat (` integer value `) {` ... `};`          | Repeat a number of times

Please mind the `;` behind the `}` symbols, they are mandatory.

# Variables and their scope

Variables can hold values of type `int`, `bool`, `fraction`, `distribution`, `array` or `tuple`
as was explained in the section on data types. You can assign a new value to an existing
variable:

    v = 3;
    # prints 3
    print v;
    v = 5;
    # prints 5
    print v;

It is an error to overwrite a variable that has not been used.

    v = 3;
    v = 5;
    print v;

produces

    ERROR: (1, 0): Variable v is not used.

This is why you may need the placeholder `_` to unpack tuples:

    v, _ = tuple 3, true;
    # Prints 3
    print v

See also the section on returning and assigning multiple values.

You cannot modify a variable, only overwrite it. You cannot do something like `a = [1, 2, 3]; a[2] = 4; print a;`;
the second statement is invalid. This is how you should update an array:

    a = [1, 2, 3];
    b = int[];
    b = b.add(a[1]);
    b = b.add(4);
    b = b.add(a[3]);
    # Should print [1, 4, 3]
    print b;

Similar logic is needed to update distributions. You need predefined functions,
which are explained later.

Each time counting-language enters a `{` ... `}` block, it starts a new scope.
After the block, variables that were defined in that scope go out of scope.
The program `{v = 3}; print v` produces the following errors:

    ERROR: (1, 1): Variable v is not used.
    ERROR: (1, 15): Undefined variable v. 

Within a block, you can refer to variables in outer scopes:

    v = 3;
    {
        # Should print 3
        print v;
        # Refers to the existing variable v
        v = 5;
    };
    # Should print 5
    print v;

But within a function, procedure or experiment, you can only refer to variables defined within the
body and to parameters. The program `v = 3; procedure proc() {print v}; proc()` produces
the following errors:

    ERROR: (1, 0): Variable v is not used.
    ERROR: (1, 31): Undefined variable v.

Within the body of a function, procedure or experiment, you can reuse symbols without
causing naming conflicts:

    v = 3;
    procedure proc() {
        # Another variable, also called v
        v = 5;
        print v;
    };
    # prints 5, refers to the variable v defined in the procedure
    proc();
    # Refers to the variable defined to the top, prints 3
    print v;

# Output

The only way to output values is using the `print` statement. You can only print
values of type `int`, `bool`, `fraction`, `distribution`, `array` or `tuple`,
which implies that you have limited control over the output format: no
string manipulations. The following table summarizes the `print` statement:

Statement          | Explanation
------------------ | --------------------------------------------
`print` ...        | Print the exact value, may be very long
`print exact` ...  | Same
`print approx` ... | Print approximation in engineering notation

Engineering notation means scientific notation with the decimal point adjusted such that the exponent is a multiple of three.
Here are a few examples:

Value type        | exact              | approx
----------------- | ------------------ | ----------
fraction          | `1 / 10000`        | `100.0E-6`
fraction          | `1 / 1000`         | `1.000E-3`
fraction          | `1 / 100`          | `10.00E-3`
fraction          | `1 / 10`           | `100.0E-3`
fraction          | `1`                | `1.000E0`
fraction          | `10000`            | `10.00E3`
int               | `1`                | `1`
int               | `10000`            | `10000`

You see here that `print approx` still prints exact values of integers. `print approx` also
approximates counts and the total of a distribution. The statement `print approx distribution 1 of true total 1000000;`
outputs:

    false   1.000E6 (1.000E0)
     true  1.000E0 (1.000E-6)
    -------------------------
    total         1.000E6 (1)

The count is shown in engineering notation, and the probability is added between parenthesis.

# Operators

The following table shows the operators you can apply in counting-language:

Operator | Example    | Meaning                                | Value types                          | Return type
-------- | ---------  | -------------------------------------- | ------------------------------------ | ---------------------------------------------------
`+`      | `5 + 3`    | Addition                               | int or fraction                      | fraction if one argument is fraction, otherwise int
`-`      | `5 - 3`    | Subtraction                            | int or fraction                      | fraction if one argument is fraction, otherwise int
`-`      | `-5`       | Construct negative value               | int or fraction                      | same as argument       
`*`      | `5 * 3`    | Multiplication                         | int or fraction                      | fraction if one argument is fraction, otherwise int
`div`    | `10 div 3` | Integer division                       | int                                  | int
`/`      | `2 / 3`    | Fraction creation or fraction division | int or fraction                      | fraction
`<`      | `1 < 2`    | Less than                              | both have same type, int or fraction | bool
`<=`     | `1 <= 2`   | Less than or equal                     | both have same type, int or fraction | bool
`>`      | `1 > 2`    | Greater than                           | both have same type, int or fraction | bool
`>=`     | `1 >= 2`   | Greater than or equal                  | both have same type, int or fraction | bool
`==`     | `[1, 2] == [1, 2]` | Equals                         | both have same type                  | bool
`!=`     | `[1, 2] != [1, 2]` | Does not equal                 | both have same type                  | bool
`not`    | `not true` | Boolean negation                       | bool                                 | bool
`and`    | `true and false` | Boolean and                      | bool                                 | bool
`or`     | `true or false`  | Boolean or                       | bool                                 | bool
`:`      | `3:5`, `3:2:7`   | Range construction               | all have same type, int or fraction  | not applicable, see subsection on ranges

The integer division operator rounds towards zero as shown by the following example:

    # Should be 3
    print 7 div 2;
    # Should be -3
    print -7 div 2;

Like in many other languages, the operators have priorities. For example, the expression `5 + 3 * 2`
is executed as if it were `5 + (3 * 2)` and hence the result is `11`. Operators from equal priority
are executed from left to right: `5 - 3 + 2` produces `4`. The following table lists the priorities
of the operators, from high to low:

| Operators |
| --------- |
| unary `-` |
| `*` `div` `/` |
| `+` `-` |
| `<` `<=` `>` `>=` |
| `not` |
| `and` |
| `or` |
| `:` |

You are encouraged to use parentheses in your expressions when in doubt about operator precedence.

# Predefined functions

### Predefined member functions of distributions

Each value of type `distribution` has predefined member functions. These functions do not modify
the distribution but they produce a new value, possibly another distribution. This
is illustrated by the following example:

    d1 = distribution 1, 2;
    d2 = d1.addAll(distribution 1, 3);
    # Should print a distribution with two times 1, once 2 and once 3
    print d2;
    # Should proint a distribution with once 1 and once 2
    print d1;

Here are all predefined member functions of a distribution:

Name | Example | Result of example | Fails if | Description
---- | ------- | ----------------- | -------- | -----------
`addAll` | `(distribution 1, 2 total 3).addAll(distribution 1, 3 total 4)` | `distribution 2 of 1, 2, 3 total 7` | - | Join two distributions
`ascending` | `(distribution 1, 2).ascending()` | `[1, 2]` | has unknowns | Convert to array, sorting elements in ascending order
`descending` | `(distribution 1, 2).descending()` | `[2, 1]` | has unknowns | Convert to array, sorting elements in descending order
`contains` | `(distribution 1, 2, 2).contains(distribution 2, 2)` | `true` | argument has unknowns | Test whether argument distribution is contained in original distribution
`countOf` | `(distribution 1, 3, 3).countOf(3)` | `2` | - | Get count of element
`countOfUnknown` | `(distribution 1 total 3).countOfUnknown()` | `2` | - | Get number of unknown elements
`E` | `(distribution 1, 2).E()` | `1 + 1 / 2` | Elements are not int and not fraction, or distribution empty, or distribution has unknowns | Get expected value
`sum` | (distribution 1, 2, 2).sum() | `5` | Elements are not int and not fraction, or distribution has unknowns | Get sum of elements
`hasElement` | `(distribution 1, 2).hasElement(1)` | `true` | - | Test whether distribution has element
`hasUnknown` | `(distribution 1 total 3).hasUnknown()` | `true` | - | Test whether distribution has unknowns
`intersect` | `(distribution 1, 1, 3).intersect(distribution 1, 2)` | `distribution 1` | Any of the two distributions has unknowns | Intersect two distributions
`isSet` | `(distribution 1, 2).isSet()` | `true` | - | Test that there are no unknowns and that every element is unique
`known` | `(distribution 1, 2, 2 unknown 2).known()` | `distribution 1, 2, 2` | - | Remove all unknowns
`probabilityOf` | `(distribution 1, 2).probabilityOf(1)` | `1 / 2` | distribution empty | Get the probability of a value
`probabilityOfAll` | `(distribution 1, 2, 3).probabilityOfAll(distribution 1, 2)` | `2 / 3` | distribution is empty or argument is not a set | Get probability of any of the argument's values
`probabilityOfUnknown` | `(distribution 1 unknown 1).probabilityOfUnknown()` | `1 / 2` | distribution is empty | Get probability of the unknown value
`removeAll` | `(distribution 1, 2, 2).removeAll(distribution 1, 2)` | `distribution 1` | other distribution has unknown | Remove all items of the argument distribution from the original
`size` | `(distribution 1, 2, 2).size()` | `3` | - | Get number of elements
`toSet` | `(distribution 1, 2, 2 unknown 1).toSet()` | `distribution 1, 2` | - | Remove unknowns and make each element unique

### Predefined member functions of arrays

Each value of type `array` has predefined member functions. These functions do not modify
the array but they produce a new value, possibly another array. This
is illustrated by the following example:

    a = [1, 2];
    b = a.addAll([3, 4]);
    # prints [1, 2]
    print a;
    # prints [1, 2, 3, 4]
    print b;

The following table lists all the member functions predefined for arrays:

Name | Example | Result of example | Description
---- | ------- | ----------------- | -----------
`add` | `[1, 2].add(3) | [1, 2, 3] | Append value to the end
`addAll` | `[1, 2].addAll([3, 4]) | `[1, 2, 3, 4]` | Add argument's members to the end of original array.
`ascending` | `[1, 3, 2].ascending()` | `[1, 2, 3]` | Sort elements in ascending order
`descending` | `[1, 3, 2].descending()` | `[3, 2, 1]` | Sort elements in descending order
`ascendingIndicesOf` | `[1, 3, 2, 3].ascendingIndicesOf(3)` | `[2, 4]` | Get indices where argument is found in original array
`delete` | `[10, 20, 30].delete(2)` | `[10, 30]` | Remove element at index
`max` | `[1, 3, 2, 3].max()` | `3` | Get maximum value
`min` | `[1, 3, 2, 3].min()` | `1` | Get minimum value
`maxRef` | `[10, 40, 30, 20].maxRef()` | `2` | Get index of maximum value
`minRef` | `[10, 40, 30, 20].minRef()` | `1` | Get index of minimum value
`reverse` | `[1, 2, 3]` | `[3, 2, 1]` | Reverse elements
`size` | `[10, 20, 30]`.size() | `3` | Get number of elements
`unsort` | `[10, 10, 20].unsort()` | `distribution 2 of 10, 20` | Convert to distribution
`update` | `[10, 20, 30].update(2, 40)` | `[10, 40, 30]` | Update value at index

All examples shown here are about integer arrays, but you apply these functions on any array. Consider
for example:

    # Should print (1, 1, 3), (1, 2), (1, 3)]
    print [(distribution 1, 3), (distribution 1, 2), (distribution 1, 1, 3)].ascending();

Counting-language defines a sort order for values of any type. This order is not accessible
with the operators `<`, `<=`, `>` and `>=` though.

### Predefined non-member functions

Counting-language also predefines functions that are not member functions.
They all take a fraction. In a future version of counting-language, they
mey become member functions, but presently they are not.

Here are the predefined functions that take a fraction:

Name        | Example               | Result of example | Description
----------- | --------------------- | ----------------- | ------------------------------------
numerator   | `numerator(4 / 10)`   | `2`               | Get numerator after simplification
denominator | `denominator(4 / 10)` | `5`               | Get denominator after simplification
isWhole     | `isWhole(10 / 2)` | `true` | Check whether a fraction is a whole number
properWhole | `properWhole(-7 / 3)` | `-2` | Round towards zero to a whole number

# Troubleshooting

Here are a few things to take care of when programming:
* Between two statements, you always need a `;`, also if the first statement ends with `}`.
* When you subtract a literal positive number, precede that number by a space. For example, the statement `print 1-1;` won't work. The text `-1` is interpreted as a negative number and then there is no operator between the numbers `1` and `-1`. The expression `print 1 - 1;` works. 
