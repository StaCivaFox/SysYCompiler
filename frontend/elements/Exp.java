package frontend.elements;

import middle.SymbolTable;
import middle.SymbolType;

public class Exp extends SyntaxNode {
    public AddExp addExp;

    public Exp(AddExp addExp) {
        this.addExp = addExp;
        childrenNodes.add(addExp);
    }

    public SymbolType getType(SymbolTable symbolTable) {
        return addExp.getType(symbolTable);
    }

    @Override
    public String toString() {
        return addExp.toString() +
                "<Exp>\n";
    }
}
