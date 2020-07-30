package com.github.mhdirkse.countlang.tasks;

import java.util.Arrays;
import java.util.List;

import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FormalParameters;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.IfStatement;
import com.github.mhdirkse.countlang.ast.MarkUsedStatement;
import com.github.mhdirkse.countlang.ast.Operator;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.ReturnStatement;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.ast.Visitor;
import com.github.mhdirkse.countlang.execution.FunctionAndReturnCheck;
import com.github.mhdirkse.countlang.execution.FunctionDefinitions;
import com.github.mhdirkse.countlang.execution.StackFrameAccess;
import com.github.mhdirkse.countlang.execution.SymbolFrameStack;
import com.github.mhdirkse.countlang.utils.Stack;

abstract class AbstractCountlangVisitor<T> 
implements Visitor {
    final SymbolFrameStack<T> symbols;
    final Stack<T> stack;
    final FunctionAndReturnCheck<T> functionAndReturnCheck;
    final FunctionDefinitions funDefs;

    boolean didReturn = false;

    AbstractCountlangVisitor(
            SymbolFrameStack<T> symbols,
            Stack<T> stack,
            FunctionAndReturnCheck<T> functionAndReturnCheck,
            List<FunctionDefinitionStatement> predefinedFuns) {
        this.symbols = symbols;
        this.stack = stack;
        this.functionAndReturnCheck = functionAndReturnCheck;
        this.funDefs = new FunctionDefinitions();
        for(FunctionDefinitionStatement fun: predefinedFuns) {
            funDefs.putFunction(fun);
        }
    }

    void runFunction(List<T> arguments, FunctionDefinitionStatement fun, int line, int column) {
        functionAndReturnCheck.onFunctionEntered(fun.getName());
        runFunctionUnchecked(arguments, fun);
        beforeFunctionLeft(fun, line, column);
        functionAndReturnCheck.onFunctionLeft();
    }

    abstract void beforeFunctionLeft(FunctionDefinitionStatement fun, int line, int column);

    private void runFunctionUnchecked(List<T> arguments, FunctionDefinitionStatement fun) {
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

    @Override
    public void visitIfStatement(final IfStatement ifStatement) {
        // TODO: Implement
    }

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

    public void visitPrintStatement(final PrintStatement statement) {
        statement.getExpression().accept(this);
        T rhs = stack.pop();
        doPrint(rhs);
    }

    abstract void doPrint(T value);

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

    public void visitFormalParameters(final FormalParameters formalParameters) {
    }
    
    public void visitFormalParameter(final FormalParameter formalParameter) {
    }
}
