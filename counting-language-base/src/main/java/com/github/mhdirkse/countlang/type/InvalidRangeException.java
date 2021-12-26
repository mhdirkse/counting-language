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
