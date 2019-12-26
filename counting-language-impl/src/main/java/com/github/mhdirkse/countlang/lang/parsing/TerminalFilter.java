package com.github.mhdirkse.countlang.lang.parsing;

class TerminalFilter extends AbstractTerminalHandler {
    private final TerminalFilterCallback callback;

    TerminalFilter(final TerminalFilterCallback callback) {
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
