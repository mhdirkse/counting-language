package com.github.mhdirkse.countlang.tasks;

import java.util.List;

import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithTotal;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithUnknown;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.IfStatement;
import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.ast.SimpleDistributionExpression;
import com.github.mhdirkse.countlang.ast.Statement;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.execution.FunctionAndReturnCheck;
import com.github.mhdirkse.countlang.execution.FunctionAndReturnCheck.SimpleContext;
import com.github.mhdirkse.countlang.execution.FunctionAndReturnCheckSimple;
import com.github.mhdirkse.countlang.execution.OutputStrategy;
import com.github.mhdirkse.countlang.execution.SymbolFrameStackExecute;
import com.github.mhdirkse.countlang.types.Distribution;
import com.github.mhdirkse.countlang.utils.Stack;

class CountlangRunner extends AbstractCountlangVisitor<Object> {
    private final OutputStrategy outputStrategy;

    static CountlangRunner getInstance(
            final OutputStrategy outputStrategy,
            List<FunctionDefinitionStatement> predefinedFuns) {
        FunctionAndReturnCheck<Object> functionAndReturnCheck
                = new FunctionAndReturnCheckSimple<>(SimpleContext::new);
        return new CountlangRunner(outputStrategy, functionAndReturnCheck, predefinedFuns);
    }
    
    private CountlangRunner(
            final OutputStrategy outputStrategy,
            FunctionAndReturnCheck<Object> functionAndReturnCheck,
            List<FunctionDefinitionStatement> predefinedFuns) {
        super(new SymbolFrameStackExecute(), new Stack<Object>(), functionAndReturnCheck, predefinedFuns);
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
    public void visitIfStatement(final IfStatement statement) {
        ExpressionNode selector = statement.getSelector();
        Statement thenStatement = statement.getThenStatement();
        Statement elseStatement = statement.getElseStatement();
        selector.accept(this);
        Object selectValueRaw = stack.pop();
        Boolean selectValue = (Boolean) selectValueRaw;
        if(selectValue.equals(true)) {
            thenStatement.accept(this);
        } else {
            if(elseStatement != null) {
                elseStatement.accept(this);
            }
        }
    }
    
    @Override
    public void doPrint(Object value) {
        if(value instanceof Distribution) {
            outputStrategy.output(((Distribution) value).format());
        } else {
            outputStrategy.output(value.toString());
        }
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

    @Override
    Object doSimpleDistributionExpression(List<Object> arguments, SimpleDistributionExpression expression) {
        Distribution.Builder builder = getDistributionBuilderWithScoredValues(arguments);
        return builder.build();
    }

    private Distribution.Builder getDistributionBuilderWithScoredValues(List<Object> arguments) {
        Distribution.Builder builder = new Distribution.Builder();
        for(Object arg: arguments) {
            builder.add((Integer) arg); 
        }
        return builder;
    }

    @Override
    Object doDistributionExpressionWithTotal(List<Object> arguments, DistributionExpressionWithTotal expression) {
        Distribution.Builder builder = getDistributionBuilderWithScoredValues(
                arguments.subList(1, arguments.size()));
        int total = (Integer) arguments.get(0);
        int totalScored = builder.getTotal();
        if(total >= totalScored) {
            int unknown = total - totalScored;
            builder.addUnknown(unknown);
            return builder.build();
        }
        else {
            throw new ProgramException(
                    expression.getLine(),
                    expression.getColumn(),
                    String.format(
                            "The scored items in the distribution make count %d, which is more than %d",
                            totalScored, total));
        }
    }

    @Override
    Object doDistributionExpressionWithUnknown(List<Object> arguments, DistributionExpressionWithUnknown expression) {
        Distribution.Builder builder = getDistributionBuilderWithScoredValues(
                arguments.subList(1, arguments.size()));
        int unknown = (Integer) arguments.get(0);
        if(unknown >= 0) {
            builder.addUnknown(unknown);
            return builder.build();
        }
        else {
            throw new ProgramException(
                    expression.getLine(),
                    expression.getColumn(),
                    String.format(
                            "The unknown count in a distribution cannot be negative; you tried %d",
                            unknown));
        }
    }

    void onFormalParameter(Object value, FormalParameter parameter) {
    }

    void onParameterCountMismatch(String functionName, int line, int column, int numActual, int numFormal) {
        throw new ProgramException(line, column,
                String.format("Parameter count mismatch calling function %s. Expected %d, actual %d",
                        functionName, numFormal, numActual));
    }

    @Override
    void beforeFunctionLeft(FunctionDefinitionStatement fun, int line, int column) {
        stack.push(functionAndReturnCheck.getReturnValues().get(0));
    }

    @Override
    void afterReturn(int line, int column) {
        functionAndReturnCheck.setStop();
    }

    @Override
    void onStatement(int line, int column) {
    }
}
