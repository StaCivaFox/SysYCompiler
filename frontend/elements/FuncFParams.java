package frontend.elements;

import java.util.ArrayList;

public class FuncFParams extends SyntaxNode {
    public ArrayList<FuncFParam> funcFParams;

    public FuncFParams(ArrayList<FuncFParam> funcFParams) {
        this.funcFParams = funcFParams;
        childrenNodes.addAll(funcFParams);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < funcFParams.size(); i++) {
            sb.append(funcFParams.get(i).toString());
            if (i < funcFParams.size() - 1) sb.append("COMMA ,\n");
        }
        sb.append("<FuncFParams>\n");
        return sb.toString();
    }
}
