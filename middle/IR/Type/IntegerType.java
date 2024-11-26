package middle.IR.Type;

import middle.IR.Context;

public class IntegerType extends Type {
    public int bitwidth;
    public IntegerType(Context context, int bitwidth) {
        super(TypeID.IntegerTy, context);
        this.bitwidth = bitwidth;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IntegerType o)) return false;
        return this.bitwidth == o.bitwidth;
    }

    @Override
    public String toString() {
        return "i" + bitwidth;
    }
}
