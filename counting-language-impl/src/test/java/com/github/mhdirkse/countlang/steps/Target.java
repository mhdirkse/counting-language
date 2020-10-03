package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.Operator;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;

class Target {
    static CompositeExpression getCompositeExpression() {
        ValueExpression firstOperand = new ValueExpression(1, 1, 5);
        SymbolExpression secondOperand = new SymbolExpression(1, 3, "x");
        Operator operatorAdd = new Operator.OperatorAdd(1, 2);
        CompositeExpression target = new CompositeExpression(1, 1);
        target.setOperator(operatorAdd);
        target.addSubExpression(firstOperand);
        target.addSubExpression(secondOperand);
        return target;
    }
}
