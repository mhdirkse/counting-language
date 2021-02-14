package com.github.mhdirkse.countlang.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.countlang.algorithm.StackFrameAccess;
import com.github.mhdirkse.countlang.ast.CountlangType;

public class MemoryTest {
    private Memory memory;
    private CodeBlock block;

    @Before
    public void setUp() {
        block = new CodeBlockRoot();
        memory = new MemoryImpl();
    }

    @Test
    public void whenVariableWrittenThenWriteAvailableInCodeBlock() {
        memory.pushScope(new Scope(StackFrameAccess.SHOW_PARENT));
        memory.write("x", 1, 2, CountlangType.INT, block);
        memory.popScope();
        assertEquals(1, block.getVariableWrites().size());
        VariableWrite write = block.getVariableWrites().get(0);
        assertEquals("x", write.getVariable().getName());
        assertEquals(CountlangType.INT, write.getVariable().getCountlangType());
        assertEquals(1, write.getLine());
        assertEquals(2, write.getColumn());
        assertEquals(CountlangType.INT, write.getCountlangType());
        assertEquals(VariableWriteKind.ASSIGNMENT, write.getVariableWriteKind());
        assertEquals(block, write.getCodeBlock());
        assertTrue(write.isInitial());
        assertFalse(write.isRead());
        assertFalse(write.isOverwritten());
    }

    @Test
    public void whenSameNameWrittenInTwoScopesThenDifferentVariablesWritten() {
        memory.pushScope(new Scope(StackFrameAccess.SHOW_PARENT));
        memory.write("x", 1, 2, CountlangType.INT, block);
        memory.pushScope(new Scope(StackFrameAccess.HIDE_PARENT));
        memory.write("x", 2, 2, CountlangType.INT, block);
        memory.popScope();
        memory.popScope();
        assertEquals(2, block.getVariableWrites().size());
        assertEquals(2, getNumVariablesOfBlock());
        assertTrue(block.getVariableWrites().stream()
                .allMatch(VariableWrite::isInitial));
    }

    private int getNumVariablesOfBlock() {
        return block.getVariableWrites().stream()
                .map(VariableWrite::getVariable)
                .distinct()
                .collect(Collectors.toList()).size();
    }

    @Test
    public void whenSameNameWrittenInSameScopeThenSameVariableWritten() {
        memory.pushScope(new Scope(StackFrameAccess.SHOW_PARENT));
        memory.write("x", 1, 2, CountlangType.INT, block);
        memory.write("x", 2, 2, CountlangType.INT, block);
        memory.popScope();
        checkSameVariableWritten();
    }

    private void checkSameVariableWritten() {
        assertEquals(2, block.getVariableWrites().size());
        assertTrue(block.getVariableWrites().get(0).isInitial());
        assertFalse(block.getVariableWrites().get(0).isRead());
        assertTrue(block.getVariableWrites().get(0).isOverwritten());
        assertFalse(block.getVariableWrites().get(1).isInitial());
        assertFalse(block.getVariableWrites().get(1).isRead());
        assertFalse(block.getVariableWrites().get(1).isOverwritten());
    }

    @Test
    public void whenNewScopeShowingParentThenNameResolvesToParentScope() {
        memory.pushScope(new Scope(StackFrameAccess.SHOW_PARENT));
        memory.write("x", 1, 2, CountlangType.INT, block);
        memory.pushScope(new Scope(StackFrameAccess.SHOW_PARENT));
        memory.write("x", 2, 2, CountlangType.INT, block);
        memory.popScope();
        memory.popScope();
        checkSameVariableWritten();        
    }

    @Test
    public void whenParameterWrittenThenWriteKindParameter() {
        Scope scope = new Scope(StackFrameAccess.SHOW_PARENT);
        memory.pushScope(scope);
        memory.addParameter("x", 1, 2, CountlangType.BOOL, block);
        assertTrue(scope.hasVariable("x"));
        memory.popScope();
        assertEquals(1, block.getVariableWrites().size());
        VariableWrite write = block.getVariableWrites().get(0);
        assertEquals("x", write.getVariable().getName());
        assertEquals(CountlangType.BOOL, write.getVariable().getCountlangType());
        assertEquals(1, write.getLine());
        assertEquals(2, write.getColumn());
        assertEquals(CountlangType.BOOL, write.getCountlangType());
        assertEquals(VariableWriteKind.PARAMETER, write.getVariableWriteKind());
        assertEquals(block, write.getCodeBlock());
        assertTrue(write.isInitial());        
    }

    @Test
    public void whenParameterClashesWithExistingVariableThenErrorEventSaved() {
        memory.pushScope(new Scope(StackFrameAccess.SHOW_PARENT));
        memory.write("x", 1, 2, CountlangType.INT, block);
        memory.addParameter("x", 2, 3, CountlangType.INT, block);
        memory.popScope();
        assertEquals(1, memory.getVariableErrorEvents().size());
        VariableErrorEvent ev = memory.getVariableErrorEvents().get(0);
        assertEquals(VariableErrorEvent.Kind.DUPLICATE_PARAMETER, ev.getKind());
        assertEquals("x", ev.getName());
        assertEquals(2, ev.getLine());
        assertEquals(3, ev.getColumn());
    }

    @Test
    public void whenVariableReadThenInitialWriteIsRead() {
        memory.pushScope(new Scope(StackFrameAccess.SHOW_PARENT));
        memory.write("x", 1, 2, CountlangType.BOOL, block);
        assertEquals(CountlangType.BOOL, memory.read("x", 2, 3, block));
        memory.popScope();
        assertEquals(1, block.getVariableWrites().size());
        VariableWrite write = block.getVariableWrites().get(0);
        assertTrue(write.isRead());
        assertFalse(write.isOverwritten());
    }

    @Test
    public void whenNonExistentSymbolReadThenErrorEventSaved() {
        memory.pushScope(new Scope(StackFrameAccess.SHOW_PARENT));
        assertEquals(CountlangType.UNKNOWN, memory.read("x", 1, 2, block));
        memory.popScope();
        assertEquals(1, memory.getVariableErrorEvents().size());
        VariableErrorEvent ev = memory.getVariableErrorEvents().get(0);
        assertEquals("x", ev.getName());
        assertEquals(VariableErrorEvent.Kind.DOES_NOT_EXIST, ev.getKind());
        assertEquals(1, ev.getLine());
        assertEquals(2, ev.getColumn());
    }
}
