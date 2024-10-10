package frontend.elements;

import java.util.ArrayList;

public class ConstInitVal extends SyntaxNode{
    public ArrayList<ConstExp> constExps;
    public StringConst stringConst;
    public VarType varType;

    public ConstInitVal(ArrayList<ConstExp> constExps, VarType varType) {
        this.constExps = constExps;
        this.varType = varType;
        childrenNodes.addAll(constExps);
    }

    public ConstInitVal(StringConst stringConst) {
        this.stringConst = stringConst;
        childrenNodes.add(stringConst);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (stringConst != null) {
            sb.append(stringConst.toString());
            sb.append("<ConstInitVal>\n");
            return sb.toString();
        }
        if (varType == VarType.Var) {
            sb.append(constExps.get(0).toString());
        }
        else {
            sb.append("LBRACE {\n");
            for (int i = 0; i < constExps.size(); i++) {
                sb.append(constExps.get(i).toString());
                if (i < constExps.size() - 1) sb.append("COMMA ,\n");
            }
            sb.append("RBRACE }\n");
        }
        sb.append("<ConstInitVal>\n");
        return sb.toString();
    }
}
