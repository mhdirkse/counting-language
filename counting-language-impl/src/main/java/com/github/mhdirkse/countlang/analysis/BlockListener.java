/*
 * Copyright Martijn Dirkse 2021
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

package com.github.mhdirkse.countlang.analysis;

import java.util.Iterator;

interface BlockListener {
    default void startSwitch(CodeBlock codeBlock) {
        getChildren().forEachRemaining(c -> c.startSwitch(codeBlock));
    }
    
    default void stopSwitch(CodeBlock codeBlock) {
        getChildren().forEachRemaining(c -> c.stopSwitch(codeBlock));
    }
    
    default void startBranch(CodeBlock codeBlock) {
        getChildren().forEachRemaining(c -> c.startBranch(codeBlock));
    }
    
    default void stopBranch(CodeBlock codeBlock) {
        getChildren().forEachRemaining(c -> c.stopBranch(codeBlock));
    }

    default void startRepetition(CodeBlock codeBlock) {
        getChildren().forEachRemaining(c -> c.startRepetition(codeBlock));
    }

    default void stopRepetition(CodeBlock codeBlock) {
        getChildren().forEachRemaining(c -> c.stopRepetition(codeBlock));
    }

    Iterator<? extends BlockListener> getChildren();
}
