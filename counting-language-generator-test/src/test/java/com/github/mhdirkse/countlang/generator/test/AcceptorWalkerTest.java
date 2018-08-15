package com.github.mhdirkse.countlang.generator.test;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.countlang.generator.test.input.Acceptor;
import com.github.mhdirkse.countlang.generator.test.input.AtomicAcceptor;
import com.github.mhdirkse.countlang.generator.test.input.CompositeAcceptor;

public class AcceptorWalkerTest {
    private VisitorListenerImpl listener;
    private Acceptor atomic;
    private CompositeAcceptor composite;

    @Before
    public void setUp() {
        listener = new VisitorListenerImpl();
        createAcceptors();
    }

    private void createAcceptors() {
        atomic = new AtomicAcceptor();
        atomic.setName("atomic");
        composite = new CompositeAcceptor();
        composite.setName("composite");
        composite.setChildren(Arrays.asList(atomic));
    }

    @Test
    public void whenOneAtomicWalkedThenListenerSeesOneAtomic() {
        AcceptorWalker instance = new AcceptorWalker(atomic);
        instance.walk(listener);
        Assert.assertEquals(1, listener.getVisitedNames().size());
        Assert.assertEquals("atomic", listener.getVisitedNames().get(0));
    }

    @Test
    public void whenCompositeWithOneAtomicthenListenerWalksCompositeAtomicComposite() {
        AcceptorWalker instance = new AcceptorWalker(composite);
        instance.walk(listener);
        Assert.assertEquals(3, listener.getVisitedNames().size());
        Assert.assertEquals("composite", listener.getVisitedNames().get(0));
        Assert.assertEquals("atomic", listener.getVisitedNames().get(1));
        Assert.assertEquals("composite", listener.getVisitedNames().get(2));
    }
}
