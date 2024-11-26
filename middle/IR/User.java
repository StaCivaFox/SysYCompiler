package middle.IR;

import middle.IR.Type.Type;

import java.util.ArrayList;

public class User extends Value {

    //保存它使用的Value
    public ArrayList<Value> valueList;

    public User(ValueType valueType, Type dataType) {
        super(valueType, dataType);
        this.valueList = new ArrayList<>();
    }
}
