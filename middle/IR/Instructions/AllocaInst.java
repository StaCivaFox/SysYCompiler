package middle.IR.Instructions;

import middle.IR.SlotTracker;
import middle.IR.Type.PointerType;
import middle.IR.Type.Type;

public class AllocaInst extends Instruction {
    //public Type dataType;       //pointer

    public AllocaInst(Type refType) {
        super(refType.context.getPointerType(refType));
        //this.dataType = refType.context.getPointerType(refType);
        refType.context.saveValue(this);
    }

    @Override
    public String toString() {
        String virtualReg = "%" + String.valueOf(SlotTracker.getInstance().getSlot(this));
        String inst = " = alloca " + ((PointerType) dataType).refType.toString();
        return virtualReg + inst;
    }
}
