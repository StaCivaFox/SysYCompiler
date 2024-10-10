package frontend.elements;

import java.util.ArrayList;

public class VarDef extends SyntaxNode{
    public Ident ident;
    public ArrayList<ConstExp> constExps;
    public InitVal initVal;

    //无初值的情况
    public VarDef(Ident ident, ArrayList<ConstExp> constExps) {
        this.ident = ident;
        this.constExps = constExps;
        childrenNodes.add(ident);
        childrenNodes.addAll(constExps);
    }

    //有初值的情况
    public VarDef(Ident ident, ArrayList<ConstExp> constExps, InitVal initVal) {
        this.ident = ident;
        this.constExps = constExps;
        this.initVal = initVal;
        childrenNodes.add(ident);
        childrenNodes.addAll(constExps);
        childrenNodes.add(initVal);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.toString());
        for (ConstExp constExp : constExps) {
            sb.append("LBRACK [\n");
            sb.append(constExp);
            sb.append("RBRACK ]\n");
        }
        if (initVal != null) {
            sb.append("ASSIGN =\n");
            sb.append(initVal.toString());
        }
        sb.append("<VarDef>\n");
        return sb.toString();
    }
}
