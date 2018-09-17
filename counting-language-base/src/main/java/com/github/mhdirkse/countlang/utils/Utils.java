package com.github.mhdirkse.countlang.utils;

public class Utils {
    private Utils() {
    }

    public static String formatLineColumnMessage(
            final int line, final int charPositionInLine, String msg) {
        return String.format("(%d, %d): ", line, charPositionInLine) + msg;
    }
}
