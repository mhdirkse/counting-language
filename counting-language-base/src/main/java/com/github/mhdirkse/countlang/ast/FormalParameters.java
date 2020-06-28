package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

public class FormalParameters extends AstNode implements CompositeNode {
    private List<FormalParameter> formalParameters = new ArrayList<>();

    public FormalParameters(final int line, final int column) {
        super(line, column);
    }

    int size() {
        return formalParameters.size();
    }

    String getFormalParameterName(int i) {
        return formalParameters.get(i).getName();
    }

    CountlangType getFormalParameterType(int i) {
        return formalParameters.get(i).getCountlangType();
    }

    public void addFormalParameter(final FormalParameter formalParameter) {
        formalParameters.add(formalParameter);
    }

    @Override
    public void accept(Visitor v) {
        v.visitFormalParameters(this);
    }

    @Override
    public List<AstNode> getChildren() {
        List<AstNode> result = new ArrayList<>();
        result.addAll(formalParameters);
        return result;
    }
}
