package com.github.mhdirkse.countlang.format;

public interface Format {
	public static final Format EXACT = new Exact();
	public static final Format APPROXIMATE = new Approximate();

	String format(Object value);
	String formatOnOneLine(Object value);
}
