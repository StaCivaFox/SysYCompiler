package frontend.elements;

public class FuncDef extends SyntaxNode{
    public FuncType funcType;
    public Ident ident;
    public FuncFParams funcFParams;
    public Block block;
    public int endLine;

    //无形参
    public FuncDef(FuncType funcType, Ident ident, Block block, int endLine) {
        this.funcType = funcType;
        this.ident = ident;
        this.block = block;
        this.endLine = endLine;
        childrenNodes.add(funcType);
        childrenNodes.add(ident);
        childrenNodes.add(block);
    }

    //有形参
    public FuncDef(FuncType funcType, Ident ident, FuncFParams funcFParams, Block block, int endLine) {
        this.funcType = funcType;
        this.ident = ident;
        this.funcFParams = funcFParams;
        this.block = block;
        this.endLine = endLine;
        childrenNodes.add(funcType);
        childrenNodes.add(ident);
        childrenNodes.add(funcFParams);
        childrenNodes.add(block);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(funcType.toString());
        sb.append(ident.toString());
        sb.append("LPARENT (\n");
        if (funcFParams != null) sb.append(funcFParams.toString());
        sb.append("RPARENT )\n");
        sb.append(block.toString());
        sb.append("<FuncDef>\n");
        return sb.toString();
    }

    public boolean hasReturn() {
        if (!block.blockItems.isEmpty()) {
            Stmt stmt = block.blockItems.get(block.blockItems.size() - 1).stmt;
            if (stmt != null) {
                return stmt.stmtType.equals(Stmt.StmtType.Return);
            }
            return false;
        }
        return false;
    }
}
