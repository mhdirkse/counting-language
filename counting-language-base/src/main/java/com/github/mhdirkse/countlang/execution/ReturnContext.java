package com.github.mhdirkse.countlang.execution;

import lombok.Getter;

class ReturnContext {
    private enum State {
        NO_RETURN_APPLICABLE,
        NO_RETURN_YET,
        RETURNED;
    }

    private State state;
    private Object returnValue;

    @Getter
    final int line;
    
    @Getter
    final int column;
    
    ReturnContext(final int line, final int column, final boolean withReturnValue) {
        this.line = line;
        this.column = column;
        if(withReturnValue) {
            state = State.NO_RETURN_YET;
        } else {
            state = State.NO_RETURN_APPLICABLE;
        }
    }

    boolean shouldContinue() {
        boolean result = false;
        switch(state) {
        case NO_RETURN_APPLICABLE:
        case NO_RETURN_YET:
            result = true;
            break;
        case RETURNED:
            result = false;
            break;
        }
        return result;
    }

    boolean haveReturnValue() {
        return state == State.RETURNED;
    }

    boolean expectReturnValue() {
        return state != State.NO_RETURN_APPLICABLE;
    }

    void setReturnValue(final Object returnValue) {
        if(returnValue == null) {
            throw new ProgramException(line, column, "Null returned, but null does not exist");
        }
        if(state == State.NO_RETURN_APPLICABLE) {
            throw new ProgramException(line, column, "No return statement allowed in this block of statements");
        }
        if(state == State.RETURNED) {
            throw new ProgramException(line, column, "Returned twice within the same function");
        }
        this.returnValue = returnValue;
        state = State.RETURNED;
    }
    
    Object getReturnValue() {
        if(state != State.RETURNED) {
            throw new ProgramException(line, column, "Return value requested, but it is not present");
        }
        return returnValue;
    }
}
