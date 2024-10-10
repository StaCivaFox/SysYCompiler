package frontend.elements;

public class FuncFParam extends SyntaxNode {
    public BType bType;
    public Ident ident;
    public VarType varType;

    public FuncFParam(BType bType, Ident ident, VarType varType) {
        this.bType = bType;
        this.ident = ident;
        this.varType = varType;
        childrenNodes.add(bType);
        childrenNodes.add(ident);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(bType.toString());
        sb.append(ident.toString());
        if (varType.equals(VarType.Array)) {
            sb.append("LBRACK [\n");
            sb.append("RBRACK ]\n");
        }
        sb.append("<FuncFParam>\n");
        return sb.toString();
    }
}
