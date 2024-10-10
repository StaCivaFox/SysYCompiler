package frontend.elements;

public class MainFuncDef extends SyntaxNode{
    public Block block;

    public MainFuncDef(Block block) {
        this.block = block;
        childrenNodes.add(block);
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
