package com.github.mhdirkse.countlang.compiler;

import com.github.mhdirkse.countlang.ast.Program;
import com.github.mhdirkse.countlang.engine.Engine;
import com.github.mhdirkse.countlang.engine.OutputReceiver;

public final class Compiler implements MemoryMapper {
    private OutputReceiver outputReceiver = null;
    private Program ast = null;
    private int numValues = 0;
    private int numVariables = 0;
    private Engine engine = null;

    public Compiler() {
    }

    public Compiler(final OutputReceiver outputReceiver) {
        this.outputReceiver = outputReceiver;
    }

    public Engine compile(Program ast) {
        this.ast = ast;
        doMemoryAnalysis();
        createEngine();
        new InstructionProducer(ast, engine, this).run();
        return engine;
    }

    private void doMemoryAnalysis() {
        MemoryAnalyzer memoryRequirementsAnalyzer =
                new MemoryAnalyzer();
        memoryRequirementsAnalyzer.run(ast);
        numValues = memoryRequirementsAnalyzer.getNumValues();
        numVariables = memoryRequirementsAnalyzer.getNumVariables();
    }

    private void createEngine() {
        int size = numValues + numVariables;
        if(outputReceiver == null) {
            engine = new Engine(size);
        } else {
            engine = new Engine(size, outputReceiver);
        }
    }

    @Override
    public int getValuePosition(int valueSeq) {
        return valueSeq;
    }

    @Override
    public int getVariablePosition(int variableSeq) {
        return numValues = variableSeq;
    }
}
