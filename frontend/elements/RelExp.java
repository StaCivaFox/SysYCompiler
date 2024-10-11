package frontend.elements;

import frontend.Token;

import java.util.ArrayList;

public class RelExp extends SyntaxNode {
    public ArrayList<AddExp> addExps;
    public ArrayList<Token> relExpOps;

    public RelExp(ArrayList<AddExp> addExps, ArrayList<Token> relExpOps) {
        this.addExps = addExps;
        this.relExpOps = relExpOps;
        childrenNodes.addAll(addExps);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (AddExp addExp : addExps) {
            sb.append(addExp.toString());
            sb.append("<RelExp>\n");
            if (i < relExpOps.size()) {
                sb.append(relExpOps.get(i).toString());
                sb.append("\n");
                i++;
            }
        }
        return sb.toString();
    }
}
