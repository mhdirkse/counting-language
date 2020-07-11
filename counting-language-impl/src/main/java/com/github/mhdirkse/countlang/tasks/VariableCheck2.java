package com.github.mhdirkse.countlang.tasks;

import java.util.List;

import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.execution.CountlangStack;
import com.github.mhdirkse.countlang.execution.DummyValue;
import com.github.mhdirkse.countlang.execution.SymbolFrameStackVariableUsage;
import com.github.mhdirkse.countlang.execution.VariableUsageEventHandler;

public class VariableCheck2 extends AbstractCountlangAnalysis<DummyValue> implements VariableUsageEventHandler {
    private final SymbolFrameStackVariableUsage symbols;

    public static VariableCheck2 getInstance(
            final StatusReporter reporter,
            List<FunctionDefinitionStatement> predefinedFuns) {
        SymbolFrameStackVariableUsage symbols = new SymbolFrameStackVariableUsage();
        VariableCheck2 instance = new VariableCheck2(symbols, reporter, predefinedFuns);
        return instance;
    }

    private VariableCheck2(
            final SymbolFrameStackVariableUsage stackFrame,
            final StatusReporter reporter,
            List<FunctionDefinitionStatement> predefinedFuns) {
        super(stackFrame, new CountlangStack<DummyValue>(), reporter, predefinedFuns);
        this.symbols = stackFrame;
    }

    @Override
    DummyValue getPseudoActualParameter(FormalParameter formalParameter) {
        return DummyValue.getInstance();
    }

    @Override
    void checkReturnValue(DummyValue returnValue, FunctionDefinitionStatement funDefStatement) {
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
}
