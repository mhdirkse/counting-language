package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.ArrayTypeNode;
import com.github.mhdirkse.countlang.ast.TypeNode;

public class ArrayTypeIdHandler extends CompositeTypeIdHandler {
    private ArrayTypeNode result;

    ArrayTypeIdHandler(int line, int column) {
        result = new ArrayTypeNode(line, column);
    }

    @Override
    public TypeNode getTypeNode() {
        return result;
    }
}
