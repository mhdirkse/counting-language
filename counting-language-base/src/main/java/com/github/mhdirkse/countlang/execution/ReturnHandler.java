package com.github.mhdirkse.countlang.execution;

public interface ReturnHandler {
    public void handleReturnValue(Object Value, int line, int column);

    public static final ReturnHandler NO_RETURN = new ReturnHandler() {
        @Override
        public void handleReturnValue(Object value, int line, int column) {
            throw new ProgramException(
                    line, column, String.format("No return statement is allowed. The value is %s", value.toString()));
        }
    };
}
