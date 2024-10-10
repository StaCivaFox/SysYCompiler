package frontend.elements;

public class Number extends SyntaxNode {
    public IntConst intConst;

    public Number(IntConst intConst) {
        this.intConst = intConst;
        childrenNodes.add(intConst);
    }

    @Override
    public String toString() {
        return intConst.toString() +
                "<Number>\n";
    }
}
