package com.github.mhdirkse.countlang.tasks;

import java.util.List;
import java.util.stream.Collectors;

import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.execution.SymbolFrameStack;
import com.github.mhdirkse.countlang.utils.Stack;

abstract public class AbstractCountlangAnalysis<T> extends AbstractCountlangVisitor<T> {
    final StatusReporter reporter;

    AbstractCountlangAnalysis(
            SymbolFrameStack<T> symbols,
            Stack<T> stack,
            final StatusReporter reporter,
            List<FunctionDefinitionStatement> predefinedFuns) {
        super(symbols, stack, predefinedFuns);
        this.reporter = reporter;
    }

    public void visitFunctionDefinitionStatement(final FunctionDefinitionStatement statement) {
        List<T> pseudoArguments = statement.getFormalParameters().getFormalParameters()
                .stream().map(this::getPseudoActualParameter).collect(Collectors.toList());
        runFunction(pseudoArguments, statement, statement.getLine(), statement.getColumn());
        checkReturnValue(stack.pop(), statement);
        funDefs.putFunction(statement);
    }

    @Override
    void onParameterCountMismatch(String functionName, int line, int column, int numActual, int numFormal) {
        throw new IllegalStateException("Should not happen, because pseudo actual parameters are derived from formal parameters");
    }

    abstract T getPseudoActualParameter(FormalParameter p);
    abstract void checkReturnValue(T value, FunctionDefinitionStatement fun);

    public void visitFunctionCallExpression(final FunctionCallExpression expression) {
        expression.getChildren().forEach(c -> c.accept(this));
        List<T> arguments = stack.repeatedPop(expression.getNumArguments());
        String funName = expression.getFunctionName();
        if(funDefs.hasFunction(funName)) {
            FunctionDefinitionStatement fun = funDefs.getFunction(funName);
            stack.push(checkFunctionCall(arguments, expression, fun));
        }
        else {
            stack.push(onUndefinedFunctionCalled(expression));
        }
    }

    abstract T checkFunctionCall(List<T> arguments, FunctionCallExpression expr, FunctionDefinitionStatement fun);
    abstract T onUndefinedFunctionCalled(FunctionCallExpression expr);

    void doPrint(T value) {        
    }
}
