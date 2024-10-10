package frontend.elements;

import frontend.Token;

import java.util.ArrayList;

public class LOrExp extends SyntaxNode {
    public ArrayList<LAndExp> lAndExps;
    public ArrayList<Token> lOrExpOps;

    public LOrExp(ArrayList<LAndExp> lAndExps, ArrayList<Token> lOrExpOps) {
        this.lAndExps = lAndExps;
        this.lOrExpOps = lOrExpOps;
        childrenNodes.addAll(lAndExps);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (LAndExp lAndExp : lAndExps) {
            sb.append(lAndExp.toString());
            sb.append("<LOrExp>\n");
            if (i < lOrExpOps.size()) {
                sb.append(lOrExpOps.get(i).toString());
                i++;
            }
        }
        return sb.toString();
    }
}
