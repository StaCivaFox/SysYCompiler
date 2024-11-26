package middle.IR;

import middle.IR.Type.Type;

public class Argument extends Value {
    public Argument(String name, Type dataType) {
        super(name, ValueType.ArgumentVTy, dataType);
        dataType.context.saveValue(this);
    }
}
