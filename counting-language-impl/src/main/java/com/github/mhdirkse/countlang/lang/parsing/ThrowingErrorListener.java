package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import com.github.mhdirkse.countlang.utils.Utils;

class ThrowingErrorListener extends BaseErrorListener {
	static final ThrowingErrorListener INSTANCE = new ThrowingErrorListener();

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException e) throws ParseCancellationException {
		throw new ParseCancellationException(Utils.formatLineColumnMessage(line, charPositionInLine, msg));
	}
}
