package com.github.mhdirkse.countlang.analysis;

import lombok.Getter;

class OverwrittenWrite {
    private @Getter AccessEvent firstWrite;
    private @Getter int lineOverwritten;
    private @Getter int columnOverwritten;
    private @Getter boolean byDefault;

    OverwrittenWrite(AccessEvent firstWrite, int lineOverwritten, int columnOverwritten, boolean byDefault) {
        this.firstWrite = firstWrite;
        this.lineOverwritten = lineOverwritten;
        this.columnOverwritten = columnOverwritten;
        this.byDefault = byDefault;
    }
}
