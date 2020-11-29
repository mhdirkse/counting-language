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

package com.github.mhdirkse.countlang.tasks;

final class Constants {
    private Constants() {
    }

    static String INCREMENT_OF_MAX_INT_MINUS_ONE = String.format("print %d + %d", Integer.MAX_VALUE - 1, 1);
    static String MAX_INT = String.format("%d", Integer.MAX_VALUE);
    static String INCREMENT_OF_MAX_INT = String.format("print %d + %d", Integer.MAX_VALUE, 1);
    static String DECREMENT_OF_MIN_INT_PLUS_ONE = String.format("print %d - %d", Integer.MIN_VALUE + 1, 1);
    static String MIN_INT = String.format("%d", Integer.MIN_VALUE);
    static String DECREMENT_OF_MIN_INT = String.format("print %d - %d", Integer.MIN_VALUE, 1);
}
