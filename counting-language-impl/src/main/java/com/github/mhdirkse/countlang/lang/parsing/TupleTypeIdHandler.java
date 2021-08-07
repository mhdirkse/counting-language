package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.TupleTypeNode;
import com.github.mhdirkse.countlang.ast.TypeNode;

public class TupleTypeIdHandler extends CompositeTypeIdHandler {
    private TupleTypeNode result;

    TupleTypeIdHandler(int line, int column) {
        result = new TupleTypeNode(line, column);
    }

    @Override
    public TypeNode getTypeNode() {
        return result;
    }
}
