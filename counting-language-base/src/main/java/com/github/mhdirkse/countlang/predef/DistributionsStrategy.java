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

package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.ast.ProgramException;

interface DistributionsStrategy {
	void test(int line, int column, List<Object> values);

	static class Combination implements DistributionsStrategy {
		private final List<DistributionsStrategy> children;

		Combination(List<DistributionsStrategy> children) {
			this.children = children;
		}

		@Override
		public void test(int line, int column, List<Object> values) {
			children.forEach(c -> c.test(line, column, values));
		}
	}

	static abstract class AbstractArgSelector implements DistributionsStrategy {
		private final int[] appliesTo;

		AbstractArgSelector(int[] appliesTo) {
			this.appliesTo = appliesTo;
		}

		@Override
		public void test(int line, int column, List<Object> values) {
			for(int i = 0; i < appliesTo.length; ++i) {
				int argNumber = appliesTo[i];
				doTest(line, column, (Distribution) values.get(argNumber), argNumber);
			}
		}

		abstract void doTest(int line, int column, Distribution d, int argNumber);		
	}

	static class ThisNotEmpty extends AbstractArgSelector {
		ThisNotEmpty() {
			super(new int[] {0});
		}

		@Override
		void doTest(int line, int column, Distribution d, int argNumber) {
			if(d.getTotal().compareTo(BigInteger.ZERO) == 0) {
				throw new ProgramException(line, column, "Division by zero");
			}
		}
	}

	static class ThisNoUnknown extends AbstractArgSelector {
		private final String name;

		ThisNoUnknown(String name) {
			super(new int[] {0});
			this.name = name;
		}

		@Override
		void doTest(int line, int column, Distribution d, int argNum) {
			if(d.getCountUnknown().compareTo(BigInteger.ZERO) != 0) {
				throw new ProgramException(line, column,
						String.format("Cannot execute distribution<>.%s() on a distribution that has unknown", name));
			}
		}
	}

	static class OtherNoUnknown extends AbstractArgSelector {
		private final String name;

		OtherNoUnknown(String name) {
			super(new int[] {1});
			this.name = name;
		}

		@Override
		void doTest(int line, int column, Distribution d, int argNumber) {
			if(d.getCountUnknown().compareTo(BigInteger.ZERO) != 0) {
				throw new ProgramException(line, column,
						String.format("Cannot execute distribution<>.%s() with an argument that has unknown", name));
			}
		}
	}

	static class OtherIsSet extends AbstractArgSelector {
		private String name;

		OtherIsSet(String name) {
			super(new int[] {1});
			this.name = name;
		}

		@Override
		void doTest(int line, int column, Distribution d, int argNumber) {
			if(! d.isSet()) {
				throw new ProgramException(line, column, String.format("Cannot execute distribution<>.%s() with an argument that is not a set", name));
			}
		}
	}

	static DistributionsStrategy of(DistributionsStrategy first, DistributionsStrategy ...others) {
		List<DistributionsStrategy> children = new ArrayList<>();
		children.add(first);
		for(int i = 0; i < others.length; ++i) {
			children.add(others[i]);
		}
		return new Combination(children);
	}

	static DistributionsStrategy notEmpty() {
		return new ThisNotEmpty();
	}

	static DistributionsStrategy noUnknown(String memberName) {
		return new ThisNoUnknown(memberName);
	}

	static DistributionsStrategy otherNoUnknown(String memberName) {
		return new OtherNoUnknown(memberName);
	}

	static DistributionsStrategy otherIsSet(String memberName) {
		return new OtherIsSet(memberName);
	}
}
