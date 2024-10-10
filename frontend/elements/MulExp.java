package frontend.elements;

import frontend.Token;

import java.util.ArrayList;

public class MulExp extends SyntaxNode {
    public ArrayList<UnaryExp> unaryExps;
    public ArrayList<Token> mulExpOps;

    public MulExp(ArrayList<UnaryExp> unaryExps, ArrayList<Token> mulExpOps) {
        this.unaryExps = unaryExps;
        this.mulExpOps = mulExpOps;
        childrenNodes.addAll(unaryExps);
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
                i++;
            }
        }
        return sb.toString();
    }
}
