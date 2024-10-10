package frontend.elements;

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
