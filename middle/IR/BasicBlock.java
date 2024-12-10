package middle.IR;

import middle.IR.Instructions.Instruction;

import java.util.LinkedList;

public class BasicBlock extends Value {
    public LinkedList<Instruction> instructions;
    public Function parent;
    public boolean isTerminated;

    public BasicBlock(Function parent) {
        super(ValueType.BasicBlockVTy, parent.getContext().getLabelTy());
        this.instructions = new LinkedList<>();
        this.parent = parent;
        this.isTerminated = false;
    }

    public void addInstruction(Instruction instruction) {
        if (!isTerminated)
            this.instructions.add(instruction);
    }

    public void setTerminator(Instruction instruction) {
        if (!isTerminated) {
            this.instructions.add(instruction);
            isTerminated = true;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        //第一个基本块不打印标号，是函数体本身
        if (!this.equals(parent.basicBlocks.getFirst())) {
            sb.append(SlotTracker.getInstance().getSlot(this)).append(":\n");
        }
        for (Instruction instruction : instructions) {
            sb.append("    ").append(instruction).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String getUseStr() {
        StringBuilder sb = new StringBuilder();
        sb.append("label ");
        String virtualReg = "%" + SlotTracker.getInstance().getSlot(this);
        sb.append(virtualReg);
        return sb.toString();
    }
}
