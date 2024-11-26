package middle.IR;

import middle.IR.Type.Type;

import java.util.ArrayList;

public class Value {
    //语法结构类型
    public enum ValueType {
        ConstantVTy,
        FunctionVTy,
        ArgumentVTy,
        GlobalVariableVTy,
        BasicBlockVTy,
        InstructionVTy

    }
    public String name;
    public ValueType valueType;
    public Type dataType;       //记录一条指令的返回值类型
    public ArrayList<Use> useList;

    public Value(String name, ValueType valueType, Type dataType) {
        this.useList = new ArrayList<>();
        this.name = name;
        this.valueType = valueType;
        this.dataType = dataType;
    }

    public Value(ValueType valueType, Type dataType) {
        this.useList = new ArrayList<>();
        this.valueType = valueType;
        this.dataType = dataType;
    }

    public Context getContext() {
        return this.dataType.context;
    }

    public void setDataType(Type dataType) {
        this.dataType = dataType;
    }

    public void addUse(User user) {
        this.useList.add(new Use(this, user));
    }

    //获得该value被使用时的形式
    public String getUseStr() {
        StringBuilder sb = new StringBuilder();
        sb.append(dataType).append(" ");
        String virtualReg = "%" + SlotTracker.getInstance().getSlot(this);
        sb.append(virtualReg);
        return sb.toString();
    }

    //获得该value的虚拟寄存器编号
    public String getVirtualReg() {
        return "%" + SlotTracker.getInstance().getSlot(this);
    }
}
