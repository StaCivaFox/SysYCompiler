package middle;

import java.util.ArrayList;

public class FuncSymbol extends Symbol {
    public int fParamCnt;
    public ArrayList<Symbol> fParamList;

    public FuncSymbol(int tableId, String name, int lineno,
                      SymbolType type, Value value,
                      int fParamCnt, ArrayList<Symbol> fParamList) {
        super(tableId, name, lineno, type,"", value);
        this.fParamCnt = fParamCnt;
        this.fParamList = fParamList;
    }

    public void setFParams(ArrayList<Symbol> fParamList) {
        this.fParamList = fParamList;
        this.fParamCnt = fParamList.size();
    }

}
