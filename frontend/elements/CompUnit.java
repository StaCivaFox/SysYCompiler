package frontend.elements;

import java.util.ArrayList;

public class CompUnit extends SyntaxNode{
    public ArrayList<Decl> decls;
    public ArrayList<FuncDef> funcDefs;
    public MainFuncDef mainFuncDef;

    public CompUnit(ArrayList<Decl> decls, ArrayList<FuncDef> funcDefs, MainFuncDef mainFuncDef) {
        super();
        this.decls = decls;
        this.funcDefs = funcDefs;
        this.mainFuncDef = mainFuncDef;
        childrenNodes.addAll(decls);
        childrenNodes.addAll(funcDefs);
        childrenNodes.add(mainFuncDef);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Decl decl : decls) {
            sb.append(decl.toString());
        }
        for (FuncDef funcDef : funcDefs) {
            sb.append(funcDef.toString());
        }
        sb.append(mainFuncDef.toString());
        sb.append("<CompUnit>\n");
        return sb.toString();
    }
}
