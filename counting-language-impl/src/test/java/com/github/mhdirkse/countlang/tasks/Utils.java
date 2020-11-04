package com.github.mhdirkse.countlang.tasks;

import java.io.IOException;
import java.io.StringReader;

import com.github.mhdirkse.countlang.execution.OutputStrategy;
import com.github.mhdirkse.countlang.steps.Stepper;

class Utils {
    static Stepper compileAndGetStepper(final String programText, OutputStrategy outputStrategy) {
        try {
            return compileAndGetStepperUnchecked(programText, outputStrategy);
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }        
    }

    private static Stepper compileAndGetStepperUnchecked(
            final String programText, OutputStrategy outputStrategy) throws IOException {
        StringReader reader = new StringReader(programText);
        try {
            return new ProgramExecutor(reader).getStepper(outputStrategy);
        }
        finally {
            reader.close();
        }
    }
}
