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

package com.github.mhdirkse.countlang.algorithm;

import lombok.Getter;

class PossibilitiesWalkerException extends Exception {
    private static final long serialVersionUID = 3546479381302208644L;

    PossibilitiesWalkerException(String msg) {
        super(msg);
    }

    static class NewDistributionDoesNotFitParentCount extends PossibilitiesWalkerException {
        private static final long serialVersionUID = 1387301468038442369L;
        private final @Getter int childCount;
        private final @Getter int parentCount;

        NewDistributionDoesNotFitParentCount(int childCount, int parentCount) {
            super(String.format("Added distribution's count %d does not fit in parent count %d", childCount, parentCount));
            this.childCount = childCount;
            this.parentCount = parentCount;
        }
    }
}
