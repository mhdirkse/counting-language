package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

public final class TestOutputStrategy implements OutputStrategy {
    private final List<String> lines = new ArrayList<String>();

    @Override
    public void output(String s) {
        lines.add(s);
    }

    public int getNumLines() {
        return lines.size();
    }

    public String getLine(final int index) {
        return lines.get(index);
    }
}
