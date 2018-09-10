package com.github.mhdirkse.countlang.tasks;

import java.util.Arrays;

import com.github.mhdirkse.utils.AbstractStatusCode;

class Status {
    private final AbstractStatusCode statusCode;
    private final String[] args;

    private Status(final AbstractStatusCode statusCode, final String... args) {
        this.statusCode = statusCode;
        this.args = Arrays.copyOf(args, args.length);
    }

    String format() {
        return statusCode.format(args);
    }

    static Status forLine(
            final AbstractStatusCode statusCode, final int line, final int column, final String... others) {
        String[] args = new String[others.length + 2];
        args[0] = Integer.valueOf(line).toString();
        args[1] = Integer.valueOf(column).toString();
        System.arraycopy(others, 0, args, 2, others.length);
        return new Status(statusCode, args);
    }
}
