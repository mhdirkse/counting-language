package com.github.mhdirkse.countlang.engine;

abstract class IOContext {
    abstract void printInt(int value);

    private class PrintIntInstruction extends Instruction {
        @Override
        void run(Stack stack) {
            int value = (Integer) stack.pop();
            printInt(value); 
        }
    }

    Instruction getPrintIntInstruction() {
        return new PrintIntInstruction();
    }

    private static final class ConsoleIOContext extends IOContext {
        @Override
        void printInt(int value) {
            System.out.println(value);
        }
    }

    static IOContext getConsoleIOContext() {
        return new ConsoleIOContext();
    }

    private static final class ReceiverIOContext extends IOContext {
        private final OutputReceiver outputReceiver;

        ReceiverIOContext(final OutputReceiver outputReceiver) {
            this.outputReceiver = outputReceiver;
        }

        @Override
        void printInt(int value) {
            outputReceiver.onOutputLine(Integer.valueOf(value).toString());
        }
    }

    static IOContext getReceiverIOContext(final OutputReceiver outputReceiver) {
        return new ReceiverIOContext(outputReceiver);
    }
}
