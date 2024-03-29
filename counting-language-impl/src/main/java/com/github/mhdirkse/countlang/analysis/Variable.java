/*
 * Copyright Martijn Dirkse 2021
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.mhdirkse.countlang.analysis;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.github.mhdirkse.countlang.type.CountlangType;
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
        private final Set<VariableWrite> startReadableWrites;
        private final CodeBlock repetitionCodeBlock;
        private Set<CodeBlock> circularReaders = new HashSet<>();

        RepetitionContext(CodeBlock repetitionCodeBlock) {
            startReadableWrites = new HashSet<>(readableWrites);
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
        this.countlangType = countlangType;
        VariableWrite firstWrite = new VariableWrite(this, line, column, countlangType, kind, codeBlock, true);
        updateOtherWritesAndRegister(firstWrite, codeBlock);
    }

    CountlangType read(CodeBlock codeBlock) {
        contexts.forEach(c -> c.onRead(codeBlock));
        readableWrites.forEach(w -> w.read(codeBlock));
        return countlangType;
    }

    VariableErrorEvent write(int line, int column, CountlangType countlangType, VariableWriteKind kind, CodeBlock codeBlock) {
        VariableWrite write = new VariableWrite(this, line, column, countlangType, kind, codeBlock, false);
        updateOtherWritesAndRegister(write, codeBlock);
        if(this.countlangType != countlangType) {
            return new VariableErrorEvent(name, line, column, this.countlangType, countlangType);
        } else {
            return null;
        }
    }

    private void updateOtherWritesAndRegister(VariableWrite write, CodeBlock codeBlock) {
        codeBlock.addVariableWrite(write);
        readableWrites.forEach(rw -> rw.overwrite(write));
        readableWrites = new HashSet<>();
        readableWrites.add(write);
    }

    @Override
    public void startSwitch(CodeBlock codeBlock) {
        contexts.push(new SwitchContext());
    }

    @Override
    public void startBranch(CodeBlock codeBlock) {
        ((SwitchContext) contexts.peek()).startBranch();
    }

    @Override
    public void stopBranch(CodeBlock codeBlock) {
        ((SwitchContext) contexts.peek()).stopBranch();
    }

    @Override
    public void stopSwitch(CodeBlock codeBlock) {
        SwitchContext switchContext = (SwitchContext) contexts.pop();
        readableWrites = new HashSet<>(switchContext.endReadableWrites);
    }

    @Override
    public void startRepetition(CodeBlock codeBlock) {
        contexts.push(new RepetitionContext(codeBlock));
    }

    @Override
    public void stopRepetition(CodeBlock codeBlock) {
        RepetitionContext repetitionContext = (RepetitionContext) contexts.pop();
        for(CodeBlock circularReader: repetitionContext.circularReaders) {
            readableWrites.forEach(w -> w.read(circularReader));    
        }
        readableWrites.addAll(repetitionContext.startReadableWrites);
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
