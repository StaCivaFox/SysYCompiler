package middle;

public class VariableSymbol extends Symbol {
    public VariableSymbol(int tableId, String name, int lineno,
                          SymbolType type, String bType, Value value) {
        super(tableId, name, lineno, type, bType, value);
    }
}
