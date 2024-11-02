package middle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class SymbolTable {
    public int id;
    public SymbolTable fatherTable;
    public ArrayList<SymbolTable> childrenTables;
    public LinkedHashMap<String, Symbol> symbolDirectory;

    public SymbolTable(int id, SymbolTable fatherTable) {
        this.id = id;
        this.fatherTable = fatherTable;
        this.childrenTables = new ArrayList<>();
        this.symbolDirectory = new LinkedHashMap<>();
    }

    public SymbolTable newTable(int id) {
        int newId = id + 1;
        SymbolTable newTable = new SymbolTable(newId, this);
        this.childrenTables.add(newTable);
        return newTable;
    }

    public SymbolTable back() {
        return this.fatherTable;
    }

    public boolean existSymbol(String symbolName) {
        return symbolDirectory.containsKey(symbolName);
    }

    //在调用该方法前创建好Symbol对象，该方法只实现简单的插入
    //如果Symbol已存在，返回false，交由上层进行重定义的错误处理
    public boolean addSymbol(Symbol symbol) {
        String symbolName = symbol.name;
        if (existSymbol(symbolName)) {
            return false;
        }
        symbolDirectory.put(symbolName, symbol);
        return true;
    }

    //从当前符号表开始，逐级向上查找symbolName对应的Symbol对象
    //如果找到，则返回Symbol对象；否则返回null
    //由于我的符号表边建边查，所以不会出现表中能查到，但实际尚未定义的情况
    public Symbol getSymbol(String symbolName) {
        if (symbolDirectory.containsKey(symbolName)) {
            return symbolDirectory.get(symbolName);
        }
        else if (fatherTable != null) {
            return fatherTable.getSymbol(symbolName);
        }
        else {
            return null;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Symbol symbol : symbolDirectory.values()) {
            sb.append(symbol.toString());
        }
        for (SymbolTable symbolTable : childrenTables) {
            sb.append(symbolTable.toString());
        }
        return sb.toString();
    }
}
