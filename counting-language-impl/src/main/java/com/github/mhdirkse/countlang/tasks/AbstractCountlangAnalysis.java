package com.github.mhdirkse.countlang.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithTotal;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithUnknown;
import com.github.mhdirkse.countlang.ast.ExperimentDefinitionStatement;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FormalParameters;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatementBase;
import com.github.mhdirkse.countlang.ast.IfStatement;
import com.github.mhdirkse.countlang.ast.MarkUsedStatement;
import com.github.mhdirkse.countlang.ast.Operator;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.ReturnStatement;
import com.github.mhdirkse.countlang.ast.SampleStatement;
import com.github.mhdirkse.countlang.ast.SimpleDistributionExpression;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.ast.Visitor;
import com.github.mhdirkse.countlang.execution.BranchHandler;
import com.github.mhdirkse.countlang.execution.FunctionAndReturnCheck;
import com.github.mhdirkse.countlang.execution.FunctionDefinitions;
import com.github.mhdirkse.countlang.execution.StackFrameAccess;
import com.github.mhdirkse.countlang.execution.SymbolFrameStack;
import com.github.mhdirkse.countlang.utils.Stack;

abstract class AbstractCountlangAnalysis<T> implements Visitor {
    final SymbolFrameStack<T> symbols;
    final Stack<T> stack;
    final FunctionAndReturnCheck<T> functionAndReturnCheck;
    final FunctionDefinitions funDefs;
    final StatusReporter reporter;
    final List<BranchHandler> branchHandlers = new ArrayList<>();

    boolean didReturn = false;

    AbstractCountlangAnalysis(
            SymbolFrameStack<T> symbols,
            Stack<T> stack,
            final StatusReporter reporter,
            final FunctionAndReturnCheck<T> functionAndReturnCheck,
            List<FunctionDefinitionStatement> predefinedFuns) {
        this.symbols = symbols;
        this.stack = stack;
        this.functionAndReturnCheck = functionAndReturnCheck;
        this.funDefs = new FunctionDefinitions();
        for(FunctionDefinitionStatement fun: predefinedFuns) {
            funDefs.putFunction(fun);
        }
        this.reporter = reporter;
        branchHandlers.add(symbols);
        branchHandlers.add(functionAndReturnCheck);
    }

    public void visitFunctionDefinitionStatement(final FunctionDefinitionStatement statement) {
        handleFunctionDefinitionStatementBase(statement, this::beforeFunctionLeft);
    }

    private <U extends FunctionDefinitionStatementBase> void handleFunctionDefinitionStatementBase(
            final U statement, FunctionDefinitionFinisher<U> finisher) {
        if(funDefs.hasFunction(statement.getName())) {
            onFunctionRedefined(funDefs.getFunction(statement.getName()), statement);
        }
        if(functionAndReturnCheck.getNestedFunctionDepth() >= 1) {
            onNestedFunction(statement);
        }
        List<T> pseudoArguments = statement.getFormalParameters().getFormalParameters()
                .stream().map(this::getPseudoActualParameter).collect(Collectors.toList());
        runFunction(pseudoArguments, statement, statement.getLine(), statement.getColumn(), finisher);
        funDefs.putFunction(statement);
        
    }

    abstract void onFunctionRedefined(FunctionDefinitionStatementBase previous, FunctionDefinitionStatementBase current);
    abstract void onNestedFunction(FunctionDefinitionStatementBase statement);
    abstract T getPseudoActualParameter(FormalParameter p);

    <U extends FunctionDefinitionStatementBase> void runFunction(List<T> arguments, U fun, int line, int column, FunctionDefinitionFinisher<U> finisher) {
        functionAndReturnCheck.onFunctionEntered(fun.getName(), fun instanceof ExperimentDefinitionStatement);
        runFunctionUnchecked(arguments, fun);
        finisher.apply(fun, line, column);
        functionAndReturnCheck.onFunctionLeft();
    }

    @Override
    public void visitExperimentDefinitionStatement(ExperimentDefinitionStatement statement) {
        handleFunctionDefinitionStatementBase(statement, this::beforeExperimentLeft);
    }

    @FunctionalInterface
    private interface FunctionDefinitionFinisher<U extends FunctionDefinitionStatementBase> {
        public void apply(U statement, int line, int column);
    }

    abstract void beforeFunctionLeft(FunctionDefinitionStatement fun, int line, int column);
    abstract void beforeExperimentLeft(ExperimentDefinitionStatement fun, int line, int column);

    private void runFunctionUnchecked(List<T> arguments, FunctionDefinitionStatementBase fun) {
        int formalParameterCount = fun.getNumParameters();
        symbols.pushFrame(StackFrameAccess.HIDE_PARENT);
        for(int i = 0; i < formalParameterCount; i++) {
            FormalParameter p = fun.getFormalParameters().getFormalParameter(i);
            symbols.write(
                    fun.getFormalParameterName(i),
                    arguments.get(i),
                    p.getLine(),
                    p.getColumn());
        }
        fun.getStatements().accept(this);
        symbols.popFrame();
    }

    public void visitFunctionCallExpression(final FunctionCallExpression expression) {
        expression.getChildren().forEach(c -> c.accept(this));
        List<T> arguments = stack.repeatedPop(expression.getNumArguments());
        String funName = expression.getFunctionName();
        if(funDefs.hasFunction(funName)) {
            FunctionDefinitionStatementBase fun = funDefs.getFunction(funName);
            stack.push(checkFunctionCall(arguments, expression, fun));
        }
        else {
            stack.push(onUndefinedFunctionCalled(expression));
        }
    }

