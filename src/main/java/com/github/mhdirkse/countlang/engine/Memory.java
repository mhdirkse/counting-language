package com.github.mhdirkse.countlang.engine;

import java.util.ArrayDeque;
import java.util.Deque;

final class Memory {
    public Memory() {
        frames = new ArrayDeque<Frame>();
    }

    private static final class Frame {
        private final Object[] data;

        Frame(int size) {
            data = new Object[size];
        }

        int getSize() {
            return data.length;
        }

        void put(final int position, final Object o) {
            data[position] = o;
        }

        Object get(final int position) {
            return data[position];
        }
    }
    
    private Deque<Frame> frames;

    public void pushFrame(final int size) {
        frames.addLast(new Frame(size));
    }

    private final class PushFrameInstruction extends Instruction {
        private final int size;

        PushFrameInstruction(final int size) {
            this.size = size;
        }

        @Override
        void run(final Stack stack) {
            pushFrame(size);
        }
    }

    public Instruction getPushFrameInstruction(final int size) {
        return new PushFrameInstruction(size);
    }

    public void popFrame() {
        frames.removeLast();
    }

    private final class PopFrameInstruction extends Instruction {
        @Override
        void run(final Stack stack) {
            popFrame();
        }
    }

    public Instruction getPopFrameInstruction() {
        return new PopFrameInstruction();
    }

    public Object getFromBottom(final int position) {
        return frames.getFirst().get(position);
    }

    public Object getFromTop(final int position) {
        return frames.getLast().get(position);
    }

    public void putInBottom(final int position, final Object o) {
        frames.getFirst().put(position, o);
    }

    public void putInTop(final int position, final Object o) {
        frames.getLast().put(position, o);
    }

    private final class ReadInstructionFromBottom extends Instruction {
        private final int position;

        ReadInstructionFromBottom(int position) {
            this.position = position;
        }

        @Override
        void run(Stack stack) {
            stack.push(getFromBottom(position));
        }
    }

    private final class ReadInstructionFromTop extends Instruction {
        private final int position;

        ReadInstructionFromTop(int position) {
            this.position = position;
        }

        @Override
        void run(Stack stack) {
            stack.push(getFromTop(position));
        }
    }

    public Instruction getReadInstructionFromBottom(final int position) {
        return new ReadInstructionFromBottom(position);
    }

    public Instruction getReadInstructionFromTop(final int position) {
        return new ReadInstructionFromTop(position);
    }

    private final class WriteInstructionToBottom extends Instruction {
        private final int position;

        WriteInstructionToBottom(int position) {
            this.position = position;
        }

        @Override
        void run(Stack stack) {
            putInBottom(position, stack.pop());
        }
    }

    private final class WriteInstructionToTop extends Instruction {
        private final int position;

        WriteInstructionToTop(int position) {
            this.position = position;
        }

        @Override
        void run(Stack stack) {
            putInTop(position, stack.pop());
        }
    }

    public Instruction getWriteInstructionToBottom(final int position) {
        return new WriteInstructionToBottom(position);
    }

    public Instruction getWriteInstructionToTop(final int position) {
        return new WriteInstructionToTop(position);
    }

    public int getNumFrames() {
        return frames.size();
    }

    public int getBottomFrameSize() {
        return frames.getFirst().getSize();
    }
}
