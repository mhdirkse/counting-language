package com.github.mhdirkse.countlang.tasks;

import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.AstListener;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FormalParameters;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.Operator;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.Program;
import com.github.mhdirkse.countlang.ast.ReturnStatement;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.ast.Visitor;
import com.github.mhdirkse.countlang.ast.AstVisitorToListener;

class VariableCheck implements AstListener {
    private final VariableCheckContext ctx = new VariableCheckContextImpl();

    private final StatusReporter reporter;

    VariableCheck(final StatusReporter reporter) {
        this.reporter = reporter;
    }

    void run(final Program program) {
        Visitor v = new AstVisitorToListener(this);
        program.accept(v);
        ctx.report(reporter);
    }

    @Override
    public void enterProgram(final Program p1) {
        ctx.pushNewFrame();
    }

    @Override
    public void exitProgram(final Program p1) {
        ctx.popFrame();
    }

    @Override
    public void enterAssignmentStatement(final AssignmentStatement p1) {
    }

    @Override
    public void exitAssignmentStatement(final AssignmentStatement statement) {
        ctx.define(statement.getLhs().getName(), statement.getLine(), statement.getColumn());
    }

    @Override
    public void enterPrintStatement(final PrintStatement p1) {
    }

    @Override
    public void exitPrintStatement(final PrintStatement p1) {
    }

    @Override
    public void enterFunctionDefinitionStatement(final FunctionDefinitionStatement p1) {
        ctx.pushNewFrame();
    }

    @Override
    public void exitFunctionDefinitionStatement(final FunctionDefinitionStatement p1) {
        ctx.popFrame();
    }

    @Override
    public void enterReturnStatement(final ReturnStatement p1) {
    }

    @Override
    public void exitReturnStatement(final ReturnStatement p1) {
    }

    @Override
    public void enterCompositeExpression(final CompositeExpression p1) {
    }

    @Override
    public void exitCompositeExpression(final CompositeExpression p1) {
    }

    @Override
    public void visitOperator(final Operator p1) {
    }

    @Override
    public void enterFunctionCallExpression(final FunctionCallExpression p1) {
    }

    @Override
    public void exitFunctionCallExpression(final FunctionCallExpression p1) {
    }

    @Override
    public void visitSymbolExpression(final SymbolExpression expression) {
        ctx.use(expression.getSymbol().getName(), expression.getLine(), expression.getColumn());
    }

    @Override
    public void visitValueExpression(final ValueExpression p1) {
    }

    @Override
    public void enterFormalParameters(final FormalParameters p1) {
    }

    @Override
    public void exitFormalParameters(final FormalParameters p1) {
    }

    @Override
    public void visitFormalParameter(final FormalParameter parameter) {
        ctx.define(parameter.getName(), parameter.getLine(), parameter.getColumn());
    }
}
