package middle.IR.Instructions;

import middle.IR.BasicBlock;
import middle.IR.Use;
import middle.IR.Value;

public class BranchInst extends Instruction {
    public Value cond;
    public Value labelTrue;
    public Value labelFalse;

    public BranchInst(BasicBlock dest) {
        super(dest.getContext().getVoidTy());
        this.labelTrue = dest;
        //保存该指令对应的value
        dest.getContext().saveValue(this);
    }

    public BranchInst(Value cond, BasicBlock labelTrue, BasicBlock labelFalse) {
        super(cond.getContext().getVoidTy());
        this.cond = cond;
        this.labelTrue = labelTrue;
        this.labelFalse = labelFalse;
        //记录对value的使用
        cond.addUse(this);
        cond.getContext().saveUse(new Use(cond, this));
        //保存该指令对应的value
        cond.getContext().saveValue(this);
    }

    @Override
    public String toString() {
        BasicBlock b1 = (BasicBlock) labelTrue;
        if (cond != null) {
            BasicBlock b2 = (BasicBlock) labelFalse;
            return "br " + cond.getUseStr() + ", " + b1.getUseStr() + ", " + b2.getUseStr();
        }
        else {
            return "br " + b1.getUseStr();
        }
    }
}
