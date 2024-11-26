package middle.IR;

import middle.IR.Type.ArrayType;
import middle.IR.Type.IntegerType;
import middle.IR.Type.PointerType;
import middle.IR.Type.Type;

import java.util.ArrayList;

public class GlobalVariable extends Value {
    //字符串字面量是全局的数组类型
    public ArrayList<Value> init;
    public boolean isPrivate;
    public boolean isConst;

    //通过传入的Type dataType区分数组还是普通变量
    public GlobalVariable(String name, Type dataType, ArrayList<Value> init,
                          boolean isPrivate, boolean isConst) {
        super(name, ValueType.GlobalVariableVTy, dataType);
        this.init = init;
        this.isPrivate = isPrivate;
        this.isConst = isConst;
        dataType.context.saveValue(this);
    }

    //常量一定有初值，变量不一定
    //GlobleVariable的dataType是一个指向变量类型的指针
    //TODO:字符字面量数组的初始化？
    @Override
    public String toString() {
        //全局常量
        if (isConst) {
            Type refType = ((PointerType) dataType).refType;
            String tmp = String.format("@%s = dso_local constant %s", name, refType);
            StringBuilder sb = new StringBuilder(tmp);
            //生成初值；常量一定有初值
            if (refType instanceof IntegerType) {
                sb.append(" ").append(init.get(0));
            }
            //数组
            else {
                if (((Constant) init.get(0)).isStringConst()) {
                    sb.append(" ").append("c").append(init.get(0)).append(", ").append("align 1");
                }
                else {
                    sb.append(" ").append("[");
                    for (int i = 0; i < init.size() - 1; i++) {
                        sb.append(String.format("%s %s", ((ArrayType) refType).elementType, init.get(i)));
                        sb.append(", ");
                    }
                    sb.append(String.format("%s %s", ((ArrayType) refType).elementType, init.get(init.size() - 1)));
                    sb.append("]");
                }
            }
            return sb.toString();
        }
        //全局**变量**
        else {
            Type refType = ((PointerType) dataType).refType;
            String tmp = String.format("@%s = dso_local global %s", name, refType);
            StringBuilder sb = new StringBuilder(tmp);
            //生成初值
            if (init.isEmpty()) {
                sb.append(" zeroinitializer");
            }
            else {
                if (refType instanceof IntegerType) {
                    sb.append(" ").append(init.get(0));
                }
                //数组
                else {
                    if (((Constant) init.get(0)).isStringConst()) {
                        sb.append(" ").append("c").append(init.get(0)).append(", ").append("align 1");
                    }
                    else {
                        sb.append(" ").append("[");
                        for (int i = 0; i < init.size() - 1; i++) {
                            sb.append(String.format("%s %s", ((ArrayType) refType).elementType, init.get(i)));
                            sb.append(", ");
                        }
                        sb.append(String.format("%s %s", ((ArrayType) refType).elementType, init.get(init.size() - 1)));
                        sb.append("]");
                    }
                }
            }
            return sb.toString();
        }
    }

    @Override
    public String getUseStr() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.dataType).append(" ");
        String name = "@" + this.name;
        sb.append(name);
        return sb.toString();
    }
}
