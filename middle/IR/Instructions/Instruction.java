package middle.IR.Instructions;

import middle.IR.BasicBlock;
import middle.IR.SlotTracker;
import middle.IR.Type.Type;
import middle.IR.User;

public class Instruction extends User {

    //设为protected，防止直接创建Instruction对象
    protected Instruction(Type dataType) {
        super(ValueType.InstructionVTy, dataType);
    }

    public boolean isVoidTy() {
        return this.dataType.typeID.equals(Type.TypeID.VoidTy);
    }

}
