package frontend.elements;

import java.util.ArrayList;

public class InitVal extends SyntaxNode{
    public ArrayList<Exp> exps;
    public StringConst stringConst;
    public VarType varType;

    public InitVal(ArrayList<Exp> exps, VarType varType) {
        this.exps = exps;
        this.varType = varType;
        childrenNodes.addAll(exps);
    }

    public InitVal(StringConst stringConst) {
        this.stringConst = stringConst;
        childrenNodes.add(stringConst);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (stringConst != null) {
            sb.append(stringConst.toString());
            sb.append("<InitVal\n>");
            return sb.toString();
        }
        if (varType.equals(VarType.Var)) {
            sb.append(exps.get(0).toString());
        }
        else {
            sb.append("LBRACE {\n");
            for (int i = 0; i < exps.size(); i++) {
                sb.append(exps.get(i).toString());
                if (i < exps.size() - 1) sb.append("COMMA ,\n");
            }
            sb.append("RBRACE }\n");
        }
        sb.append("<InitVal>\n");
        return sb.toString();
    }
}
