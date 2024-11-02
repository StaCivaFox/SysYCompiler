package frontend.elements;

import frontend.Token;
import middle.SymbolTable;
import middle.SymbolType;

import java.util.ArrayList;

public class AddExp extends SyntaxNode {
    public ArrayList<MulExp> mulExps;
    public ArrayList<Token> addExpOps;

    public AddExp(ArrayList<MulExp> mulExps, ArrayList<Token> addExpOps) {
        this.mulExps = mulExps;
        this.addExpOps = addExpOps;
        childrenNodes.addAll(mulExps);
    }

    public SymbolType getType(SymbolTable symbolTable) {
        return mulExps.get(0).getType(symbolTable);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (MulExp mulExp : mulExps) {
            sb.append(mulExp.toString());
            sb.append("<AddExp>\n");
            if (i < addExpOps.size()){
                sb.append(addExpOps.get(i).toString());
                sb.append("\n");
                i++;
            }
        }
        return sb.toString();
    }
}
