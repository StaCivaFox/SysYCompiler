package frontend.elements;

public class Character extends SyntaxNode {
    public CharConst charConst;

    public Character(CharConst charConst) {
        this.charConst = charConst;
        childrenNodes.add(charConst);
    }

    @Override
    public String toString() {
        return charConst.toString() + "<Character>\n";
    }
}
