package com.github.mhdirkse.countlang.tasks;

import java.io.IOException;

public interface AbstractTask {
    void run(OutputContext outputContext) throws IOException;
}
