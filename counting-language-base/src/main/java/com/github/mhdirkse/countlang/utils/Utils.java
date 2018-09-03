package com.github.mhdirkse.countlang.utils;

public class Utils {
    private Utils() {
    }

    public static String formatLineColumnMessage(int line, int charPositionInLine, String msg) {
        return "line " + line + ":" + charPositionInLine + " " + msg;
    }
}
