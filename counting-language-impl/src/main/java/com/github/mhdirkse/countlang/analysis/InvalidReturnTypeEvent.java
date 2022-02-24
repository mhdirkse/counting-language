package com.github.mhdirkse.countlang.analysis;

class InvalidReturnTypeEvent {
	int line;
	int column;

	InvalidReturnTypeEvent(int line, int column) {
		this.line = line;
		this.column = column;
	}
}
