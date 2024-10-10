package frontend.elements;

public class FuncDef extends SyntaxNode{
    public FuncType funcType;
    public Ident ident;
    public FuncFParams funcFParams;
    public Block block;

    //无形参
    public FuncDef(FuncType funcType, Ident ident, Block block) {
        this.funcType = funcType;
        this.ident = ident;
        this.block = block;
        childrenNodes.add(funcType);
        childrenNodes.add(ident);
        childrenNodes.add(block);
    }

    //有形参
    public FuncDef(FuncType funcType, Ident ident, FuncFParams funcFParams, Block block) {
        this.funcType = funcType;
        this.ident = ident;
        this.funcFParams = funcFParams;
        this.block = block;
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
}
