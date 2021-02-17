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

import java.util.Comparator;

/**
 * The order of the values is significant. If in a serial block, every child
 * has status NO_RETURN, then the block's status is NO_RETURN. If any child's
 * status is SOME_RETURN while the other children have NONE_RETURN, then the block's
 * status is SOME_RETURN. The same applies to the other values.
 * @author martijn
 *
 */
enum ReturnStatus implements Comparable<ReturnStatus> {
    NONE_RETURN,
    SOME_RETURN,
    WEAK_ALL_RETURN,
    STRONG_ALL_RETURN;

    static final Comparator<ReturnStatus> COMPARATOR = new Comparator<ReturnStatus>() {
        @Override
        public int compare(final ReturnStatus r1, final ReturnStatus r2) {
            return r1.compareTo(r2);
        }
    };
}
