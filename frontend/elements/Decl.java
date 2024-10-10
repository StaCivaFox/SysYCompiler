package frontend.elements;

public class Decl extends SyntaxNode{
    public ConstDecl constDecl;
    public VarDecl varDecl;

    public Decl(ConstDecl constDecl) {
        this.constDecl = constDecl;
        this.varDecl = null;
        childrenNodes.add(constDecl);
    }

    public Decl(VarDecl varDecl) {
        this.varDecl = varDecl;
        this.constDecl = null;
        childrenNodes.add(varDecl);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (constDecl != null)
            sb.append(constDecl.toString());
        else sb.append(varDecl.toString());
        return sb.toString();
    }
}
