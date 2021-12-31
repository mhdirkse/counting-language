package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.type.CountlangType;
import com.github.mhdirkse.countlang.utils.Utils;

abstract class AbstractDistributionToArray extends AbstractMemberFunction {
    private static final BigInteger COUNT_THRESHOLD = new BigInteger("1000000");

    @SuppressWarnings("unchecked")
	AbstractDistributionToArray(String name) {
        super(name, CountlangType.distributionOfAny(), t -> CountlangType.arrayOf(t.getSubType()));
    }

    @Override
    public Object run(int line, int column, List<Object> args) {
        Distribution d = (Distribution) args.get(0);
        if(d.getCountUnknown().compareTo(BigInteger.ZERO) != 0) {
            throw new ProgramException(line, column, "Cannot sort a distribution that has unknown");
        }
        List<Comparable<Object>> arrayValues = new ArrayList<>();
        Iterator<Object> it = d.getItemIterator();
        while(it.hasNext()) {
            @SuppressWarnings("unchecked")
            Comparable<Object> item = (Comparable<Object>) it.next();
            BigInteger rawCount = d.getCountOf(item);
            if(rawCount.compareTo(COUNT_THRESHOLD) > 0) {
                throw new ProgramException(line, column, String.format("Distribution %s is too big to put in array, item %s has count %s",
                        Utils.genericFormat(d), Utils.genericFormat(item), Utils.genericFormat(rawCount)));
            }
            int count = (int) rawCount.longValue();
            IntStream.range(0, count).forEach(i -> arrayValues.add(item));
        }
        // Sorts ascending
        Collections.sort(arrayValues);
        afterSort(arrayValues);
        return arrayValues;
    }

    abstract void afterSort(List<Comparable<Object>> result);
}
