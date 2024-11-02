package middle;

public class Symbol {
    public int tableId;
    public String name;
    public int lineno;
    public SymbolType type;
    public String bType;       //int/char
    //TODO:用于中间代码生成
    public Value value;

    public Symbol(int tableId, String name, int lineno,
                  SymbolType type, String bType, Value value) {
        this.tableId = tableId;
        this.name = name;
        this.lineno = lineno;
        this.type = type;
        this.bType = bType;
        this.value = value;
    }

    @Override
    public String toString() {
        return tableId + " " + name + " " + type + '\n';
    }
}
