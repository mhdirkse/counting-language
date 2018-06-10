package com.github.mhdirkse.countlang.lang.parsing;

class TerminalStrategy2 extends AbstractTerminalHandler2 {
    private final TerminalStrategyCallback2 callback;

    TerminalStrategy2(final TerminalStrategyCallback2 callback) {
        this.callback = callback;
    }

    @Override
    public int getRequiredType() {
        return callback.getRequiredType();
    }

    @Override
    public void setText(final String text) {
        callback.setText(text);
    }
}
