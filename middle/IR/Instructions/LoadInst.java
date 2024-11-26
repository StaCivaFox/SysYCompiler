package middle.IR.Instructions;

import middle.IR.SlotTracker;
import middle.IR.Type.PointerType;
import middle.IR.Type.Type;
import middle.IR.Use;
import middle.IR.Value;

//把一个Value指针地址中的值，以Type类型加载到一个虚拟寄存器中，
public class LoadInst extends Instruction {
    //public Type dataType;
    public Value address;

    public LoadInst(Value address) {
        //load指令的返回值类型是address指针的refType
        super(((PointerType) address.dataType).refType);
        this.address = address;
        //this.dataType = dataType;
        //记录LoadInst对address对应的指令（通常是AllocaInst）的使用
        address.addUse(this);
        dataType.context.saveUse(new Use(address, this));

        dataType.context.saveValue(this);
    }

    @Override
    public String toString() {
        String virtualReg = "%" + SlotTracker.getInstance().getSlot(this);
        String inst = " = load " + this.dataType + ", " + address.getUseStr();
        return virtualReg + inst;
    }
}
