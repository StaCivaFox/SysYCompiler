package frontend.elements;

import middle.Symbol;
import middle.SymbolTable;
import middle.SymbolType;
import utils.ErrorReporter;

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

    public SymbolType getType(SymbolTable symbolTable) {
        if (exp != null) return exp.getType(symbolTable);
        else if (lVal != null) {
            return lVal.getType(symbolTable);
        }
        else if (number != null) return SymbolType.Int;
        else return SymbolType.Char;
    }

    public String getNumber() {
        return this.number.intConst.token.getContent();
    }

    public String getCharacter() {
        return this.character.charConst.token.getContent();
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
