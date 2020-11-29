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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.github.mhdirkse.codegen.compiletime.ClassModel;
import com.github.mhdirkse.codegen.compiletime.MethodModel;

public class VisitorClassModel extends ClassModel {
    private Set<String> atomicMethods;
    private String enterPrefix;
    private String exitPrefix;

    VisitorClassModel(final ClassModel orig) {
        super(orig);
    }

    void extendMethods(
            final Set<String> atomicMethods,
            final String enterPrefix,
            final String exitPrefix) {
        this.atomicMethods = atomicMethods;
        this.enterPrefix = enterPrefix;
        this.exitPrefix = exitPrefix;
        getMethods().replaceAll(m -> getMethodExtra(m));
    }

    private VisitorMethodModel getMethodExtra(
            final MethodModel orig) {
        if(atomicMethods.contains(orig.getParameterTypes().get(0))) {
            return getMethodExtraAtomic(orig);
        } else {
            return getMethodExtraComposite(orig);
        }
    }

    private VisitorMethodModel getMethodExtraAtomic(final MethodModel orig) {
        VisitorMethodModel result = new VisitorMethodModel(orig);
        result.setAtomic(true);
        result.setVisitMethod(new MethodModel(orig));
        return result;
    }

    private VisitorMethodModel getMethodExtraComposite(final MethodModel orig) {
        VisitorMethodModel result = new VisitorMethodModel(orig);
        result.setAtomic(false);
        result.setEnterMethod(getListenerMethod(orig, enterPrefix));
        result.setExitMethod(getListenerMethod(orig, exitPrefix));
        return result;
    }

    static MethodModel getListenerMethod(final MethodModel m, final String prefix) {
        MethodModel result = new MethodModel(m);
        String withoutVisit = m.getName().replaceFirst("^visit", "");
        result.setName(prefix + StringUtils.capitalize(withoutVisit));
        return result;
    }

    List<MethodModel> getListenerMethods() {
        return getMethods().stream()
            .map(m -> (VisitorMethodModel) m)
            .map(m -> Arrays.asList(
                    m.getVisitMethod(),
                    m.getEnterMethod(),
                    m.getExitMethod()))
            .flatMap(Collection::stream)
            .filter(Objects::nonNull)
            .map(m -> new MethodModel(m))
            .collect(Collectors.toList());
    }
}
