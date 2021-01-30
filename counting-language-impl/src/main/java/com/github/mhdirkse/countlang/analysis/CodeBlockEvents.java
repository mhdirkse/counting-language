package com.github.mhdirkse.countlang.analysis;

import lombok.Getter;
import lombok.Setter;

final class CodeBlockEvents {
    private CodeBlockEvents() {
    }

    static class Return {
        private final @Getter int line;
        private final @Getter int column;
        private @Getter @Setter Statement after = null;

        Return(int line, int column) {
            this.line = line;
            this.column = column;
        }
    }

    static class Statement {
        private final @Getter int line;
        private final @Getter int column;

        Statement(int line, int column) {
            this.line = line;
            this.column = column;
        }
    }
}
