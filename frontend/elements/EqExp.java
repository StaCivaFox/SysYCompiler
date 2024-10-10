package frontend.elements;

import frontend.Token;

import java.util.ArrayList;

public class EqExp extends SyntaxNode {
    public ArrayList<RelExp> relExps;
    public ArrayList<Token> eqExpOps;

    public EqExp(ArrayList<RelExp> relExps, ArrayList<Token> eqExpOps) {
        this.relExps = relExps;
        this.eqExpOps = eqExpOps;
        childrenNodes.addAll(relExps);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (RelExp relExp : relExps) {
            sb.append(relExp.toString());
            sb.append("<EqExp>\n");
            if (i < eqExpOps.size()) {
                sb.append(eqExpOps.get(i).toString());
                i++;
            }
        }
        return sb.toString();
    }
}
