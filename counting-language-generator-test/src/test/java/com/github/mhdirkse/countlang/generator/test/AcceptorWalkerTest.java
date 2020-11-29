/*
 * Copyright Martijn Dirkse 2020
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
