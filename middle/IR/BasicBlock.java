package middle.IR;

import middle.IR.Instructions.Instruction;

import java.util.LinkedList;

public class BasicBlock extends Value {
    public LinkedList<Instruction> instructions;
    public Function parent;

    public BasicBlock(Function parent) {
        super(ValueType.BasicBlockVTy, parent.getContext().getLabelTy());
        this.instructions = new LinkedList<>();
        this.parent = parent;
    }

    public void addInstruction(Instruction instruction) {
        this.instructions.add(instruction);
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
}
