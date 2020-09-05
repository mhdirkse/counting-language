package com.github.mhdirkse.countlang.tasks;

import static com.github.mhdirkse.countlang.execution.BranchingReturnCheck.Status.SOME_RETURN;

import java.util.List;

import com.github.mhdirkse.countlang.ast.AbstractDistributionExpression;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithTotal;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithUnknown;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.SimpleDistributionExpression;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.execution.FunctionAndReturnCheck;
import com.github.mhdirkse.countlang.execution.FunctionAndReturnTypeCheck;
import com.github.mhdirkse.countlang.execution.SymbolFrameStackTypeCheck;
import com.github.mhdirkse.countlang.execution.SymbolNotAccessibleHandler;
import com.github.mhdirkse.countlang.utils.Stack;

class TypeCheck extends AbstractCountlangAnalysis<CountlangType>
implements SymbolNotAccessibleHandler, FunctionAndReturnTypeCheck.Callback {
    public static TypeCheck getInstance(
            final StatusReporter reporter,
            List<FunctionDefinitionStatement> predefinedFuns) {
        SymbolFrameStackTypeCheck symbols = new SymbolFrameStackTypeCheck();
        FunctionAndReturnTypeCheck functionAndReturnCheck = new FunctionAndReturnTypeCheck(
                FunctionAndReturnCheck.TypeCheckContext::new);
        TypeCheck instance = new TypeCheck(symbols, reporter, functionAndReturnCheck, predefinedFuns);
        functionAndReturnCheck.setCallback(instance);
        symbols.setHandler(instance);
        return instance;
    }

    private TypeCheck(
            final SymbolFrameStackTypeCheck symbols,
            final StatusReporter reporter,
            FunctionAndReturnTypeCheck functionAndReturnCheck,
            List<FunctionDefinitionStatement> predefinedFuns) {
        super(symbols, new Stack<CountlangType>(), reporter, functionAndReturnCheck, predefinedFuns);
    }
    
    @Override
    void onFunctionRedefined(FunctionDefinitionStatement previous, FunctionDefinitionStatement current) {
        reporter.report(
                StatusCode.FUNCTION_ALREADY_DEFINED,
                current.getLine(),
                current.getColumn(),
                current.getName());
    }

    @Override
    CountlangType getPseudoActualParameter(FormalParameter formalParameter) {
        return formalParameter.getCountlangType();
    }

    @Override
    void beforeFunctionLeft(FunctionDefinitionStatement fun, int line, int column) {
        FunctionAndReturnTypeCheck cast = (FunctionAndReturnTypeCheck) functionAndReturnCheck;
        boolean doesNotReturn =
                (functionAndReturnCheck.getReturnStatus() == SOME_RETURN)
                || (cast.getNumReturnValues() != 1);
        if(doesNotReturn) {
            reporter.report(StatusCode.FUNCTION_DOES_NOT_RETURN, line, column, fun.getName());
        }
        else {
            fun.setReturnType(cast.getReturnValues().get(0));
        }
    }

    @Override
    void checkSelectValue(final CountlangType value, final ExpressionNode selector) {
        if(!value.equals(CountlangType.BOOL)) {
            reporter.report(
                    StatusCode.IF_SELECT_NOT_BOOLEAN,
                    selector.getLine(),
                    selector.getColumn(),
                    selector.getCountlangType().toString());
        }
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
            throw new IllegalStateException("Cannot happen, would be a syntax error");
        } else if(compositeExpression.getOperator().checkAndEstablishTypes(arguments)) {
            result = compositeExpression.getOperator().getResultType();
        }
        else {
            reporter.report(
                    StatusCode.OPERATOR_TYPE_MISMATCH,
                    compositeExpression.getLine(),
                    compositeExpression.getColumn(),
                    compositeExpression.getOperator().getName());
        }
        compositeExpression.setCountlangType(result);
        return result;
    }

    @Override
    CountlangType doSimpleDistributionExpression(
            List<CountlangType> arguments, SimpleDistributionExpression expression) {
        if(checkDistributionScoredExpressions(arguments, expression.getChildren())) {
            return CountlangType.DISTRIBUTION;
        } else {
            return CountlangType.UNKNOWN;
        }
    }

    private boolean checkDistributionScoredExpressions(
            List<CountlangType> scoredTypes, List<AstNode> scoredExpressions) {
        boolean typesOk = true;
        for(int i = 0; i < scoredTypes.size(); i++) {
            if(!scoredTypes.get(i).equals(CountlangType.INT)) {
                typesOk = false;
                reporter.report(
                        StatusCode.DISTRIBUTION_SCORED_NOT_INT,
                        scoredExpressions.get(i).getLine(),
                        scoredExpressions.get(i).getColumn(),
                        Integer.toString(i),
                        scoredTypes.get(i).toString());
            }
        }
        return typesOk;
    }

    CountlangType doDistributionExpressionWithTotal(
            List<CountlangType> arguments, DistributionExpressionWithTotal expression) {
        return doDistributionExpressionWithAmount(arguments, expression);
    }

   CountlangType doDistributionExpressionWithUnknown(
            List<CountlangType> arguments, DistributionExpressionWithUnknown expression) {
        return doDistributionExpressionWithAmount(arguments, expression);
    }

    CountlangType doDistributionExpressionWithAmount(
            List<CountlangType> arguments, AbstractDistributionExpression expression) {
        CountlangType amountType = arguments.get(0);
        ExpressionNode expressionAmount = (ExpressionNode) expression.getChildren().get(0);
        List<CountlangType> scoredTypes = arguments.subList(1, arguments.size());
        List<AstNode> scoredExpressions = expression.getChildren().subList(1, expression.getNumSubExpressions());
        boolean scoredTypesOk = checkDistributionScoredExpressions(scoredTypes, scoredExpressions);
        boolean amountTypeOk = true;
        if(!amountType.equals(CountlangType.INT)) {
            amountTypeOk = false;
            reporter.report(
                    StatusCode.DISTRIBUTION_AMOUNT_NOT_INT,
                    expressionAmount.getLine(),
                    expressionAmount.getColumn());
        }
        if(scoredTypesOk && amountTypeOk) {
            return CountlangType.DISTRIBUTION;
        } else {
            return CountlangType.UNKNOWN;
        }
    }

    @Override
    CountlangType checkFunctionCall(
            final List<CountlangType> arguments,
            final FunctionCallExpression funCallExpr,
            final FunctionDefinitionStatement funDefStatement) {
        if(arguments.size() != funDefStatement.getNumParameters()) {
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

    @Override
    void onStatement(int line, int column) {
        ((FunctionAndReturnTypeCheck) functionAndReturnCheck).onStatement(line, column);
    }

    @Override
    public void reportStatementHasNoEffect(int line, int column, String functionName) {
        reporter.report(
                StatusCode.FUNCTION_STATEMENT_WITHOUT_EFFECT,
                line, column, functionName);
        functionAndReturnCheck.setStop();
    }

    @Override
    public void reportInconsistentReturnType(int lineOrigType, int columnOrigType, int line, int column, String functionName ) {
        throw new IllegalStateException("Not yet implemented");
    }

    @Override
    void onNestedFunction(FunctionDefinitionStatement statement) {
        reporter.report(
                StatusCode.FUNCTION_NESTED_NOT_ALLOWED, statement.getLine(), statement.getColumn());
    }

    @Override
    public void afterReturn(int line, int column) {
        if(functionAndReturnCheck.getNestedFunctionDepth() == 0) {
            reporter.report(StatusCode.RETURN_OUTSIDE_FUNCTION, line, column);
        }
    }
}
