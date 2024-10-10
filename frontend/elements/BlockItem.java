package frontend.elements;

public class BlockItem extends SyntaxNode {
    public Decl decl;
    public Stmt stmt;

    public BlockItem(Decl decl) {
        this.decl = decl;
        childrenNodes.add(decl);
    }

    public BlockItem(Stmt stmt) {
        this.stmt = stmt;
        childrenNodes.add(stmt);
    }

    @Override
    public String toString() {
        if (decl != null) return decl.toString();
        else return stmt.toString();
    }


}
