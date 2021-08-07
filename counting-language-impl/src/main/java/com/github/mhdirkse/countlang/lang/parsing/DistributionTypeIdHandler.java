package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.DistributionTypeNode;
import com.github.mhdirkse.countlang.ast.TypeNode;

public class DistributionTypeIdHandler extends CompositeTypeIdHandler {
    private DistributionTypeNode result;

    DistributionTypeIdHandler(int line, int column) {
        result = new DistributionTypeNode(line, column);
    }

    @Override
    public TypeNode getTypeNode() {
        return result;
    }
}
