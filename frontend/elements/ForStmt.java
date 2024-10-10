package frontend.elements;

public class ForStmt extends SyntaxNode {
    public LVal lVal;
    public Exp exp;

    public ForStmt(LVal lVal, Exp exp) {
        this.lVal = lVal;
        this.exp = exp;
        childrenNodes.add(lVal);
        childrenNodes.add(exp);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lVal.toString());
        sb.append("ASSIGN =\n");
        sb.append(exp.toString());
        sb.append("<ForStmt>\n");
        return sb.toString();
    }
}
