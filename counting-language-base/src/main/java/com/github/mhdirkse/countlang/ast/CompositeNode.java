package com.github.mhdirkse.countlang.ast;

import java.util.List;

public interface CompositeNode {
    List<AstNode> getChildren();
}
