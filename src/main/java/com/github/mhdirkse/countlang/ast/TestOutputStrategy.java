package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

public final class TestOutputStrategy implements OutputStrategy {
    private final List<String> lines = new ArrayList<String>();
    private final List<String> errors = new ArrayList<String>();

    @Override
    public void output(final String s) {
        lines.add(s);
    }

    public int getNumLines() {
        return lines.size();
    }

    public String getLine(final int index) {
        return lines.get(index);
    }

    @Override
    public void error(final String s) {
        errors.add(s);
    }

    public int getNumErrors() {
        return errors.size();
    }

    public String getError(final int index) {
        return errors.get(index);
    }
}
