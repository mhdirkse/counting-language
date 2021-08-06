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
    DISTRIBUTION_RETURN_TYPE_MISMATCH("({1}, {2}): Type of return value {3} does not match return type of experiment: {4}."),
    
    OPERATOR_ARGUMENT_COUNT_MISMATCH("({1}, {2}): Argument count mismatch for operator {3}. Expected {4}, got {5}."),
    OPERATOR_TYPE_MISMATCH("({1}, {2}): Type mismatch using operator {3}."),

    VAR_NOT_USED("({1}, {2}): Variable {3} is not used."),
    VAR_UNDEFINED("({1}, {2}): Undefined variable {3}."),
    VAR_TYPE_CHANGED("({1}, {2}): Cannot change type of variable {3}."),
    DUPLICATE_PARAMETER("({1}, {2}): Cannot reuse parameter name {3}."),
    IF_SELECT_NOT_BOOLEAN("({1}, {2}): Selector of if statement must be BOOL, but was {3}."),
    WHILE_TEST_NOT_BOOLEAN("({1}, {2}): Test expression of while statement must be BOOL, but was {3}."),

    DISTRIBUTION_SCORED_VALUE_TYPE_MISMATCH("({1}, {2}): Element number {3} in distribution is {4}, should be {5}."),
    DISTRIBUTION_SCORED_COUNT_NOT_INT("({1}, {2}): Element number {3}, the count, in distribution is {4}, should be int."),
    DISTRIBUTION_AMOUNT_NOT_INT("({1}, {2}): The amount or unknown clause of a distribution should be int."),
    UNTYPED_DISTRIBUTION("({1}, {2}): Distribution should define its subtype."),
    SAMPLING_OUTSIDE_EXPERIMENT("({1}, {2}): Sampling is only allowed within an experiment."),
    SAMPLED_FROM_NON_DISTRIBUTION("({1}, {2}): The value you sample from is a {3}, but should be DISTRIBUTION."),
    EMPTY_COLLECTION_IS_PRIMITIVE("({1}, {2}): Cannot build an empty collection from a primitive type id."),
    ARRAY_ELEMENT_TYPE_MISMATCH("({1}, {2}): Array element {3} has invalid type {4}, expected {5}."),
    MEMBER_OF_NON_ARRAY("({1}, {2}): Cannot get a member from something that is not an array."),
    MEMBER_INDEX_NOT_INT("({1}, {2}): An array index should be integer.");

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
