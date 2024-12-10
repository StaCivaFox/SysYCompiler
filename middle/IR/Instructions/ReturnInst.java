package middle.IR.Instructions;

import middle.IR.Type.Type;
import middle.IR.Use;
import middle.IR.Value;

public class ReturnInst extends Instruction {
    //public Type retType;
    public Value retValue;

    //ret void
    public ReturnInst(Type retType) {
        super(retType.context.getVoidTy());
        //this.retType = retType;
        //保存该指令对应的Value
        retType.context.saveValue(this);
    }

    //ret i32 0 ret i8 0
    public ReturnInst(Type retType, Value retValue) {
        super(retType.context.getVoidTy());
        //this.retType = retType;
        this.retValue = retValue;
        //记录对Value的使用
        retValue.addUse(this);
        retType.context.saveUse(new Use(retValue, this));
        //保存该指令对应的Value
        retType.context.saveValue(this);
    }

    @Override
    public String toString() {
        if (this.retValue == null)
            return "ret void";
        else
            return "ret " + retValue.getUseStr();
    }
}
