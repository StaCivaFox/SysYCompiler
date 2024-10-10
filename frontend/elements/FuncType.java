package frontend.elements;

public class FuncType extends SyntaxNode {
    public String type; //void/int/char

    public FuncType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (type.equals("void")) {
            sb.append("VOIDTK void\n");
        }
        else if (type.equals("int")) {
            sb.append("INTTK int\n");
        }
        else {
            sb.append("CHARTK char\n");
        }
        sb.append("<FuncType>\n");
        return sb.toString();
    }
}
