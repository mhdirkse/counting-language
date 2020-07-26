package com.github.mhdirkse.countlang.tasks;

import java.util.List;

import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.execution.OutputStrategy;
import com.github.mhdirkse.countlang.execution.SymbolFrameStackExecute;
import com.github.mhdirkse.countlang.utils.Stack;

public class CountlangRunner3 extends AbstractCountlangVisitor<Object> {
    private final OutputStrategy outputStrategy;

    CountlangRunner3(final OutputStrategy outputStrategy, List<FunctionDefinitionStatement> predefinedFuns) {
        super(new SymbolFrameStackExecute(), new Stack<Object>(), predefinedFuns);
        this.outputStrategy = outputStrategy;
    }

    public void visitFunctionDefinitionStatement(final FunctionDefinitionStatement statement) {
        String name = statement.getName();
        if(funDefs.hasFunction(name)) {
            throw new ProgramException(statement.getLine(), statement.getColumn(),
                    String.format("Function is already defined: %s", name));
        }
        else {
            funDefs.putFunction(statement);
        }
    }

    public void visitFunctionCallExpression(final FunctionCallExpression expression) {
        expression.getChildren().forEach(c -> c.accept(this));
        List<Object> arguments = stack.repeatedPop(expression.getNumArguments());
        String funName = expression.getFunctionName();
        if(funDefs.hasFunction(funName)) {
            FunctionDefinitionStatement fun = funDefs.getFunction(funName);
            runFunction(arguments, fun, expression.getLine(), expression.getColumn());
        }
        else {
            throw new ProgramException(expression.getLine(), expression.getColumn(),
                    String.format("Undefined function called: %s", funName));
        }
    }

    @Override
    public void doPrint(Object value) {
        outputStrategy.output(value.toString());
    }

    @Override
    Object representValue(ValueExpression expression) {
        return expression.getValue();
    }

    @Override
    void onSymbolExpression(Object value, SymbolExpression expression) {
    }

    Object doCompositeExpression(List<Object> arguments, CompositeExpression expression) {
        return expression.getOperator().execute(arguments);
    }

    void onFormalParameter(Object value, FormalParameter parameter) {
    }

    void onParameterCountMismatch(String functionName, int line, int column, int numActual, int numFormal) {
        throw new ProgramException(line, column,
                String.format("Parameter count mismatch calling function %s. Expected %d, actual %d",
                        functionName, numFormal, numActual));
    }

    void onActualParameter(Object value, FormalParameter parameter) {
    }
}
