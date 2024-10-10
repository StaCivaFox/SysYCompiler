package frontend.elements;

public class Cond extends SyntaxNode {
    public LOrExp lOrExp;

    public Cond(LOrExp lOrExp) {
        this.lOrExp = lOrExp;
        childrenNodes.add(lOrExp);
    }

    @Override
    public String toString() {
        return lOrExp.toString() + "<Cond>\n";
    }
}
