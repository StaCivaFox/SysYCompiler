package frontend.elements;

public class PrimaryExp extends SyntaxNode {
    public Exp exp;
    public LVal lVal;
    public Number number;
    public Character character;

    public PrimaryExp(Exp exp) {
        this.exp = exp;
        childrenNodes.add(exp);
    }

    public PrimaryExp(LVal lVal) {
        this.lVal = lVal;
        childrenNodes.add(lVal);
    }

    public PrimaryExp(Number number) {
        this.number = number;
        childrenNodes.add(number);
    }

    public PrimaryExp(Character character) {
        this.character = character;
        childrenNodes.add(character);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (exp != null) {
            sb.append("LPARENT (\n");
            sb.append(exp.toString());
            sb.append("RPARENT )\n");
        }
        else if (lVal != null) {
            sb.append(lVal.toString());
        }
        else if (number != null) {
            sb.append(number.toString());
        }
        else if (character != null) {
            sb.append(character.toString());
        }
        sb.append("<PrimaryExp>\n");
        return sb.toString();
    }
}
