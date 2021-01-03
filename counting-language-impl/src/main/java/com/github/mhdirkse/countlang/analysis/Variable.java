package com.github.mhdirkse.countlang.analysis;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.utils.Stack;

import lombok.Getter;

class Variable implements BlockListener {
    private abstract class Context {
        void onRead(CodeBlock readingCodeBlock) {
        }
    }

    private class SwitchContext extends Context {
        private final Set<VariableWrite> startReadableWrites;
        private final Set<VariableWrite> endReadableWrites;

        SwitchContext() {
            startReadableWrites = new HashSet<>(readableWrites);
            endReadableWrites = new HashSet<>();
        }

        void startBranch() {
            readableWrites = new HashSet<>(startReadableWrites);
        }

        void stopBranch() {
            endReadableWrites.addAll(readableWrites);
        }
    }

    private class RepetitionContext extends SwitchContext {
        private final CodeBlock repetitionCodeBlock;
        private Set<CodeBlock> circularReaders = new HashSet<>();

        RepetitionContext(CodeBlock repetitionCodeBlock) {
            this.repetitionCodeBlock = repetitionCodeBlock;
        }

        @Override
        void onRead(CodeBlock readingCodeBlock) {
            boolean readsWriteOutsideRepetition = readableWrites.stream()
                    .map(VariableWrite::getCodeBlock)
                    .anyMatch(blockOfWrite -> ! repetitionCodeBlock.contains(blockOfWrite));
            if(readsWriteOutsideRepetition) {
                circularReaders.add(readingCodeBlock);
            }
        }
    }

    private @Getter CountlangType countlangType;
    private @Getter final String name;
    private Set<VariableWrite> readableWrites = new HashSet<>();
    private Stack<Context> contexts = new Stack<>();

    Variable(String name, int line, int column, CountlangType countlangType, VariableWriteKind kind, CodeBlock codeBlock) {
        this.name = name;
        VariableWrite firstWrite = new VariableWrite(this, line, column, countlangType, kind, codeBlock, true);
        updateOtherWritesAndRegister(firstWrite, codeBlock);
    }

    CountlangType read(CodeBlock codeBlock) {
        contexts.forEach(c -> c.onRead(codeBlock));
        readableWrites.forEach(w -> w.read(codeBlock));
        return countlangType;
    }

    void write(int line, int column, CountlangType countlangType, CodeBlock codeBlock) {
        VariableWrite write = new VariableWrite(this, line, column, countlangType, VariableWriteKind.ASSIGNMENT, codeBlock, false);
        updateOtherWritesAndRegister(write, codeBlock);
    }

    private void updateOtherWritesAndRegister(VariableWrite write, CodeBlock codeBlock) {
        codeBlock.addVariableWrite(write);
        readableWrites.forEach(rw -> rw.overwrite(write));
        readableWrites = new HashSet<>();
    }

    @Override
    public void startSwitch() {
        contexts.push(new SwitchContext());
    }

    @Override
    public void startBranch() {
        ((SwitchContext) contexts.peek()).startBranch();
    }

    @Override
    public void stopBranch() {
        ((SwitchContext) contexts.peek()).stopBranch();
    }

    @Override
    public void stopSwitch() {
        SwitchContext switchContext = (SwitchContext) contexts.pop();
        readableWrites = new HashSet<>(switchContext.endReadableWrites);
    }

    @Override
    public void startRepetition(CodeBlock codeBlock) {
        contexts.push(new RepetitionContext(codeBlock));
    }

    @Override
    public void stopRepetition() {
        RepetitionContext repetitionContext = (RepetitionContext) contexts.pop();
        for(CodeBlock circularReader: repetitionContext.circularReaders) {
            readableWrites.forEach(w -> w.read(circularReader));    
        }
    }

    /**
     * Not applicable here, because the {@link BlockListener} default implementations
     * are overridden here
     * @throws IllegalStateException
     */
    @Override
    public Iterator<BlockListener> getChildren() {
        throw new RuntimeException("Programming error - operation makes no sense here");
    }
}
