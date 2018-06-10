package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.Expression;

interface ExpressionSource {
    Expression getExpression();
}
