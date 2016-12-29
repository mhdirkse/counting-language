package com.github.mhdirkse.countlang.compiler;

import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.Operator;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.Program;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.engine.Engine;
import com.github.mhdirkse.countlang.engine.Instruction;
import com.github.mhdirkse.countlang.engine.OperatorInstructions;

/**
 * Also writes constant values in memory.
 *
 * @author martijndirkse
 *
 */
final class InstructionProducer implements AstNode.Visitor {
    private final Program program;
    private final Engine engine;
    private final MemoryMapper memoryMapper;

    InstructionProducer(
            final Program program,
            final Engine engine,
            final MemoryMapper memoryMapper)
    {
        this.program = program;
        this.engine = engine;
        this.memoryMapper = memoryMapper;
    }

    public void run() {
        program.accept(this);
    }

    @Override
    public void visitProgram(final Program program) {
        for(int i = 0; i < program.getSize(); ++i) {
            program.getStatement(i).accept(this);
        }
    }

    @Override
    public void visitAssignmentStatement(final AssignmentStatement statement) {
        statement.getRhs().accept(this);
        int position = memoryMapper.getVariablePosition(
                statement.getLhs().getSeq());
        Instruction instruction = engine.getWriteInstructionToBottom(position);
        engine.addInstruction(instruction);
    }

    @Override
    public void visitPrintStatement(final PrintStatement statement) {
        statement.getExpression().accept(this);
        Instruction instruction = engine.getPrintIntInstruction();
        engine.addInstruction(instruction);
    }

    @Override
    public void visitCompositeExpression(final CompositeExpression expression) {
        for(int i = 0; i < expression.getNumSubExpressions(); ++i) {
            expression.getSubExpression(i).accept(this);
        }
        Instruction instruction = getOperatorInstruction(
                expression.getOperator());
        engine.addInstruction(instruction);
    }

    Instruction getOperatorInstruction(Operator operator) {
        String name = operator.getName();
        if(name.equals("+")) {
            return OperatorInstructions.getAddInt();
        } else if(name.equals("-")) {
            return OperatorInstructions.getSubtractInt();
        } else if(name.equals("*")) {
            return OperatorInstructions.getMultiplyInt();
        } else if(name.equals("/")) {
            return OperatorInstructions.getDivideInt();
        } else {
            throw new IllegalArgumentException("Unknown operator " + name);
        }
    }

    @Override
    public void visitSymbolExpression(final SymbolExpression expression) {
        int seq = expression.getSymbol().getSeq();
        int position = memoryMapper.getVariablePosition(seq);
        Instruction instruction = engine.getReadInstructionFromBottom(position);
        engine.addInstruction(instruction);
    }

    @Override
    public void visitValueExpression(final ValueExpression expression) {
        int seq = expression.getValue().getSeq();
        int position = memoryMapper.getValuePosition(seq);
        engine.write(position, expression.getValue().getValue());
        Instruction instruction = engine.getReadInstructionFromBottom(position);
        engine.addInstruction(instruction);
    }
}
