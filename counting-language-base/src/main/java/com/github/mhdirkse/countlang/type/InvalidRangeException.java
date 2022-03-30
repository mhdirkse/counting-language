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

package com.github.mhdirkse.countlang.type;

public class InvalidRangeException extends Exception {
	private static final long serialVersionUID = -4259817560936152090L;

	private String invalidRangeString;

	public InvalidRangeException(String invalidRangestring) {
		super(String.format("Invalid range %s", invalidRangestring));
		this.invalidRangeString = invalidRangestring;
	}

	public String getInvalidRangeString() {
		return invalidRangeString;
	}
}
