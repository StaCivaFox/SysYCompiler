package middle.IR.Instructions;

import middle.IR.Op;
import middle.IR.SlotTracker;
import middle.IR.Use;
import middle.IR.Value;

public class IcmpInst extends Instruction {
    public Op op;
    public Value value1;
    public Value value2;

    public IcmpInst(Op op, Value value1, Value value2) {
        //返回true或者false，i1类型
        super(value1.getContext().getInt1Ty());
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
        String inst = " = icmp " + this.op + " " + value1.dataType + " " + value1.getVirtualReg() + ", " + value2.getVirtualReg();
        return virtualReg + inst;
    }
}
