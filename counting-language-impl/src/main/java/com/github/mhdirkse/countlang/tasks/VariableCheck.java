package com.github.mhdirkse.countlang.tasks;

import com.github.mhdirkse.countlang.ast.AbstractAstListener;
import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.AstVisitorToListener;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.StackFrameAccess;
import com.github.mhdirkse.countlang.ast.StackStrategy;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.Visitor;

class VariableCheck extends AbstractAstListener {
    private final VariableCheckContext ctx = new VariableCheckContextImpl();

    private final StatusReporter reporter;

    VariableCheck(final StatusReporter reporter) {
        this.reporter = reporter;
        ctx.pushNewFrame(StackFrameAccess.HIDE_PARENT);
    }

    void run(final StatementGroup statementGroup) {
        Visitor v = new AstVisitorToListener(this);
        statementGroup.accept(v);
        ctx.report(reporter);
    }

    @Override
    public void enterFunctionDefinitionStatement(final FunctionDefinitionStatement statement) {
        ctx.pushNewFrame(StackFrameAccess.HIDE_PARENT);
    }

    @Override
    public void visitFormalParameter(final FormalParameter parameter) {
        ctx.define(parameter.getName(), parameter.getLine(), parameter.getColumn());
    }

    @Override
    public void exitFunctionDefinitionStatement(final FunctionDefinitionStatement statement) {
        ctx.popFrame();
    }

    @Override
    public void enterStatementGroup(final StatementGroup statementGroup) {
        switch(statementGroup.getStackStrategy()) {
        case NO_NEW_FRAME:
            break;
        case NEW_FRAME_SHOWING_PARENT:
            ctx.pushNewFrame(StackFrameAccess.SHOW_PARENT);
            break;
        case NEW_FRAME_HIDING_PARENT:
            ctx.pushNewFrame(StackFrameAccess.HIDE_PARENT);
            break;
        }
    }

    @Override
    public void exitStatementGroup(final StatementGroup statementGroup) {
        if(statementGroup.getStackStrategy() != StackStrategy.NO_NEW_FRAME) {
            ctx.popFrame();
        }
    }

    @Override
    public void exitAssignmentStatement(final AssignmentStatement statement) {
        ctx.define(statement.getLhs(), statement.getLine(), statement.getColumn());
    }

    @Override
    public void visitSymbolExpression(final SymbolExpression expression) {
        ctx.use(expression.getSymbol(), expression.getLine(), expression.getColumn());
    }
}
