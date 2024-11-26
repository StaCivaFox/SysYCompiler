package middle.IR;

import middle.IR.Type.FunctionType;
import middle.IR.Type.Type;

import java.util.ArrayList;
import java.util.LinkedList;

public class Function extends Value {
    public FunctionType functionType;
    public ArrayList<Argument> fParams;
    public LinkedList<BasicBlock> basicBlocks;
    public Module parent;
    public boolean isDefine = true;

    public Function(Type returnType, String name, Module parent) {
        super(name, ValueType.FunctionVTy, parent.context.getFunctionType(returnType));
        this.functionType = parent.context.getFunctionType(returnType);
        this.fParams = new ArrayList<>();
        this.basicBlocks = new LinkedList<>();
        this.parent = parent;
        parent.context.saveValue(this);
    }

    public Function(Type returnType, String name, ArrayList<Argument> fParams, Module parent) {
        super(name, ValueType.FunctionVTy, null/*tmp*/);
        ArrayList<Type> fParamsTypes = new ArrayList<>();
        for (Value value : fParams) {
            fParamsTypes.add(value.dataType);
        }
        this.setDataType(parent.context.getFunctionType(returnType, fParamsTypes));
        this.functionType = parent.context.getFunctionType(returnType, fParamsTypes);
        this.fParams = fParams;
        this.basicBlocks = new LinkedList<>();
        this.parent = parent;
        parent.context.saveValue(this);
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        this.basicBlocks.add(basicBlock);
    }
    public void setNotDefine() {
        this.isDefine = false;
    }

    //declare不用slottrack
    private String declare() {
        StringBuilder sb = new StringBuilder();
        sb.append("declare ").append(functionType.returnType).append(" @").append(name);
        sb.append("(");
        for (int i = 0; i < functionType.paramTypes.size(); i++) {
            sb.append(functionType.paramTypes.get(i));
            if (i != functionType.paramTypes.size() - 1) sb.append(",");
        }
        sb.append(")");
        return sb.toString();
    }

    //define需要slottrack
    private String define() {
        SlotTracker.getInstance().trace(this);
        StringBuilder sb = new StringBuilder();
        sb.append("define dso_local ").append(functionType.returnType).append(" @").append(name);
        //形参
        sb.append("(");
        for (int i = 0; i < fParams.size(); i++) {
            sb.append(fParams.get(i).dataType).append(" ");
            sb.append("%").append(SlotTracker.getInstance().getSlot(fParams.get(i)));
            if (i != fParams.size() - 1) sb.append(",");
        }
        sb.append(") ");
        //函数体
        sb.append("{\n");
        for (BasicBlock basicBlock : basicBlocks) {
            sb.append(basicBlock);
        }
        sb.append("}\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        if (isDefine) return define();
        else return declare();
    }
}
