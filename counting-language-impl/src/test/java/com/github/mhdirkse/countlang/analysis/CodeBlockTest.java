package com.github.mhdirkse.countlang.analysis;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.countlang.ast.CountlangType;

public class CodeBlockTest implements Memory {
    private static enum BlockKind {
        SWITCH,
        BRANCH,
        REPETITION;
    }

    private static class ReceivedBlock {
        CodeBlock block;
        BlockKind kind;
        boolean isStopped = false;

        ReceivedBlock(CodeBlock block, BlockKind kind) {
            this.block = block;
            this.kind = kind;
        }
    }

    private CodeBlocks codeBlocks;
    private LinkedHashMap<CodeBlock, ReceivedBlock> receivedBlocks;

    @Override
    public void startSwitch(CodeBlock block) {
        receivedBlocks.put(block, new ReceivedBlock(block, BlockKind.SWITCH));
    }

    @Override
    public void stopSwitch(CodeBlock block) {
        ReceivedBlock stoppedBlock = stopBlock(block);
        assertEquals(BlockKind.SWITCH, stoppedBlock.kind);
    }

    private ReceivedBlock stopBlock(CodeBlock block) {
        ReceivedBlock expected = getLastNotStoppedBlock();
        assertSame(expected.block, block);
        expected.isStopped = true;
        return expected;
    }

    private ReceivedBlock getLastNotStoppedBlock() {
        Iterator<ReceivedBlock> it = (new ArrayDeque<ReceivedBlock>(receivedBlocks.values())).descendingIterator();
        while(true) {
            ReceivedBlock r = it.next();
            if(! r.isStopped) {
                return r;
            }
        }
    }

    @Override
    public void startBranch(CodeBlock block) {
        receivedBlocks.put(block, new ReceivedBlock(block, BlockKind.BRANCH));
    }

    @Override
    public void stopBranch(CodeBlock block) {
        ReceivedBlock stoppedBlock = stopBlock(block);
        assertEquals(BlockKind.BRANCH, stoppedBlock.kind);
    }

    @Override
    public void startRepetition(CodeBlock block) {
        receivedBlocks.put(block, new ReceivedBlock(block, BlockKind.REPETITION));
    }

    @Override
    public void stopRepetition(CodeBlock block) {
        ReceivedBlock stoppedBlock = stopBlock(block);
        assertEquals(BlockKind.REPETITION, stoppedBlock.kind);
    }

    @Override
    public Iterator<? extends BlockListener> getChildren() {
        throw new IllegalStateException("This class overrides the block listening method, no children needed");
    }

    @Override
    public List<VariableErrorEvent> getVariableErrorEvents() {
        fail("Should not be used by CodeBlocks, except in its decorator role");
        return null;
    }

    @Override
    public void pushScope(AnalysisScope analysisScope) {
        fail("Should not be used by CodeBlocks, except in its decorator role");
    }

    @Override
    public AnalysisScope popScope() {
        fail("Should not be used by CodeBlocks, except in its decorator role");
        return null;
    }

    @Override
    public CountlangType read(String name, int line, int column, CodeBlock codeBlock) {
        fail("Should not be used by CodeBlocks, except in its decorator role");
        return null;
    }

    @Override
    public void write(String name, int line, int column, CountlangType countlangType, CodeBlock codeBlock) {
        fail("Should not be used by CodeBlocks, except in its decorator role");
    }

    @Override
    public void addParameter(String name, int line, int column, CountlangType countlangType, CodeBlock codeBlock) {
        fail("Should not be used by CodeBlocks, except in its decorator role");
    }

    @Before
    public void setUp() {
        codeBlocks = new CodeBlocks(this);
        receivedBlocks = new LinkedHashMap<>();
    }

    @Test
    public void testWithoutChildren() {
        codeBlocks.start();
        CodeBlock defaultBlock = codeBlocks.getLastAddedBlock();
        codeBlocks.stop();
        assertTrue(receivedBlocks.isEmpty());
        assertTrue(defaultBlock.getChildren().isEmpty());
        assertTrue(defaultBlock.getDescendants().isEmpty());
    }

