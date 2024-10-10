package frontend.elements;

import java.util.ArrayList;

public class ConstDef extends SyntaxNode{
    public Ident ident;
    public ArrayList<ConstExp> constExps;
    public ConstInitVal constInitVal;

    public ConstDef(Ident ident, ArrayList<ConstExp> constExps, ConstInitVal constInitVal) {
        this.ident = ident;
        this.constExps = constExps;
        this.constInitVal = constInitVal;
        childrenNodes.add(ident);
        childrenNodes.addAll(constExps);
        childrenNodes.add(constInitVal);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.toString());
        for (ConstExp constExp : constExps) {
            sb.append("LBRACK [\n");
            sb.append(constExp.toString());
            sb.append("RBRACK ]\n");
        }
        sb.append("ASSIGN =\n");
        sb.append(constInitVal.toString());
        return sb.toString();
    }
}
