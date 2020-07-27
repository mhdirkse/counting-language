package com.github.mhdirkse.countlang.tasks;

import java.util.List;

import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.execution.SymbolFrameStackTypeCheck;
import com.github.mhdirkse.countlang.execution.SymbolNotAccessibleHandler;
import com.github.mhdirkse.countlang.utils.Stack;

class TypeCheck2 extends AbstractCountlangAnalysis<CountlangType> implements SymbolNotAccessibleHandler {
    public static TypeCheck2 getInstance(
            final StatusReporter reporter,
            List<FunctionDefinitionStatement> predefinedFuns) {
        SymbolFrameStackTypeCheck symbols = new SymbolFrameStackTypeCheck();
        TypeCheck2 instance = new TypeCheck2(symbols, reporter, predefinedFuns);
        symbols.setHandler(instance);
        return instance;
    }

    private TypeCheck2(
            final SymbolFrameStackTypeCheck symbols,
            final StatusReporter reporter,
            List<FunctionDefinitionStatement> predefinedFuns) {
        super(symbols, new Stack<CountlangType>(), reporter, predefinedFuns);
    }
    
    @Override
    CountlangType getPseudoActualParameter(FormalParameter formalParameter) {
        return formalParameter.getCountlangType();
    }

    @Override
    void checkReturnValue(CountlangType returnValue, FunctionDefinitionStatement funDefStatement) {
        funDefStatement.setReturnType(returnValue);
    }

    @Override
    CountlangType representValue(ValueExpression valueExpression) {
        return CountlangType.typeOf(valueExpression.getValue());
    }

    @Override
    void onSymbolExpression(CountlangType value, SymbolExpression symbolExpression) {
        symbolExpression.setCountlangType(value);
    }

    @Override
    CountlangType doCompositeExpression(final List<CountlangType> arguments, final CompositeExpression compositeExpression) {
        CountlangType result = CountlangType.UNKNOWN;
        if(compositeExpression.getOperator().getNumArguments() != arguments.size()) {
            reporter.report(
                    StatusCode.OPERATOR_ARGUMENT_COUNT_MISMATCH,
                    compositeExpression.getLine(),
                    compositeExpression.getColumn(),
                    compositeExpression.getOperator().toString(),
                    Integer.valueOf(compositeExpression.getOperator().getNumArguments()).toString(),
                    Integer.valueOf(arguments.size()).toString());
        } else if(compositeExpression.getOperator().checkAndEstablishTypes(arguments)) {
            result = compositeExpression.getOperator().getResultType();
        }
        else {
            reporter.report(
                    StatusCode.OPERATOR_TYPE_MISMATCH,
                    compositeExpression.getLine(),
                    compositeExpression.getColumn(),
                    compositeExpression.getOperator().getResultType().toString());
        }
        compositeExpression.setCountlangType(result);
        return result;
    }

    @Override
    CountlangType checkFunctionCall(
            final List<CountlangType> arguments,
            final FunctionCallExpression funCallExpr,
            final FunctionDefinitionStatement funDefStatement) {
        if(arguments.size() != funCallExpr.getNumArguments()) {
            reporter.report(
                    StatusCode.FUNCTION_ARGUMENT_COUNT_MISMATCH,
                    funCallExpr.getLine(),
                    funCallExpr.getColumn(),
                    funCallExpr.getFunctionName(),
                    Integer.valueOf(funCallExpr.getNumArguments()).toString(),
                    Integer.valueOf(arguments.size()).toString());
            return CountlangType.UNKNOWN;
        }
        for(int i = 0; i < arguments.size(); i++) {
            if(!arguments.get(i).equals(funDefStatement.getFormalParameterType(i))) {
                reporter.report(
                        StatusCode.FUNCTION_TYPE_MISMATCH,
                        funCallExpr.getLine(),
                        funCallExpr.getColumn(),
                        funCallExpr.getFunctionName(),
                        funDefStatement.getFormalParameterName(i));
                return CountlangType.UNKNOWN;
            }
        }
        CountlangType resultType = funDefStatement.getReturnType();
        funCallExpr.setCountlangType(resultType);
        return resultType;
    }

    @Override
    public CountlangType onUndefinedFunctionCalled(final FunctionCallExpression funCallExpr) {
        reporter.report(
                StatusCode.FUNCTION_DOES_NOT_EXIST,
                funCallExpr.getLine(),
                funCallExpr.getColumn(),
                funCallExpr.getFunctionName());
        return CountlangType.UNKNOWN;
    }

    @Override
    void onParameterCountMismatch(String functionName, int line, int column, int numActual, int numFormal) {
        reporter.report(
                StatusCode.FUNCTION_ARGUMENT_COUNT_MISMATCH,
                line,
                column,
                functionName,
                Integer.valueOf(numFormal).toString(),
                Integer.valueOf(numActual).toString());
    }

    @Override
    public void notReadable(String name, int line, int column) {
        reporter.report(
                StatusCode.VAR_UNDEFINED,
                line,
                column,
                name);
    }

    @Override
    public void notWritable(String name, int line, int column) {
        reporter.report(
                StatusCode.VAR_TYPE_CHANGED,
                line,
                column,
                name);
    }
}
