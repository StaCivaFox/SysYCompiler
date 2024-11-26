package middle.IR.Instructions;

import middle.IR.Use;
import middle.IR.Value;

//读出一个Value的值，将它保存在Value指针地址对应的位置
public class StoreInst extends Instruction {
    Value value;
    Value address;

    public StoreInst(Value value, Value address) {
        super(value.getContext().getVoidTy());
        this.value = value;
        this.address = address;
        //记录对Value的使用
        value.addUse(this);
        address.addUse(this);
        value.getContext().saveUse(new Use(value, this));
        value.getContext().saveUse(new Use(address, this));
        //保存该指令对应的Value
        value.getContext().saveValue(this);
    }

    @Override
    public String toString() {
        return "store " + value.getUseStr() + ", " + address.getUseStr();
    }
}
