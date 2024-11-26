package middle.IR.Instructions;

import middle.IR.SlotTracker;
import middle.IR.Type.Type;
import middle.IR.Use;
import middle.IR.Value;

public class ZextInst extends Instruction {
    public Value value;
    public Type destType;

    public ZextInst(Value value, Type destType) {
        super(destType);
        this.value = value;
        this.destType = destType;
        //记录对Value的使用
        value.addUse(this);
        value.getContext().saveUse(new Use(value, this));
        //保存该指令对应的Value
        value.getContext().saveValue(this);
    }

    @Override
    public String toString() {
        String virtualReg = "%" + SlotTracker.getInstance().getSlot(this);
        String inst = " = zext " + value.getUseStr() + " to " + destType;
        return virtualReg + inst;
    }
}
