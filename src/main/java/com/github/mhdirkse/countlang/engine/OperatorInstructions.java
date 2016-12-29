package com.github.mhdirkse.countlang.engine;

public final class OperatorInstructions {
    private OperatorInstructions() {
    }

    static abstract class BinOp<T> extends Instruction {
        final Class<T> clazz;

        BinOp(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        final void run(Stack stack) {
            T arg2 = clazz.cast(stack.pop());
            T arg1 = clazz.cast(stack.pop());
            stack.push(getResult(arg1, arg2));
        }

        abstract T getResult(final T arg1, final T arg2);
    }

    private static final class AddInt extends BinOp<Integer> {
        AddInt() {
            super(Integer.class);
        }

        @Override
        Integer getResult(final Integer arg1, final Integer arg2) {
            return Integer.valueOf(arg1.intValue() + arg2.intValue());
        }
    }

    private static final class SubtractInt extends BinOp<Integer> {
        SubtractInt() {
            super(Integer.class);
        }

        @Override
        Integer getResult(final Integer arg1, final Integer arg2) {
            return Integer.valueOf(arg1.intValue() - arg2.intValue());
        }
    }

    private static final class MultiplyInt extends BinOp<Integer> {
        MultiplyInt() {
            super(Integer.class);
        }

        @Override
        Integer getResult(final Integer arg1, final Integer arg2) {
            return Integer.valueOf(arg1.intValue() * arg2.intValue());
        }
    }

    private static final class DivideInt extends BinOp<Integer> {
        DivideInt() {
            super(Integer.class);
        }

        @Override
        Integer getResult(final Integer arg1, final Integer arg2) {
            return Integer.valueOf(arg1.intValue() / arg2.intValue());
        }
    }

    private static final Instruction ADD_INT = new AddInt();
    private static final Instruction SUBTRACT_INT = new SubtractInt();
    private static final Instruction MULTIPLY_INT = new MultiplyInt();
    private static final Instruction DIVIDE_INT = new DivideInt();

    public static Instruction getAddInt() {
        return ADD_INT;
    }

    public static Instruction getSubtractInt() {
        return SUBTRACT_INT;
    }

    public static Instruction getMultiplyInt() {
        return MULTIPLY_INT;
    }

    public static Instruction getDivideInt() {
        return DIVIDE_INT;
    }
}
