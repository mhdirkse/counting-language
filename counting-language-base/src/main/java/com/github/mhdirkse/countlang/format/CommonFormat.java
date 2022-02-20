package com.github.mhdirkse.countlang.format;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.fraction.BigFraction;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.algorithm.DistributionCompareHelper;
import com.github.mhdirkse.countlang.algorithm.ProbabilityTreeValue;
import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangTuple;

abstract class CommonFormat implements Format {
    private static final BigInteger MAX_DISTRIBUTION_ITEMS_ON_A_LINE = new BigInteger("10");

	@Override
	public String format(Object value) {
        if(value instanceof Distribution) {
            return formatDistributionAsTable((Distribution) value);
        } else {
        	return formatForDistributionTable(value);
        }
	}

    private String formatForDistributionTable(Object item) {
        if(item instanceof CountlangTuple) {
            return formatTupleForDistributionTable((CountlangTuple) item);
        } else {
            return formatOnOneLine(item);
        }
    }

	@Override
	public String formatOnOneLine(Object value) {
		if(value instanceof Distribution) {
			return formatDistributionOnOneLine((Distribution) value);
		} else if(value instanceof CountlangArray) {
			return formatArray((CountlangArray) value);
		} else if(value instanceof CountlangTuple) {
            return formatTuple((CountlangTuple) value);
        } else if(value instanceof BigFraction) {
            return formatBigFraction((BigFraction) value);
        } else {
            return value.toString();
        }
	}

	private String formatDistributionAsTable(Distribution d) {
        if(d.getTotal().equals(BigInteger.ZERO)) {
            return "empty";
        }
        List<List<String>> table = createTable(d);
        final List<Integer> columnWidths = getTableColumnWidths(table);
        List<String> result = table.stream()
                .map(row -> formatTableRow(row, columnWidths))
                .collect(Collectors.toList());
        result.add(table.size()-1, getSeparator(columnWidths));
        return String.join("\n", result);
	}

    private List<List<String>> createTable(Distribution d) {
        List<List<String>> table = new ArrayList<>();
        for(Object item: d.getItems()) {
            String strItem = formatForDistributionTable(item);
            String strCount = formatCountInDistributionTable(item, d);
            table.add(Arrays.asList(strItem, strCount));
        }
        if(d.getCountUnknown().compareTo(BigInteger.ZERO) > 0) {
            table.add(Arrays.asList("unknown", formatCountUnknownInDistributionTable(d)));
        }
        table.add(Arrays.<String>asList("total", formatTotalInDistributionTable(d)));
        return table;
    }

    private List<Integer> getTableColumnWidths(List<List<String>> table) {
        List<Integer> columnWidths = new ArrayList<>();
        for(int column = 0; column < 2; column++) {
            final int theColumn = column;
            int width = table.stream()
                    .map(row -> row.get(theColumn).length())
                    .max((i1, i2) -> i1.compareTo(i2)).get();
            columnWidths.add(width);
        }
        return columnWidths;
    }

    private String getSeparator(final List<Integer> columnWidths) {
        int totalWidth = columnWidths.stream().reduce(0, (i1, i2) -> i1 + i2) + 2;
        String separator = StringUtils.repeat('-', totalWidth);
        return separator;
    }

    private String formatTableRow(List<String> row, List<Integer> columnWidths) {
        String value = StringUtils.leftPad(row.get(0), columnWidths.get(0), " ");
        String count = StringUtils.leftPad(row.get(1), columnWidths.get(1), " ");
        return value + "  " + count;
    }

    private String formatDistributionOnOneLine(Distribution d) {
        List<String> values = new ArrayList<>();
        DistributionCompareHelper helper = new DistributionCompareHelper(d);
        BigInteger maxToAdd = MAX_DISTRIBUTION_ITEMS_ON_A_LINE;
        while( (! maxToAdd.equals(BigInteger.ZERO) ) && (! helper.isDone())) {
            String value = formatProbabilityTreeValueForDistributionTable(helper.getCurrent());
            BigInteger countBig = helper.getCountToNext().min(maxToAdd);
            int count = countBig.intValue();
            IntStream.range(0, count).forEach(i -> values.add(value));
            helper.advance(countBig);
            maxToAdd = maxToAdd.subtract(countBig);
        }
        if(! helper.isDone()) {
            values.add("...");
        }
        return "(" + values.stream().collect(Collectors.joining(", ")) + ")";
    }

    private String formatProbabilityTreeValueForDistributionTable(ProbabilityTreeValue value) {
    	if(value.isUnknown()) {
    		return "unknown";
    	} else {
    		return formatOnOneLine(value.getValue());
    	}
    }

    private String formatArray(CountlangArray value) {
		return "[" + formatMembersOnOneLine(value.getAll()) + "]";
    }

    private String formatTuple(CountlangTuple value) {
    	return "[" + formatMembersOnOneLine(value.getAll()) + "]";
    }

    private String formatTupleForDistributionTable(CountlangTuple value) {
    	return formatMembersOnOneLine(value.getAll());
    }

	private String formatMembersOnOneLine(List<Object> members) {
		return members.stream().map(this::formatOnOneLine).collect(Collectors.joining(", "));
	}

    abstract String formatCountInDistributionTable(Object item, Distribution d);
    abstract String formatCountUnknownInDistributionTable(Distribution d);
    abstract String formatTotalInDistributionTable(Distribution d);
    abstract String formatBigFraction(BigFraction b);
}
