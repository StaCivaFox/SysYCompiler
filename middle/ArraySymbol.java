package middle;

import middle.IR.Value;

public class ArraySymbol extends Symbol {
    public int dim;        //无必要，因为实验要求中只有一维数组）））

    public ArraySymbol(int tableId, String name, int lineno,
                       SymbolType type, String bType, Value value,
                       int dim) {
        super(tableId, name, lineno, type, bType, value);
        this.dim = dim;
    }
}
