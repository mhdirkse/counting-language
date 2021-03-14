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

import com.github.mhdirkse.countlang.ast.Operator.OperatorAdd;

public final class TestFunctionDefinitions {

    static final int ADDED_VALUE = 5;
    static final int VALUE_OF_X = 3;
    static final Object VALUE_OF_X_AS_VALUE = Integer.valueOf(VALUE_OF_X);
    static final String FORMAL_PARAMETER = "x";

    private TestFunctionDefinitions() {        
    }

    public static FunctionDefinitionStatement createTestFunction() {
        return new FunctionCreatorValidFunction().createFunction();
    }

    static abstract class FunctionCreatorBase {

        FunctionDefinitionStatement instance = new FunctionDefinitionStatement(1, 1);

        FunctionDefinitionStatement createFunction() {
            instance.setName("testFunction");
            instance.setStatements(new StatementGroup(1, 1));
            handleParameter();
            instance.addStatement(getStatement());
            handleExtraStatement();
            handleReturnType();
            return instance;
        }

        abstract void handleParameter();

        void addTheParameter() {
            instance.addFormalParameter(FORMAL_PARAMETER, CountlangType.integer());
        }

        abstract Statement getStatement();

        Statement getReturnStatement() {
            CompositeExpression ex1 = getStatementExpression();
            ReturnStatement result = new ReturnStatement(1, 1);
            result.setExpression(ex1);
            return result;
        }

        Statement getPrintStatement() {
            CompositeExpression ex1 = getStatementExpression();
            PrintStatement result = new PrintStatement(1, 1);
            result.setExpression(ex1);
            return result;
        }

        CompositeExpression getStatementExpression() {
            ValueExpression ex11 = new ValueExpression(1, 1, ADDED_VALUE, CountlangType.integer());
            SymbolExpression ex12 = new SymbolExpression(1, 1, FORMAL_PARAMETER);
            CompositeExpression ex1 = new CompositeExpression(1, 1);
            ex1.setOperator(new OperatorAdd(1, 1));
            ex1.addSubExpression(ex11);
            ex1.addSubExpression(ex12);
            return ex1;
        }

        ExpressionNode getActualParameter() {
            ValueExpression result = new ValueExpression(1, 1, VALUE_OF_X_AS_VALUE, CountlangType.integer());
            return result;
        }

        String getFormalParameter() {
            return FORMAL_PARAMETER;
        }

        int getParameterValue() {
            return VALUE_OF_X;
        }

        int getExpectedResult() {
            return ADDED_VALUE + VALUE_OF_X;            
        }

        abstract void handleExtraStatement();

        void handleReturnType() {
        }
    }

    static class FunctionCreatorValidFunction extends FunctionCreatorBase {
        @Override
        void handleParameter() {
            addTheParameter();
        }

        @Override
        Statement getStatement() {
            return getReturnStatement();
        }

        @Override
        void handleExtraStatement() {
        }

        @Override
        void handleReturnType() {
            instance.setReturnType(CountlangType.integer());
        }
    }
}
