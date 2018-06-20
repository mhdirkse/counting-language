package com.github.mhdirkse.countlang.tasks;

final class Constants {
    private Constants() {
    }

    static String INCREMENT_OF_MAX_INT_MINUS_ONE = String.format("print %d + %d", Integer.MAX_VALUE - 1, 1);
    static String MAX_INT = String.format("%d", Integer.MAX_VALUE);
    static String INCREMENT_OF_MAX_INT = String.format("print %d + %d", Integer.MAX_VALUE, 1);
    static String DECREMENT_OF_MIN_INT_PLUS_ONE = String.format("print %d - %d", Integer.MIN_VALUE + 1, 1);
    static String MIN_INT = String.format("%d", Integer.MIN_VALUE);
    static String DECREMENT_OF_MIN_INT = String.format("print %d - %d", Integer.MIN_VALUE, 1);
}
