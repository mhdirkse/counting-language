package com.github.mhdirkse.countlang.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * The engine of the counting-language application
 * does the actual calculations. It acts as a
 * virtual machine. This class is the entry point.
 *
 * @author martijndirkse
 *
 */
public final class Engine {
    private final Memory memory;
    private final List<Instruction> instructions;
    private final IOContext ioContext;

    public Engine(final int bottomFrameSize) {
        memory = new Memory();
        memory.pushFrame(bottomFrameSize);
        instructions = new ArrayList<Instruction>();
        ioContext = IOContext.getConsoleIOContext();
    }

    public Engine(final int bottomFrameSize, final OutputReceiver outputReceiver) {
        memory = new Memory();
        memory.pushFrame(bottomFrameSize);
        instructions = new ArrayList<Instruction>();
        ioContext = IOContext.getReceiverIOContext(outputReceiver);
    }

    public void addInstruction(final Instruction i) {
        instructions.add(i);
    }

    public void run() {
        printAllStatements();
        Stack stack = new Stack();
        for(Instruction i : instructions) {
            i.run(stack);
        }
    }

    private void printAllStatements() {
        for(Instruction i : instructions) {
            System.out.println(i.toString());
        }
    }

    public void write(int position, final Object o) {
        memory.putInBottom(position, o);
    }

    public Instruction getReadInstructionFromBottom(final int position) {
        return memory.getReadInstructionFromBottom(position);
    }

    public Instruction getReadInstructionFromTop(final int position) {
        return memory.getReadInstructionFromTop(position);
    }

    public Instruction getWriteInstructionToBottom(final int position) {
        return memory.getWriteInstructionToBottom(position);
    }

    public Instruction getWriteInstructionToTop(final int position) {
        return memory.getWriteInstructionToTop(position);
    }

    public Instruction getPrintIntInstruction() {
        return ioContext.getPrintIntInstruction();
    }
}
