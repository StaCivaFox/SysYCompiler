package middle.IR.Type;

public class PointerType extends Type {
    public Type refType;

    public PointerType(Type refType) {
        super(TypeID.PointerTy, refType.context);
        this.refType = refType;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PointerType o)) return false;
        return refType.equals(o.refType);
    }

    @Override
    public String toString() {
        return refType.toString() + "*";
    }
}
