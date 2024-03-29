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

import java.math.BigInteger;
import java.util.Arrays;

import com.github.mhdirkse.countlang.ast.Operator.OperatorAdd;
import com.github.mhdirkse.countlang.type.CountlangType;

public final class TestFunctionDefinitions {

    static final BigInteger ADDED_VALUE = new BigInteger("5");
    static final BigInteger VALUE_OF_X = new BigInteger("3");
    static final String FORMAL_PARAMETER = "x";

    private TestFunctionDefinitions() {        
    }

    public static FunctionDefinitionStatement createTestFunction() {
        FunctionDefinitionStatement instance = new FunctionDefinitionStatement(1, 1);
        instance.setKey(new FunctionKey("testFunction"));
        instance.setStatements(new StatementGroup(1, 1));
        TypeNode theType = new AtomicTypeNode(1, 1, CountlangType.integer());
        instance.addFormalParameter(FORMAL_PARAMETER, theType);
        instance.addStatement(getReturnStatement());
        instance.setReturnType(CountlangType.integer());
        return instance;
    }

    private static Statement getReturnStatement() {
        CompositeExpression ex1 = getStatementExpression();
        ValueReturnStatement result = new ValueReturnStatement(1, 1);
        result.setExpression(ex1);
        return result;
    }

    private static CompositeExpression getStatementExpression() {
        ValueExpression ex11 = new ValueExpression(1, 1, ADDED_VALUE, CountlangType.integer());
        SymbolExpression ex12 = new SymbolExpression(1, 1, FORMAL_PARAMETER);
        CompositeExpression ex1 = new CompositeExpression(1, 1);
        OperatorAdd operator = new OperatorAdd(1, 1);
        ex1.setOperator(operator);
        ex1.addSubExpression(ex11);
        ex1.addSubExpression(ex12);
        boolean typeCheckOk = operator.checkAndEstablishTypes(Arrays.asList(CountlangType.integer(), CountlangType.integer()));
        if(! typeCheckOk) {
            throw new IllegalStateException("Type check failed for TestFunction");
        }
        return ex1;
    }
}
