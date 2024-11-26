package middle.IR.Instructions;

import middle.IR.Op;
import middle.IR.SlotTracker;
import middle.IR.Use;
import middle.IR.Value;

public class BinaryInst extends Instruction {
    public Op op;
    public Value value1;
    public Value value2;

    public BinaryInst(Op op, Value value1, Value value2) {
        //进行运算的两个value的Type必须相同，BinaryInstruction的返回类型也与之相同
        super(value1.dataType);
        this.op = op;
        this.value1 = value1;
        this.value2 = value2;
        //记录使用的Value
        value1.addUse(this);
        value1.getContext().saveUse(new Use(value1, this));
        value2.addUse(this);
        value2.getContext().saveUse(new Use(value2, this));
        //保存该指令对应的Value
        value1.getContext().saveValue(this);
    }

    @Override
    public String toString() {
        String virtualReg = "%" + SlotTracker.getInstance().getSlot(this);
        String inst = " = " + this.op + " " + value1.dataType + " " + value1.getVirtualReg() + ", " + value2.getVirtualReg();
        return virtualReg + inst;
    }
}
