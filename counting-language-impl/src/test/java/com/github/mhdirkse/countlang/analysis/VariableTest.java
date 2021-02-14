package com.github.mhdirkse.countlang.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.countlang.algorithm.StackFrameAccess;
import com.github.mhdirkse.countlang.ast.CountlangType;

public class VariableTest {
    private CodeBlocks codeBlocks;
    private CodeBlock main;

    @Before
    public void setUp() {
        codeBlocks = new CodeBlocks(new MemoryImpl());
        codeBlocks.start();
        main = codeBlocks.getLastAddedBlock();
        codeBlocks.pushScope(new Scope(StackFrameAccess.SHOW_PARENT));
    }

    private void stop() {
        codeBlocks.popScope();
        codeBlocks.stop();
    }

    @Test
    public void whenWriteVariableWithoutReadThenHaveUnreadWrite() {
        codeBlocks.write("x", 1, 2, CountlangType.BOOL);
        stop();
        List<VariableWrite> writes = main.getVariableWrites();
        assertEquals(1, writes.size());
        VariableWrite write = writes.get(0);
        assertSame(main, write.getCodeBlock());
        assertFalse(write.isRead());
        assertFalse(write.isOverwritten());
    }

    @Test
    public void whenWriteVariableAndReadThenHaveWriteThatIsRead() {
        codeBlocks.write("x", 1, 2, CountlangType.INT);
        codeBlocks.read("x", 2, 2);
        stop();
        List<VariableWrite> writes = main.getVariableWrites();
        assertEquals(1, writes.size());
        VariableWrite write = writes.get(0);
        assertSame(main, write.getCodeBlock());
        assertTrue(write.isRead());
        assertFalse(write.isOverwritten());
    }

    @Test
    public void whenWriteReadWriteThenHaveWriteThatIsReadAndWriteThatIsNotRead() {
        codeBlocks.write("x", 1, 2, CountlangType.INT);
        codeBlocks.read("x", 2, 2);
        codeBlocks.write("x", 3, 2, CountlangType.INT);
        stop();
        List<VariableWrite> writes = main.getVariableWrites();
        assertEquals(2, writes.size());
        VariableWrite first = writes.get(0);
        VariableWrite second = writes.get(1);
        assertTrue(first.isRead());
        assertTrue(first.isOverwritten());
        assertFalse(second.isRead());
        assertFalse(second.isOverwritten());
        // Test that there is no type mismatch
        assertTrue(codeBlocks.getVariableErrorEvents().isEmpty());
    }

    @Test
    public void whenTypeMismatchThenTypeMismatchSaved() {
        codeBlocks.write("x", 1, 2, CountlangType.INT);
        codeBlocks.read("x", 2, 2);
        codeBlocks.write("x", 3, 4, CountlangType.BOOL);
        assertEquals(1, codeBlocks.getVariableErrorEvents().size());
        VariableErrorEvent mismatch = codeBlocks.getVariableErrorEvents().get(0);
        assertEquals(VariableErrorEvent.Kind.TYPE_MISMATCH, mismatch.getKind());
        assertEquals("x", mismatch.getName());
        assertEquals(3, mismatch.getLine());
        assertEquals(4, mismatch.getColumn());
        assertEquals(CountlangType.INT, mismatch.getVariableType());
        assertEquals(CountlangType.BOOL, mismatch.getTypeMismatch());
    }

    @Test
    public void whenVariableReadAfterIfThenOriginalWriteAndConditionalWriteAreRead() {
        codeBlocks.write("x", 1, 2, CountlangType.INT);
        codeBlocks.startSwitch();
        codeBlocks.startBranch();
        codeBlocks.write("x", 3, 2, CountlangType.INT);
        codeBlocks.stopBranch();
        codeBlocks.startBranch();
        codeBlocks.stopBranch();
        codeBlocks.stopSwitch();
        codeBlocks.read("x", 5, 2);
        stop();
        List<VariableWrite> writes = main.getVariableWrites();
        assertEquals(1, writes.size());
        VariableWrite initialWrite = writes.get(0);
        assertEquals(1, main.getChildren().size());
        CodeBlock switchBlock = main.getChildren().get(0);
        List<VariableWrite> conditionalWrites = switchBlock.getChildren().get(0).getVariableWrites();
        assertEquals(1, conditionalWrites.size());
        VariableWrite overwritingWrite = conditionalWrites.get(0);
        assertTrue(switchBlock.getChildren().get(1).getVariableWrites().isEmpty());
        assertTrue(initialWrite.isInitial());
        assertTrue(initialWrite.isOverwritten());
        assertTrue(initialWrite.isRead());
        assertFalse(overwritingWrite.isInitial());
        assertFalse(overwritingWrite.isOverwritten());
        assertTrue(overwritingWrite.isRead());
    }

