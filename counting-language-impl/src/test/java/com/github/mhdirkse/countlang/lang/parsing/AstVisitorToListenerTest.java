package com.github.mhdirkse.countlang.lang.parsing;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.runner.RunWith;

import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.AstListener;
import com.github.mhdirkse.countlang.ast.AstVisitorToListener;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FormalParameters;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.Operator;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.ReturnStatement;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.testhelper.AstConstructionTestBase;

import org.junit.Assert;
import org.junit.Test;

import static org.easymock.EasyMock.*;

@RunWith(EasyMockRunner.class)
public class AstVisitorToListenerTest extends AstConstructionTestBase {
    @Mock(type = MockType.STRICT)
    AstListener listener;

    private void runProgram(final String programText) {
        parse(programText);
        Assert.assertFalse(hasParseErrors);
        AstVisitorToListener instance = new AstVisitorToListener(listener);
        ast.accept(instance);
    }

    @Test
    public void testPrintOperatorExpression() {
        listener.enterStatementGroup(isA(StatementGroup.class));
        listener.enterPrintStatement(isA(PrintStatement.class));
        listener.enterCompositeExpression(isA(CompositeExpression.class));
        listener.visitOperator(isA(Operator.class));
        listener.visitValueExpression(isA(ValueExpression.class));
        listener.visitValueExpression(isA(ValueExpression.class));
        listener.exitCompositeExpression(isA(CompositeExpression.class));
        listener.exitPrintStatement(isA(PrintStatement.class));
        listener.exitStatementGroup(isA(StatementGroup.class));
        replay(listener);
        runProgram("print 5 + 3");
        verify(listener);
    }

    @Test
    public void testFunctionDefAndFunctionCallAndAssignment() {
        listener.enterStatementGroup(isA(StatementGroup.class));
        listener.enterFunctionDefinitionStatement(isA(FunctionDefinitionStatement.class));
        listener.enterFormalParameters(isA(FormalParameters.class));
        listener.visitFormalParameter(isA(FormalParameter.class));
        listener.exitFormalParameters(isA(FormalParameters.class));
        listener.enterStatementGroup(isA(StatementGroup.class));
        listener.enterReturnStatement(isA(ReturnStatement.class));
        listener.enterCompositeExpression(isA(CompositeExpression.class));
        listener.visitOperator(isA(Operator.class));
        listener.visitSymbolExpression(isA(SymbolExpression.class));
        listener.visitValueExpression(isA(ValueExpression.class));
        listener.exitCompositeExpression(isA(CompositeExpression.class));
        listener.exitReturnStatement(isA(ReturnStatement.class));
        listener.exitStatementGroup(isA(StatementGroup.class));
        listener.exitFunctionDefinitionStatement(isA(FunctionDefinitionStatement.class));
        listener.enterAssignmentStatement(isA(AssignmentStatement.class));
        listener.enterFunctionCallExpression(isA(FunctionCallExpression.class));
        listener.visitValueExpression(isA(ValueExpression.class));
        listener.exitFunctionCallExpression(isA(FunctionCallExpression.class));
        listener.exitAssignmentStatement(isA(AssignmentStatement.class));
        listener.exitStatementGroup(isA(StatementGroup.class));
        replay(listener);
        runProgram("function fun(int x) {return x + 2}; y = fun(3)");
        verify(listener);
    }
}
