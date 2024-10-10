package frontend.elements;

public class BType extends SyntaxNode{
    public String type;

    public BType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        if (type.equals("int")) {
            return "INTTK int\n";
        }
        else return "CHARTK char\n";
    }
}
