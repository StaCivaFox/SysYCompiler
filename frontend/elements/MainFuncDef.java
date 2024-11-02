package frontend.elements;

public class MainFuncDef extends SyntaxNode{
    public Block block;
    public int endLine;

    public MainFuncDef(Block block, int endLine) {
        this.block = block;
        this.endLine = endLine;
        childrenNodes.add(block);
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("INTTK int\n");
        sb.append("MAINTK main\n");
        sb.append("LPARENT (\n");
        sb.append("RPARENT )\n");
        sb.append(block.toString());
        sb.append("<MainFuncDef>\n");
        return sb.toString();
    }
}
