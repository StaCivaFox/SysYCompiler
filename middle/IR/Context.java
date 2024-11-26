package middle.IR;

import middle.IR.Type.*;

import java.util.ArrayList;

public class Context {
    public ArrayList<PointerType> pointerTypes = new ArrayList<>();
    public ArrayList<ArrayType> arrayTypes = new ArrayList<>();
    public ArrayList<FunctionType> functionTypes = new ArrayList<>();

    public ArrayList<Use> uses = new ArrayList<>();
    public ArrayList<Value> values = new ArrayList<>();

    public Context() {}

    //管理类型
    public PointerType getPointerType(Type refType) {
        PointerType tmpPointerType = new PointerType(refType);
        for (PointerType pointerType : pointerTypes) {
            if (pointerType.equals(tmpPointerType)) {
                return pointerType;
            }
        }
        pointerTypes.add(tmpPointerType);
        return tmpPointerType;
    }

    public FunctionType getFunctionType(Type returnType) {
        FunctionType tmpFunctionType = new FunctionType(returnType);
        for (FunctionType functionType : functionTypes) {
            if (functionType.equals(tmpFunctionType)) {
                return functionType;
            }
        }
        functionTypes.add(tmpFunctionType);
        return tmpFunctionType;
    }

    public FunctionType getFunctionType(Type returnType, ArrayList<Type> paramTypes) {
        FunctionType tmpFunctionType = new FunctionType(returnType, paramTypes);
        for (FunctionType functionType : functionTypes) {
            if (functionType.equals(tmpFunctionType)) {
                return functionType;
            }
        }
        functionTypes.add(tmpFunctionType);
        return tmpFunctionType;
    }

    public ArrayType getArrayType(Type elementType, int size) {
        ArrayType tmpArrayType = new ArrayType(elementType, size);
        for (ArrayType arrayType : arrayTypes) {
            if (arrayType.equals(tmpArrayType)) {
                return arrayType;
            }
        }
        arrayTypes.add(tmpArrayType);
        return tmpArrayType;
    }

    public Type getVoidTy() {
        return new Type(Type.TypeID.VoidTy, this);
    }

    public Type getLabelTy() {
        return new Type(Type.TypeID.LabelTy, this);
    }

    public IntegerType getInt1Ty() {
        return new IntegerType(this, 1);
    }

    public IntegerType getInt8Ty() {
        return new IntegerType(this, 8);
    }

    public IntegerType getInt32Ty() {
        return new IntegerType(this, 32);
    }

    //管理全局的def和use
    public void saveValue(Value value) {
        this.values.add(value);
    }

    public void saveUse(Use use) {
        this.uses.add(use);
    }
}
