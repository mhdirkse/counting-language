package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.Collections;
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

    public FormalParameter getFormalParameter(int i) {
        return formalParameters.get(i);
    }

    CountlangType getFormalParameterType(int i) {
        return formalParameters.get(i).getCountlangType();
    }

    public void addFormalParameter(final FormalParameter formalParameter) {
        formalParameters.add(formalParameter);
    }

    public List<FormalParameter> getFormalParameters() {
        List<FormalParameter> result = new ArrayList<>(formalParameters.size());
        result.addAll(formalParameters);
        return result;
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

    public List<AstNode> getReversedFormalParameters() {
        List<AstNode> result = new ArrayList<>();
        result.addAll(formalParameters);
        Collections.reverse(result);
        return result;
    }
}
