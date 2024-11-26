package middle.IR.Instructions;

import middle.IR.Function;
import middle.IR.SlotTracker;
import middle.IR.Use;
import middle.IR.Value;

import java.util.ArrayList;

public class CallInst extends Instruction {
    Function function;
    ArrayList<Value> rParams;

    public CallInst(Function function, ArrayList<Value> rParams) {
        super(function.functionType.returnType);
        this.function = function;
        this.rParams = rParams;
        //记录对Value的使用
        for (Value param : rParams) {
            param.addUse(this);
            param.getContext().saveUse(new Use(param, this));
        }
        //保存该指令对应的Value
        function.getContext().saveValue(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        //有返回值
        if (!function.functionType.returnType.equals(function.functionType.returnType.context.getVoidTy())) {
            String virtualReg = "%" + SlotTracker.getInstance().getSlot(this);
            sb.append(virtualReg).append(" = call ").append(function.functionType.returnType);
        }
        else {
            sb.append("call ").append(function.functionType.returnType);
        }
        //函数名
        sb.append(" @").append(function.name);
        //实参
        sb.append("(");
        for (int i = 0; i < rParams.size(); i++) {
            sb.append(rParams.get(i).getUseStr());
            if (i != rParams.size() - 1) sb.append(", ");
        }
        sb.append(")");
        return sb.toString();
    }
}
