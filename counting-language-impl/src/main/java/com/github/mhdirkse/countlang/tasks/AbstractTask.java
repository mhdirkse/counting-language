package com.github.mhdirkse.countlang.tasks;

import java.io.IOException;

import com.github.mhdirkse.countlang.execution.OutputStrategy;

public interface AbstractTask {
    void run(OutputStrategy outputStrategy) throws IOException;
}
