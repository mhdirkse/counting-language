package com.github.mhdirkse.countlang.tasks;

import com.github.mhdirkse.utils.AbstractStatusCode;

import lombok.Setter;

public enum StatusCode implements AbstractStatusCode {
    TEST_LINE_STATUS_NO_EXTRA_ARGS("Test line {1} column {2}."),
    TEST_LINE_STATUS_ONE_EXTRA_ARG("Test line {1} column {2} with {3}."),

    RETURN_OUTSIDE_FUNCTION("({1}, {2}): Return statement outside function."),
    FUNCTION_DOES_NOT_RETURN("({1}, {2}): Function {3} does not return a value."),
    FUNCTION_STATEMENT_WITHOUT_EFFECT("({1}, {2}): Statement in function {3} has no effect."),
    FUNCTION_NESTED_NOT_ALLOWED("({1}, {2}): Nested functions not allowed."),
    FUNCTION_DOES_NOT_EXIST("({1}, {2}): Function {3} does not exist."),
    FUNCTION_ALREADY_DEFINED("({1}, {2}): Function {3} was already defined."),
    FUNCTION_ARGUMENT_COUNT_MISMATCH("({1}, {2}): Argument count mismatch calling {3}. Expected {4}, got {5}."),
    FUNCTION_TYPE_MISMATCH("({1}, {2}): Type mismatch calling function {3}, formal parameter {4}."),
    FUNCTION_RETURN_TYPE_MISMATCH("({1}, {2}): Type of return value {3} does not match return type {4}."),
    
    OPERATOR_ARGUMENT_COUNT_MISMATCH("({1}, {2}): Argument count mismatch for operator {3}. Expected {4}, got {5}."),
    OPERATOR_TYPE_MISMATCH("({1}, {2}): Type mismatch using operator {3}."),

    VAR_NOT_USED("({1}, {2}): Variable {3} is not used."),
    VAR_UNDEFINED("({1}, {2}): Undefined variable {3}."),
    VAR_TYPE_CHANGED("({1}, {2}): Cannot change type of variable {3}."),

    IF_SELECT_NOT_BOOLEAN("({1}, {2}): Selector of if statement must be Boolean, but was {3}."),

    DISTRIBUTION_SCORED_NOT_INT("({1}, {2}): Element number {3} in distribution is {4}, should be int."),
    DISTRIBUTION_AMOUNT_NOT_INT("({1}, {2}): The amount or unknown clause of a distribution should be int.");

    StatusCode(final String formatString) {
        this.formatString = formatString;
    }

    private String formatString;
    @Override
    public String getFormatString() {
        return formatString;
    }

    @Setter
    private static boolean isTestMode = false;
    @Override
    public boolean isTestMode() {
        return isTestMode;
    }
}