    abstract T checkFunctionCall(List<T> arguments, FunctionCallExpression expr, FunctionDefinitionStatementBase fun);
    abstract T onUndefinedFunctionCalled(FunctionCallExpression expr);

    @Override
    public void visitIfStatement(final IfStatement ifStatement) {
        ExpressionNode selector = ifStatement.getSelector();
        selector.accept(this);
        checkSelectValue(stack.pop(), selector);
        branchHandlers.forEach(h -> h.onSwitchOpened());
        branchHandlers.forEach(h -> h.onBranchOpened());
        ifStatement.getThenStatement().accept(this);
        branchHandlers.forEach(h -> h.onBranchClosed());
        branchHandlers.forEach(h -> h.onBranchOpened());
        AstNode elseNode = ifStatement.getElseStatement();
        if(elseNode != null) {
            elseNode.accept(this);
        }
        branchHandlers.forEach(h -> h.onBranchClosed());
        branchHandlers.forEach(h -> h.onSwitchClosed());
    }

    abstract void checkSelectValue(T value, ExpressionNode selector);

    public void visitStatementGroup(final StatementGroup sg) {
        symbols.pushFrame(StackFrameAccess.SHOW_PARENT);
        for(AstNode c : sg.getChildren()) {
            onStatement(c.getLine(), c.getColumn());
            c.accept(this);
            if(functionAndReturnCheck.isStop()) {
                break;
            }
        }
        symbols.popFrame();
    }
    
    abstract void onStatement(int line, int column);
    
    public void visitAssignmentStatement(final AssignmentStatement statement) {
        statement.getRhs().accept(this);
        T rhs = stack.pop();
        String lhs = statement.getLhs();
        symbols.write(lhs, rhs, statement.getLine(), statement.getColumn());
    }

    @Override
    public void visitSampleStatement(SampleStatement statement) {
        if(! functionAndReturnCheck.isInExperiment()) {
            onSamplingOutsideExperiment(statement);
        }
        statement.getSampledDistribution().accept(this);
        T distributionValue = stack.pop();
        checkSampledDistribution(distributionValue, statement);
        String lhs = statement.getSymbol();
        symbols.write(lhs, getSampleResultValue(), statement.getLine(), statement.getColumn());        
    }

    abstract void onSamplingOutsideExperiment(SampleStatement statement);
    abstract void checkSampledDistribution(T value, SampleStatement statement);
    abstract T getSampleResultValue();

    public void visitPrintStatement(final PrintStatement statement) {
        statement.getExpression().accept(this);
        T rhs = stack.pop();
        doPrint(rhs);
    }

    void doPrint(T value) {        
    }

    public void visitMarkUsedStatement(final MarkUsedStatement statement) {
        statement.getExpression().accept(this);
        stack.pop();
    }

    public void visitReturnStatement(final ReturnStatement statement) {
        statement.getExpression().accept(this);
        functionAndReturnCheck.onReturn(statement.getLine(), statement.getColumn(),
                Arrays.asList(stack.pop()));
        afterReturn(statement.getLine(), statement.getColumn());
    }

    abstract void afterReturn(int line, int column);

    public void visitValueExpression(final ValueExpression expression) {
        stack.push(representValue(expression));
    }

    abstract T representValue(ValueExpression expression);

    public void visitSymbolExpression(final SymbolExpression expression) {
        T value = symbols.read(expression.getSymbol(), expression.getLine(), expression.getColumn());
        onSymbolExpression(value, expression);
        stack.push(value);
    }

    abstract void onSymbolExpression(T value, SymbolExpression expression);

    public void visitCompositeExpression(final CompositeExpression expression) {
        expression.getChildren().forEach(c -> c.accept(this));
        List<T> arguments = stack.repeatedPop(expression.getNumSubExpressions());
        stack.push(doCompositeExpression(arguments, expression));
    }

    public void visitOperator(final Operator operator) {
    }

    abstract T doCompositeExpression(List<T> arguments, CompositeExpression expression);

    @Override
    public void visitSimpleDistributionExpression(SimpleDistributionExpression expression) {
        expression.getChildren().forEach(c -> c.accept(this));
        List<T> arguments = stack.repeatedPop(expression.getNumSubExpressions());
        stack.push(doSimpleDistributionExpression(arguments, expression));
    }

    abstract T doSimpleDistributionExpression(List<T> arguments, SimpleDistributionExpression expression);
    
    @Override
    public void visitDistributionExpressionWithTotal(DistributionExpressionWithTotal expression) {
        expression.getChildren().forEach(c -> c.accept(this));
        List<T> arguments = stack.repeatedPop(expression.getNumSubExpressions());
        stack.push(doDistributionExpressionWithTotal(arguments, expression));
    }

    abstract T doDistributionExpressionWithTotal(List<T> arguments, DistributionExpressionWithTotal expression);

    @Override
    public void visitDistributionExpressionWithUnknown(DistributionExpressionWithUnknown expression) {
        expression.getChildren().forEach(c -> c.accept(this));
        List<T> arguments = stack.repeatedPop(expression.getNumSubExpressions());
        stack.push(doDistributionExpressionWithUnknown(arguments, expression));
    }

    abstract T doDistributionExpressionWithUnknown(List<T> arguments, DistributionExpressionWithUnknown expression);
    
    public void visitFormalParameters(final FormalParameters formalParameters) {
    }
    
    public void visitFormalParameter(final FormalParameter formalParameter) {
    }
}
