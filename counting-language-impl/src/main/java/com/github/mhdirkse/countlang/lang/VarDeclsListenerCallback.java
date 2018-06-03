package com.github.mhdirkse.countlang.lang;

import com.github.mhdirkse.countlang.ast.AstNode;

interface VarDeclsListenerCallback extends AstNode.Visitor {
    void onVarDeclsDone();
}
