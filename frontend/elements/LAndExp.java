package frontend.elements;

import frontend.Token;

import java.util.ArrayList;

public class LAndExp extends SyntaxNode {
    public ArrayList<EqExp> eqExps;
    public ArrayList<Token> lAndExpOps;

    public LAndExp(ArrayList<EqExp> eqExps, ArrayList<Token> lAndExpOps) {
        this.eqExps = eqExps;
        this.lAndExpOps = lAndExpOps;
        childrenNodes.addAll(eqExps);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (EqExp eqExp : eqExps) {
            sb.append(eqExp.toString());
            sb.append("<LAndExp>\n");
            if (i < lAndExpOps.size()) {
                sb.append(lAndExpOps.get(i).toString());
                i++;
            }
        }
        return sb.toString();
    }
}
