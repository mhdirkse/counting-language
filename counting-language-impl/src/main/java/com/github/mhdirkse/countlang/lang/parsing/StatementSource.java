package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.Statement;

interface StatementSource {
    Statement getStatement();
}
