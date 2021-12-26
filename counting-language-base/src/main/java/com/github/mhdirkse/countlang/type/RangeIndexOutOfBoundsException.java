package com.github.mhdirkse.countlang.type;

import java.math.BigInteger;

public class RangeIndexOutOfBoundsException extends Exception {
	private static final long serialVersionUID = 5242606535546644655L;

	private BigInteger offendingIndex;

	public RangeIndexOutOfBoundsException(BigInteger offendingIndex) {
		super(String.format("Index out of bounds: %s", offendingIndex.toString()));
		this.offendingIndex = offendingIndex;
	}

	public BigInteger getOffendingIndex() {
		return offendingIndex;
	}
}
