package frontend.elements;

import frontend.Token;
import middle.SymbolTable;
import middle.SymbolType;

import java.util.ArrayList;

public class MulExp extends SyntaxNode {
    public ArrayList<UnaryExp> unaryExps;
    public ArrayList<Token> mulExpOps;

    public MulExp(ArrayList<UnaryExp> unaryExps, ArrayList<Token> mulExpOps) {
        this.unaryExps = unaryExps;
        this.mulExpOps = mulExpOps;
        childrenNodes.addAll(unaryExps);
    }

    public SymbolType getType(SymbolTable symbolTable) {
        return unaryExps.get(0).getType(symbolTable);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (UnaryExp unaryExp : unaryExps) {
            sb.append(unaryExp.toString());
            sb.append("<MulExp>\n");
            if (i < mulExpOps.size()) {
                sb.append(mulExpOps.get(i).toString());
                sb.append("\n");
                i++;
            }
        }
        return sb.toString();
    }
}
