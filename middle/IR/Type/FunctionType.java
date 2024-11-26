package middle.IR.Type;

import java.util.ArrayList;

public class FunctionType extends Type {
    public Type returnType;
    public ArrayList<Type> paramTypes;

    public FunctionType(Type returnType) {
        super(TypeID.FunctionTy, returnType.context);
        this.returnType = returnType;
    }

    public FunctionType(Type returnType, ArrayList<Type> paramTypes) {
        super(TypeID.FunctionTy, returnType.context);
        this.returnType = returnType;
        this.paramTypes = paramTypes;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FunctionType o)) return false;
        if (o.paramTypes == null) {
            return this.returnType.equals(o.returnType);
        }
        if (!this.returnType.equals(o.returnType))
            return false;
        if (this.paramTypes.size() != o.paramTypes.size())
            return false;
        int paramSize = this.paramTypes.size();
        for (int i = 0; i < paramSize; i++) {
            if (!this.paramTypes.get(i).equals(o.paramTypes.get(i)))
                return false;
        }
        return true;
    }

    //type (arg1, arg2, ...)
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(returnType).append(" ");
        sb.append("(");
        for (int i = 0; i < paramTypes.size(); i++) {
            sb.append(paramTypes.get(i));
            if (i != paramTypes.size() - 1) sb.append(", ");
        }
        sb.append(")");
        return sb.toString();
    }
}
