package frontend.elements;

import java.util.ArrayList;

public class FuncRParams extends SyntaxNode {
    public ArrayList<Exp> exps;

    public FuncRParams(ArrayList<Exp> exps) {
        this.exps = exps;
        childrenNodes.addAll(exps);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < exps.size(); i++) {
            sb.append(exps.get(i).toString());
            if (i < exps.size() - 1) sb.append("COMMA ,\n");
        }
        sb.append("<FuncRParams>\n");
        return sb.toString();
    }
}
