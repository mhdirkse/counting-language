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

package com.github.mhdirkse.countlang.generator;

import com.github.mhdirkse.codegen.compiletime.MethodModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisitorMethodModel extends MethodModel {
    private boolean atomic;
    private MethodModel enterMethod;
    private MethodModel exitMethod;
    private MethodModel visitMethod;

    VisitorMethodModel(final MethodModel m) {
        super(m);
        atomic = false;
    }

    VisitorMethodModel(final VisitorMethodModel orig) {
        super(orig);
        atomic = orig.atomic;
        enterMethod = new MethodModel(orig.enterMethod);
        exitMethod = new MethodModel(orig.exitMethod);
        visitMethod = new MethodModel(orig.visitMethod);
    }
}
