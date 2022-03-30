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

package com.github.mhdirkse.countlang.algorithm;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import com.github.mhdirkse.countlang.type.CountlangArray;

class SequenceLayback implements Samplable {
	private Distribution source;
	private BigInteger numSampled;
	private BigInteger total = BigInteger.ONE;
	private BigInteger unknown;

	SequenceLayback(Distribution source, BigInteger numSampled) {
		this.source = source;
		this.numSampled = numSampled;
		BigInteger known = BigInteger.ONE;
		for(BigInteger i = BigInteger.ZERO; i.compareTo(numSampled) < 0; i = i.add(BigInteger.ONE)) {
			total = total.multiply(source.getTotal());
			known = known.multiply(source.getTotal().subtract(source.getCountUnknown()));
		}
		unknown = total.subtract(known);
	}

	@Override
	public BigInteger getTotal() {
		return total;
	}

	@Override
	public Iterator<Object> getItemIterator() {
		return new TheIterator(source, numSampled);
	}

	@Override
	public BigInteger getCountUnknown() {
		return unknown;
	}

	@Override
	public BigInteger getCountOf(ProbabilityTreeValue value) {
		if(value.isUnknown()) {
			return getCountUnknown();
		}
		CountlangArray normalValue = (CountlangArray) value.getValue();
		BigInteger count = BigInteger.ONE;
		for(int i = 0; i < normalValue.size(); ++i) {
			count = count.multiply(source.getCountOf(normalValue.get(i)));
		}
		return count;
	}

	static class TheIterator implements Iterator<Object> {
		private int indexes[];
		private Object[] choices;
		private boolean done = false;

		TheIterator(Distribution source, BigInteger bigNumSampled) {
			if((bigNumSampled.compareTo(BigInteger.ONE) < 0) || (bigNumSampled.compareTo(MAX_NUM_SAMPLED) > 0)) {
				throw new IllegalArgumentException("Sampling too many values from a distribution");
			}
			int numSampled = bigNumSampled.intValue();
			indexes = new int[numSampled];
			IntStream.range(0, numSampled).forEach(i -> indexes[i] = 0);
			getChoices(source);
			if(choices.length == 0) {
				done = true;
			}
		}

		private void getChoices(Distribution source) {
			Iterator<Object> sourceIterator = source.getItemIterator();
			choices = new Object[source.getNumChoices()];
			sourceIterator = source.getItemIterator();
			int choiceIndex = 0;
			while(sourceIterator.hasNext()) {
				choices[choiceIndex++] = sourceIterator.next();
			}
		}

		@Override
		public boolean hasNext() {
			return ! done;
		}

		@Override
		public Object next() {
			if(done) {
				throw new IllegalArgumentException("No next value available");
			}
			CountlangArray result = assembleResult();
			goToNext();
			return result;
		}

		private CountlangArray assembleResult() {
			List<Object> subValues = new ArrayList<>(indexes.length);
			for(int i = 0; i < indexes.length; ++i) {
				subValues.add(choices[indexes[i]]);
			}
			CountlangArray result = new CountlangArray(subValues);
			return result;
		}

		private void goToNext() {
			int maxChoice = choices.length - 1;
			done = true;
			for(int i = (indexes.length-1); i >= 0; --i) {
				if(indexes[i] < maxChoice) {
					++(indexes[i]);
					IntStream.range(i+1, indexes.length).forEach(j -> indexes[j] = 0);
					done = false;
					break;
				}
			}
		}
	}
}
