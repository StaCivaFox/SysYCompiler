package frontend.elements;

public class ConstExp extends SyntaxNode {
    public AddExp addExp;

    public ConstExp(AddExp addExp) {
        this.addExp = addExp;
        childrenNodes.add(addExp);
    }

    @Override
    public String toString() {
        return addExp.toString() + "<ConstExp>\n";
    }
}
