package com.github.mhdirkse.countlang.lang.parsing;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.mhdirkse.countlang.lang.CountlangParser;

@RunWith(EasyMockRunner.class)
public class IgnoredMethodsHandlerTest {
    private IgnoredMethodsHandler instance;

    @Mock(type = MockType.STRICT)
    private DelegationContext delegationContext;

    @Mock(type = MockType.STRICT)
    private Token token;

    @Mock(type = MockType.STRICT)
    private TerminalNode terminalNode;

    @Before
    public void setUp() {
        instance = new IgnoredMethodsHandler();
    }

    @Test
    public void whenRelevantTerminalThenDontHandle() {
        expect(terminalNode.getSymbol()).andReturn(token).anyTimes();
        expect(token.getType()).andReturn(CountlangParser.ID).anyTimes();
        replay(delegationContext);
        replay(terminalNode);
        replay(token);
        Assert.assertFalse(instance.visitTerminal(terminalNode, delegationContext));
        verify(delegationContext);
        verify(terminalNode);
        verify(token);
    }
}
