package com.github.mhdirkse.countlang.tasks;

import java.util.List;

import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithTotal;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithUnknown;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.ast.SimpleDistributionExpression;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.execution.DummyValue;
import com.github.mhdirkse.countlang.execution.FunctionAndReturnCheck;
import com.github.mhdirkse.countlang.execution.FunctionAndReturnCheck.SimpleContext;
import com.github.mhdirkse.countlang.execution.FunctionAndReturnCheckSimple;
import com.github.mhdirkse.countlang.execution.SymbolFrameStackVariableUsage;
import com.github.mhdirkse.countlang.execution.VariableUsageEventHandler;
import com.github.mhdirkse.countlang.utils.Stack;

class VariableCheck extends AbstractCountlangAnalysis<DummyValue> implements VariableUsageEventHandler {
    private final SymbolFrameStackVariableUsage symbols;

    public static VariableCheck getInstance(
            final StatusReporter reporter,
            List<FunctionDefinitionStatement> predefinedFuns) {
        SymbolFrameStackVariableUsage symbols = new SymbolFrameStackVariableUsage();
        FunctionAndReturnCheck<DummyValue> functionAndReturnCheck =
                new FunctionAndReturnCheckSimple<>(SimpleContext<DummyValue>::new);
        VariableCheck instance = new VariableCheck(symbols, reporter, functionAndReturnCheck, predefinedFuns);
        return instance;
    }

    private VariableCheck(
            final SymbolFrameStackVariableUsage stackFrame,
            final StatusReporter reporter,
            final FunctionAndReturnCheck<DummyValue> functionAndReturnCheck,
            List<FunctionDefinitionStatement> predefinedFuns) {
        super(stackFrame, new Stack<DummyValue>(), reporter, functionAndReturnCheck, predefinedFuns);
        this.symbols = stackFrame;
    }

    @Override
    void onFunctionRedefined(FunctionDefinitionStatement previous, FunctionDefinitionStatement current) {
        throw new ProgramException(
                current.getLine(),
                current.getColumn(),
                "Should have been caught during type check");
    }

    @Override
    DummyValue getPseudoActualParameter(FormalParameter formalParameter) {
        return DummyValue.getInstance();
    }

    @Override
    void beforeFunctionLeft(FunctionDefinitionStatement fun, int line, int column) {
    }

    @Override
    void checkSelectValue(final DummyValue value, final ExpressionNode selector) {       
    }

    @Override
    DummyValue doCompositeExpression(
            final List<DummyValue> arguments,
            final CompositeExpression compositeExpression) {
        return DummyValue.getInstance();
    }

    @Override
    DummyValue checkFunctionCall(
            final List<DummyValue> arguments,
            final FunctionCallExpression expr,
            final FunctionDefinitionStatement fun) {
        return DummyValue.getInstance();
    }

    @Override
    DummyValue onUndefinedFunctionCalled(FunctionCallExpression functionCallExpression) {
        return DummyValue.getInstance();
    }

    @Override
    DummyValue representValue(ValueExpression valueExpression) {
        return DummyValue.getInstance();
    }

    @Override
    void onSymbolExpression(DummyValue value, SymbolExpression symbolExpression) {
    }

    public void listEvents() {
        symbols.listEvents(this);
    }

    @Override
    public void variableNotUsed(String name, int line, int column) {
        reporter.report(
                StatusCode.VAR_NOT_USED,
                line,
                column,
                name);
    }

    @Override
    void onNestedFunction(FunctionDefinitionStatement statement) {
        throw new IllegalArgumentException("Should have been caught by the type check");
    }

    @Override
    void afterReturn(int line, int column) {       
    }

    @Override
    void onStatement(int line, int column) {
    }

    @Override
    DummyValue doSimpleDistributionExpression(
            List<DummyValue> arguments, SimpleDistributionExpression expression) {
        return DummyValue.getInstance();
    }

    @Override
    DummyValue doDistributionExpressionWithTotal(
            List<DummyValue> arguments, DistributionExpressionWithTotal expression) {
        return DummyValue.getInstance();
    }

    @Override
    DummyValue doDistributionExpressionWithUnknown(
            List<DummyValue> arguments, DistributionExpressionWithUnknown expression) {
        return DummyValue.getInstance();
    }
}
