package middle.IR.Instructions;

import middle.IR.Constant;
import middle.IR.SlotTracker;
import middle.IR.Type.ArrayType;
import middle.IR.Type.IntegerType;
import middle.IR.Type.PointerType;
import middle.IR.Type.Type;
import middle.IR.Use;
import middle.IR.Value;

//获取一个地址，返回类型是指针类型，其referenced type为数组元素的类型
//第一个<ty>是基地址索引指向的类型，即基地址指针的referenced Type
public class GetElementPtr extends Instruction {
    public Value baseAddr;
    public Value selfOffset;    //一般来说都是0
    public Value offset;        //索引数组元素时，该数组元素相对基地址的偏移量
                                //c语言中只有一维数组，通过指针的referenced type构建多维数组。当然实验中只考虑普遍意义上的一维数组。


    public GetElementPtr(Value baseAddr, Value offset) {
        //super(baseAddr.getContext().getPointerType(((ArrayType) (baseAddr.dataType)).elementType));
        super(null/*tmp*/);
        //获取baseAddr指针指向的refType
        Type baseAddrRefType = ((PointerType) baseAddr.dataType).refType;
        //如果指向一个数组，则getElementPtr返回指向“该数组元素类型”的指针类型
        if (baseAddrRefType instanceof ArrayType) {
            this.dataType = baseAddr.getContext().getPointerType(((ArrayType) baseAddrRefType).elementType);
        }
        //否则，则getElementPtr返回该指针类型
        else {
            this.dataType = baseAddr.dataType;
        }
        this.baseAddr = baseAddr;
        this.selfOffset = new Constant(baseAddr.getContext().getInt32Ty(), "0");
        this.offset = offset;
        //记录对Value的使用
        baseAddr.addUse(this);
        baseAddr.getContext().saveUse(new Use(baseAddr, this));
        offset.addUse(this);
        offset.getContext().saveUse(new Use(offset, this));
        //保存该指令对应的Value
        baseAddr.getContext().saveValue(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String virtualReg = "%" + SlotTracker.getInstance().getSlot(this);
        sb.append(virtualReg);
        sb.append(" = getelementptr ");
        sb.append(((PointerType) baseAddr.dataType).refType);
        sb.append(", ");
        sb.append(baseAddr.getUseStr());
        if (((PointerType) baseAddr.dataType).refType instanceof ArrayType) {
            sb.append(", ");
            sb.append(selfOffset.getUseStr());
        }
        sb.append(", ");
        sb.append(offset.getUseStr());
        return sb.toString();
    }
}
