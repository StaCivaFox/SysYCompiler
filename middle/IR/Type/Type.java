package middle.IR.Type;

import middle.IR.Context;

public class Type {
    public enum TypeID {
        VoidTy,
        LabelTy,
        IntegerTy,
        FunctionTy,
        PointerTy,
        ArrayTy
    }

    public TypeID typeID;
    public Context context;


    public Type(TypeID typeID, Context context) {
        this.context = context;
        this.typeID = typeID;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Type o)) return false;
        return this.typeID.equals(o.typeID);
    }
}
