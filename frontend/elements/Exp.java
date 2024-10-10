package frontend.elements;

public class Exp extends SyntaxNode {
    public AddExp addExp;

    public Exp(AddExp addExp) {
        this.addExp = addExp;
        childrenNodes.add(addExp);
    }

    @Override
    public String toString() {
        return addExp.toString() +
                "<Exp>\n";
    }
}
