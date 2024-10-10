package frontend.elements;

import java.util.ArrayList;

public class VarDecl extends SyntaxNode{
    public BType bType;
    public ArrayList<VarDef> varDefs;

    public VarDecl(BType bType, ArrayList<VarDef> varDefs) {
        this.bType = bType;
        this.varDefs = varDefs;
        childrenNodes.add(bType);
        childrenNodes.addAll(varDefs);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(bType.toString());
        for (int i = 0; i < varDefs.size(); i++) {
            sb.append(varDefs.get(i).toString());
            if (i < varDefs.size() - 1) sb.append("COMMA ,\n");
        }
        sb.append("SEMICN ;\n");
        sb.append("<VarDecl>\n");
        return sb.toString();
    }
}
