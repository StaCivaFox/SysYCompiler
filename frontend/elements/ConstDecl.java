package frontend.elements;

import frontend.Token;

import java.util.ArrayList;

public class ConstDecl extends SyntaxNode{
    public BType bType;
    public ArrayList<ConstDef> constDefs;

    public ConstDecl(BType bType, ArrayList<ConstDef> constDefs) {
        this.bType = bType;
        this.constDefs = constDefs;
        childrenNodes.add(bType);
        childrenNodes.addAll(constDefs);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CONSTTK const\n");
        sb.append(bType.toString());
        for (int i = 0; i < constDefs.size(); i++) {
            sb.append(constDefs.get(i).toString());
            if (i < constDefs.size() - 1)
                sb.append("COMMA ,\n");
        }
        sb.append("SEMICN ;\n");
        sb.append("<ConstDecl>\n");
        return sb.toString();
    }
}
