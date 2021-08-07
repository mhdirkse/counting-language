/*
 * Copyright Martijn Dirkse 2020
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.mhdirkse.countlang.type.CountlangType;

public class FormalParameters extends AstNode implements CompositeNode {
    private List<FormalParameter> formalParameters = new ArrayList<>();

    public FormalParameters(final int line, final int column) {
        super(line, column);
    }

    public int size() {
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
