package com.github.mhdirkse.countlang.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.github.mhdirkse.countlang.ast.AbstractAstListener;
import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.AstListener;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.AstVisitorToListener;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FormalParameters;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.ReturnStatement;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.ProgramException;
import com.github.mhdirkse.countlang.execution.RunnableFunction;
import com.github.mhdirkse.countlang.execution.StackFrameAccess;

class ProgramRunner extends AbstractAstListener {
    private AstNode root;
    private ExecutionContext executionContext;

    private boolean haveReturnValue = false;

    ProgramRunner(AstNode root, ExecutionContext executionContext) {
        this.root = root;
        this.executionContext = executionContext;
    }

    private class ExecutingVisitor extends AstVisitorToListener {
        ExecutingVisitor(AstListener l) {
            super(l);
        }

        @Override
        public void visitFunctionDefinitionStatement(FunctionDefinitionStatement stmt) {
            ProgramRunner.this.exitFunctionDefinitionStatement(stmt);
        }

        @Override
        public void visitFunctionCallExpression(FunctionCallExpression expr) {
            ProgramRunner.this.enterFunctionCallExpression(expr);
            for (AstNode child : expr.getChildren()) {
                child.accept(this);
            }
            RunnableFunction runnableFunction = ProgramRunner.this.executionContext.getFunction(expr.getFunctionName());
            FunctionDefinitionStatement fun = (FunctionDefinitionStatement) runnableFunction;
            fun.getFormalParameters().accept(this);
            fun.getStatements().accept(this);
            ProgramRunner.this.exitFunctionCallExpression(expr);
        }

        @Override
        public void visitFormalParameters(FormalParameters formalParameters) {
            List<AstNode> reversedChildren = new ArrayList<>();
            reversedChildren.addAll(formalParameters.getChildren());
            Collections.reverse(reversedChildren);
            for(AstNode c: reversedChildren) {
                c.accept(this);
            }
        }

        @Override
        public void visitStatementGroup(StatementGroup statementGroup) {
            ProgramRunner.this.enterStatementGroup(statementGroup);
            try {
                Iterator<AstNode> it = statementGroup.getChildren().iterator();
                while(it.hasNext() && !ProgramRunner.this.haveReturnValue) {
                    it.next().accept(this);
                }
            }
            finally {
                ProgramRunner.this.exitStatementGroup(statementGroup);
            }
        }
    }
    
    public void run() {
        root.accept(new ExecutingVisitor(this));
    }
    
    @Override
    public void enterStatementGroup (final StatementGroup statementGroup) {
        statementGroup.getStackStrategy().before(executionContext);
    }

    @Override
    public void exitStatementGroup (final StatementGroup statementGroup) {
        statementGroup.getStackStrategy().after(executionContext);
    }

    @Override
    public void exitAssignmentStatement (final AssignmentStatement assignmentStatement) {
        Object rhs = executionContext.popValue();
        executionContext.putSymbol(assignmentStatement.getLhs(), rhs);
    }
    
    @Override
    public void exitPrintStatement (final PrintStatement printStatement) {
        String result = executionContext.popValue().toString();
        executionContext.output(result);
    }

    @Override
    public void exitReturnStatement (final ReturnStatement returnStatement) {
        haveReturnValue = true;
    }

    @Override
    public void exitFunctionDefinitionStatement (final FunctionDefinitionStatement functionDefinitionStatement) {
        executionContext.putFunction(functionDefinitionStatement);
    }

    @Override
    public void enterFunctionCallExpression(final FunctionCallExpression functionCallExpression) {
        executionContext.startPreparingNewFrame(StackFrameAccess.HIDE_PARENT);
        executionContext.pushNewFrame();
    }

    @Override
    public void exitFunctionCallExpression (final FunctionCallExpression functionCallExpression) {
        executionContext.popFrame();
        haveReturnValue = false;
    }

    @Override
    public void exitCompositeExpression (final CompositeExpression compositeExpression) {
        List<Object> arguments = new ArrayList<>();
        for(int i = 0; i < compositeExpression.getNumSubExpressions(); ++i) {
            arguments.add(executionContext.popValue());
        }
        Collections.reverse(arguments);
        Object result = compositeExpression.getOperator().execute(arguments);
        executionContext.pushValue(result);
    }

    @Override
    public void visitSymbolExpression (final SymbolExpression symbolExpression) {
        if(!executionContext.hasSymbol(symbolExpression.getSymbol())) {
            throw new ProgramException(
                    symbolExpression.getLine(),
                    symbolExpression.getColumn(),
                    String.format("Unknown symbol %s", symbolExpression.getSymbol()));
        }
        executionContext.pushValue(executionContext.getValue(symbolExpression.getSymbol()));
    }
    
    @Override
    public void visitValueExpression (final ValueExpression valueExpression) {
        executionContext.pushValue(valueExpression.getValue());
    }

    @Override
    public void visitFormalParameter (final FormalParameter formalParameter) {
        Object value = executionContext.popValue();
        String name = formalParameter.getName();
        executionContext.putSymbol(name, value);
    }
}
