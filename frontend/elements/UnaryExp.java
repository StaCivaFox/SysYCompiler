package frontend.elements;

public class UnaryExp extends SyntaxNode {
    public PrimaryExp primaryExp;
    public Ident ident;
    public FuncRParams funcRParams;
    public UnaryOp unaryOp;
    public UnaryExp unaryExp;

    public UnaryExp(PrimaryExp primaryExp) {
        this.primaryExp = primaryExp;
        childrenNodes.add(primaryExp);
    }

    public UnaryExp(Ident ident, FuncRParams funcRParams) {
        this.ident = ident;
        this.funcRParams = funcRParams;
        childrenNodes.add(ident);
        childrenNodes.add(funcRParams);
    }

    public UnaryExp(UnaryOp unaryOp, UnaryExp unaryExp) {
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
        childrenNodes.add(unaryOp);
        childrenNodes.add(unaryExp);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (primaryExp != null) {
            sb.append(primaryExp.toString());
        }
        else if (ident != null) {
            sb.append(ident.toString());
            sb.append("LPARENT (\n");
            if (funcRParams != null) {
                sb.append(funcRParams.toString());
            }
            sb.append("RPARENT )\n");
        }
        else if (unaryOp != null) {
            sb.append(unaryOp.toString());
            sb.append(unaryExp.toString());
        }
        sb.append("<UnaryExp>\n");
        return sb.toString();
    }
}
