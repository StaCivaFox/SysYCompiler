package middle.IR.Type;

import middle.IR.Context;

public class ArrayType extends Type {
    public Type elementType;
    public int size;

    public ArrayType(Type elementType, int size) {
        super(TypeID.ArrayTy, elementType.context);
        this.elementType = elementType;
        this.size = size;
    }

    //支持多维数组的类型系统（虽然没什么用）
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ArrayType o)) return false;
        return this.elementType.equals(o.elementType) && this.size == o.size;
    }

    @Override
    public String toString() {
        return String.format("[%d x %s]", size, elementType.toString());
    }
}