    @Test
    public void whenVariableOverwrittenByAllBranchesThenInitialNotRead() {
        codeBlocks.write("x", 1, 2, CountlangType.INT);
        codeBlocks.startSwitch();
        codeBlocks.startBranch();
        codeBlocks.write("x", 3, 2, CountlangType.INT);
        codeBlocks.stopBranch();
        codeBlocks.startBranch();
        codeBlocks.write("x", 5, 2, CountlangType.INT);
        codeBlocks.stopBranch();
        codeBlocks.stopSwitch();
        codeBlocks.read("x", 7, 2);
        stop();
        List<VariableWrite> writes = main.getVariableWrites();
        assertEquals(1, writes.size());
        VariableWrite initialWrite = writes.get(0);
        assertEquals(1, main.getChildren().size());
        CodeBlock switchBlock = main.getChildren().get(0);
        assertEquals(2, switchBlock.getChildren().size());
        assertEquals(1, switchBlock.getChildren().get(0).getVariableWrites().size());
        assertEquals(1, switchBlock.getChildren().get(1).getVariableWrites().size());
        List<VariableWrite> conditionalWrites = switchBlock.getChildren().stream()
                .map(branchBlock -> branchBlock.getVariableWrites().get(0))
                .collect(Collectors.toList());
        assertTrue(initialWrite.isInitial());
        assertTrue(initialWrite.isOverwritten());
        assertFalse(initialWrite.isRead());
        for(VariableWrite cw: conditionalWrites) {
            assertFalse(cw.isInitial());
            assertFalse(cw.isOverwritten());
            assertTrue(cw.isRead());
        }
    }

    @Test
    public void whenRepetitionReadsFromBeforeThenFinishingWriteInRepetitionIsRead() {
        codeBlocks.write("x", 1, 2, CountlangType.INT);
        codeBlocks.startRepetition();
        codeBlocks.read("x", 3, 2);
        codeBlocks.write("x", 4, 2, CountlangType.INT);
        codeBlocks.stopRepetition();
        stop();
        assertEquals(1, main.getVariableWrites().size());
        VariableWrite initial = main.getVariableWrites().get(0);
        assertEquals(1, main.getChildren().size());
        CodeBlock repetitionBlock = main.getChildren().get(0);
        assertEquals(1, repetitionBlock.getVariableWrites().size());
        VariableWrite finisher = repetitionBlock.getVariableWrites().get(0);
        assertTrue(initial.isInitial());
        assertTrue(initial.isOverwritten());
        assertTrue(initial.isRead());
        assertFalse(finisher.isInitial());
        // We do not count the overwrite by itself
        assertFalse(finisher.isOverwritten());
        assertTrue(finisher.isRead());
    }

    @Test
    public void whenRepetitionDoesNotReadFromBeforeThenFinishingWriteInRepetitionIsNotRead() {
        codeBlocks.write("x", 1, 2, CountlangType.INT);
        codeBlocks.startRepetition();
        codeBlocks.write("x", 3, 2, CountlangType.INT);
        codeBlocks.read("x", 4, 2);
        codeBlocks.write("x", 5, 2, CountlangType.INT);
        codeBlocks.stopRepetition();
        stop();
        assertEquals(1, main.getVariableWrites().size());
        VariableWrite initial = main.getVariableWrites().get(0);
        assertEquals(1, main.getChildren().size());
        CodeBlock repetitionBlock = main.getChildren().get(0);
        assertEquals(2, repetitionBlock.getVariableWrites().size());
        VariableWrite overwriter = repetitionBlock.getVariableWrites().get(0);
        VariableWrite finisher = repetitionBlock.getVariableWrites().get(1);
        assertTrue(initial.isInitial());
        assertFalse(initial.isRead());
        assertTrue(initial.isOverwritten());
        assertFalse(overwriter.isInitial());
        assertTrue(overwriter.isRead());
        assertTrue(overwriter.isOverwritten());
        assertFalse(finisher.isInitial());
        // The earlier read does not influence the last write in the repetition,
        // because that read reads the first write inside the repetition.
        assertFalse(finisher.isRead());
        assertFalse(finisher.isOverwritten());
    }

