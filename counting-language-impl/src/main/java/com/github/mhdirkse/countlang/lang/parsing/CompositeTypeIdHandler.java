package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.CompositeTypeNode;
import com.github.mhdirkse.countlang.ast.TypeNode;

abstract class CompositeTypeIdHandler extends AbstractTypeIdStrategy implements TypeIdSource {
    @Override
    void handleChild(TypeNode child) {
        ((CompositeTypeNode) getTypeNode()).addChild(child);
    }
}
