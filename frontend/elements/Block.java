package frontend.elements;

import java.util.ArrayList;

public class Block extends SyntaxNode {
    public ArrayList<BlockItem> blockItems;

    public Block(ArrayList<BlockItem> blockItems) {
        this.blockItems = blockItems;
        childrenNodes.addAll(blockItems);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LBRACE {\n");
        for (BlockItem blockItem : blockItems) {
            sb.append(blockItem.toString());
        }
        sb.append("RBRACE }\n");
        sb.append("<Block>\n");
        return sb.toString();
    }
}