    @Test
    public void testSwitchWithBranches() {
        codeBlocks.start();
        CodeBlock defaultBlock = codeBlocks.getLastAddedBlock();
        codeBlocks.startSwitch();
        CodeBlock switchBlock = codeBlocks.getLastAddedBlock();
        codeBlocks.startBranch();
        CodeBlock branchBlock1 = codeBlocks.getLastAddedBlock();
        codeBlocks.stopBranch();
        codeBlocks.startBranch();
        CodeBlock branchBlock2 = codeBlocks.getLastAddedBlock();
        codeBlocks.stopBranch();
        codeBlocks.stopSwitch();
        codeBlocks.stop();
        assertEquals(3, receivedBlocks.size());
        assertTrue(receivedBlocks.containsKey(switchBlock));
        assertTrue(receivedBlocks.containsKey(branchBlock1));
        assertTrue(receivedBlocks.containsKey(branchBlock2));
        assertArrayEquals(new CodeBlock[] {switchBlock}, defaultBlock.getChildren().toArray());
        assertArrayEquals(new CodeBlock[] {branchBlock1, branchBlock2}, switchBlock.getChildren().toArray());
        assertTrue(branchBlock1.getChildren().isEmpty());
        assertTrue(branchBlock2.getChildren().isEmpty());
        assertEquals(3, defaultBlock.getDescendants().size());
        assertTrue(defaultBlock.contains(switchBlock));
        assertTrue(defaultBlock.contains(branchBlock1));
        assertTrue(defaultBlock.contains(branchBlock2));
        assertTrue(receivedBlocks.get(switchBlock).isStopped);
        assertEquals(BlockKind.SWITCH, receivedBlocks.get(switchBlock).kind);
        assertTrue(receivedBlocks.get(branchBlock1).isStopped);
        assertEquals(BlockKind.BRANCH, receivedBlocks.get(branchBlock1).kind);
        assertTrue(receivedBlocks.get(branchBlock2).isStopped);
        assertEquals(BlockKind.BRANCH, receivedBlocks.get(branchBlock2).kind);
    }

    @Test
    public void testRepetitionWithSwitch() {
        codeBlocks.start();
        CodeBlock defaultBlock = codeBlocks.getLastAddedBlock();
        codeBlocks.startRepetition();
        CodeBlock repetitionBlock = codeBlocks.getLastAddedBlock();
        codeBlocks.startSwitch();
        CodeBlock switchBlock = codeBlocks.getLastAddedBlock();
        codeBlocks.stopSwitch();
        codeBlocks.stopRepetition();
        codeBlocks.stop();
        assertEquals(2, receivedBlocks.size());
        assertTrue(receivedBlocks.containsKey(repetitionBlock));
        assertTrue(receivedBlocks.containsKey(switchBlock));
        assertArrayEquals(new CodeBlock[] {repetitionBlock}, defaultBlock.getChildren().toArray());
        assertArrayEquals(new CodeBlock[] {switchBlock}, repetitionBlock.getChildren().toArray());
        assertTrue(switchBlock.getChildren().isEmpty());
        assertEquals(2, defaultBlock.getDescendants().size());
        assertTrue(defaultBlock.contains(repetitionBlock));
        assertTrue(defaultBlock.contains(switchBlock));
        assertTrue(receivedBlocks.get(repetitionBlock).isStopped);
        assertEquals(BlockKind.REPETITION, receivedBlocks.get(repetitionBlock).kind);
        assertTrue(receivedBlocks.get(switchBlock).isStopped);
        assertEquals(BlockKind.SWITCH, receivedBlocks.get(switchBlock).kind);
    }

    @Override
    public boolean isAtRootScope() {
        throw new IllegalStateException("Not expected to be called");
    }
}
