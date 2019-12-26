package com.github.mhdirkse.countlang.lang.parsing;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.mhdirkse.countlang.lang.CountlangParser;

@RunWith(EasyMockRunner.class)
public class FunctionDefinitionStatementHandlerTest {
    private FunctionDefinitionStatementHandler instance;

    @Mock(type = MockType.STRICT)
    private DelegationContext delegationContext;

    @Mock(type = MockType.STRICT)
    private CountlangParser.VarDeclsContext antlrContext;

    @Before
    public void setUp() {
        instance = new FunctionDefinitionStatementHandler(1, 1);
    }

    @Test
    public void whenExitVarDeclsAndFirstHandlerThenNotHandled() {
        expect(delegationContext.isFirst()).andReturn(true).anyTimes();
        replay(antlrContext);
        replay(delegationContext);
        Assert.assertFalse(instance.exitVarDecls(antlrContext, delegationContext));
        verify(antlrContext);
        verify(delegationContext);
    }
}
