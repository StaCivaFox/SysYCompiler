package frontend.elements;

import middle.Symbol;
import middle.SymbolTable;
import middle.SymbolType;

import java.util.ArrayList;

public class LVal extends SyntaxNode {
    public Ident ident;
    public ArrayList<Exp> exps;
    public VarType varType;

    public LVal(Ident ident) {
        this.ident = ident;
        childrenNodes.add(ident);
    }

    public LVal(Ident ident, ArrayList<Exp> exps) {
        this.ident = ident;
        this.exps = exps;
        childrenNodes.add(ident);
        childrenNodes.addAll(exps);
    }

    public SymbolType getType(SymbolTable symbolTable) {
        if (exps != null && !exps.isEmpty()) {
            //数组索引
            Symbol identSymbol = symbolTable.getSymbol(ident.name());
            if (identSymbol == null) return null;
            if (identSymbol.type.equals(SymbolType.IntArray)) return SymbolType.Int;
            else if (identSymbol.type.equals(SymbolType.CharArray)) return SymbolType.Char;
            else return null;
        }
        else {
            //普通变量
            Symbol identSymbol = symbolTable.getSymbol(ident.name());
            if (identSymbol == null) return null;
            else return identSymbol.type;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.toString());
        if (exps != null && !exps.isEmpty()) {
            sb.append("LBRACK [\n");
            sb.append(exps.get(0).toString());
            sb.append("RBRACK ]\n");
        }
        sb.append("<LVal>\n");
        return sb.toString();
    }
}
