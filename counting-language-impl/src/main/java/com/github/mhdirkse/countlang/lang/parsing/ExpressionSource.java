package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.ExpressionNode;

interface ExpressionSource {
    ExpressionNode getExpression();
}
