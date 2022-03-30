/*
 * Copyright Martijn Dirkse 2022
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

public interface Call extends CompositeNode {
	// Every Call is also an AstNode. Therefore, getLine() and getColumn() are implemented
	// in every implementation even though these implementations do not themselves have
	// the code.
	int getLine();
	int getColumn();
	String getName();
	void setName(String name);
	FunctionKey getKey();
	int getNumArguments();
	ExpressionNode getArgument(int index);
	void addArgument(ExpressionNode expr);
}