    @Test
    public void writeBeforeRepetitionIsReadAfterRepetitionBecauseRepetitionMayNotBeExecuted() {
        codeBlocks.write("x", 1, 2, CountlangType.INT);
        codeBlocks.startRepetition();
        codeBlocks.write("x", 3, 2, CountlangType.INT);
        codeBlocks.stopRepetition();
        codeBlocks.read("x", 5, 2);
        stop();
        assertEquals(1, main.getVariableWrites().size());
        VariableWrite initial = main.getVariableWrites().get(0);
        assertEquals(1, main.getChildren().size());
        CodeBlock repetitionBlock = main.getChildren().get(0);
        assertEquals(1, repetitionBlock.getVariableWrites().size());
        VariableWrite repeated = repetitionBlock.getVariableWrites().get(0);
        assertTrue(initial.isInitial());
        assertTrue(initial.isRead());
        assertTrue(initial.isOverwritten());
        assertFalse(repeated.isInitial());
        assertTrue(repeated.isRead());
        assertFalse(repeated.isOverwritten());
    }

    @Test
    public void whenFirstReadInRepetitionIsInSubThenFinishingWriteIsRead() {
        codeBlocks.write("x", 1, 1, CountlangType.INT);
        codeBlocks.startRepetition();
        codeBlocks.startSwitch();
        codeBlocks.startBranch();
        codeBlocks.read("x", 5, 2);
        codeBlocks.stopBranch();
        codeBlocks.startBranch();
        codeBlocks.stopBranch();
        codeBlocks.stopSwitch();
        codeBlocks.write("x", 10, 2, CountlangType.INT);
        codeBlocks.stopRepetition();
        stop();
        assertEquals(1, main.getVariableWrites().size());
        VariableWrite initial = main.getVariableWrites().get(0);
        assertEquals(1, main.getChildren().size());
        CodeBlock repetitionBlock = main.getChildren().get(0);
        assertEquals(1, repetitionBlock.getVariableWrites().size());
        VariableWrite finisher = repetitionBlock.getVariableWrites().get(0);
        assertTrue(initial.isInitial());
        assertTrue(initial.isRead());
        assertTrue(initial.isOverwritten());
        assertFalse(finisher.isInitial());
        assertTrue(finisher.isRead());
        assertFalse(finisher.isOverwritten());
    }

    @Test
    public void whenReadInNestedRepetitionThenFinalWriteInOuterRepetitionIsRead() {
        codeBlocks.write("x", 1, 2, CountlangType.BOOL);
        codeBlocks.startRepetition();
        codeBlocks.startRepetition();
        codeBlocks.read("x", 5, 2);
        // Is read circularly by the inner repetition
        codeBlocks.write("x", 6, 2, CountlangType.BOOL);
        codeBlocks.stopRepetition();
        // We test that this write is circularly read
        codeBlocks.write("x", 8, 2, CountlangType.BOOL);
        codeBlocks.stopRepetition();
        stop();
        assertEquals(1, main.getVariableWrites().size());
        VariableWrite initial = main.getVariableWrites().get(0);
        assertEquals(1, main.getChildren().size());
        CodeBlock outer = main.getChildren().get(0);
        assertEquals(1, outer.getChildren().size());
        CodeBlock inner = outer.getChildren().get(0);
        assertEquals(1, inner.getVariableWrites().size());
        VariableWrite innerFinisher = inner.getVariableWrites().get(0);
        assertEquals(6, innerFinisher.getLine());
        assertEquals(1, outer.getVariableWrites().size());
        VariableWrite outerFinisher = outer.getVariableWrites().get(0);
        assertEquals(8, outerFinisher.getLine());
        assertTrue(initial.isInitial());
        assertTrue(initial.isRead());
        assertTrue(initial.isOverwritten());
        assertFalse(innerFinisher.isInitial());
        assertTrue(innerFinisher.isRead());
        assertTrue(innerFinisher.isOverwritten());
        assertFalse(outerFinisher.isInitial());
        assertTrue(outerFinisher.isRead());
        assertFalse(outerFinisher.isOverwritten());
    }
}
