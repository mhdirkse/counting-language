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

package com.github.mhdirkse.countlang.generator.test;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.generator.test.input.AtomicAcceptor;
import com.github.mhdirkse.countlang.generator.test.input.CompositeAcceptor;

import lombok.Getter;

class VisitorListenerImpl implements VisitorListener {
    @Getter
    private List<String> visitedNames = new ArrayList<>();

    @Override
    public void enterCompositeAcceptor(CompositeAcceptor ctx) {
        visitedNames.add(ctx.getName());
    }

    @Override
    public void exitCompositeAcceptor(CompositeAcceptor ctx) {
        visitedNames.add(ctx.getName());
    }

    @Override
    public void visitAtomicAcceptor(AtomicAcceptor ctx) {
        visitedNames.add(ctx.getName());
    }
}
