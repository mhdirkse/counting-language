package com.github.mhdirkse.countlang.lang.parsing;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.lang.CountlangParser;

class VarDeclsHandler extends AbstractTerminalHandler {
    private List<String> formalParameters = new ArrayList<>();

    List<String> getFormalParameters() {
        return formalParameters;
    }

    @Override
    public int getRequiredType() {
        return CountlangParser.ID;
    }

    @Override
    public void setText(final String text) {
        formalParameters.add(text);
    }
}
