package frontend.elements;

public class UnaryOp extends SyntaxNode {
    public String op;

    public UnaryOp(String op) {
        this.op = op;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        switch (op) {
            case "+" -> sb.append("PLUS +\n");
            case "-" -> sb.append("MINU -\n");
            case "!" -> sb.append("NOT !\n");
        }
        sb.append("<UnaryOp>\n");
        return sb.toString();
    }
}
