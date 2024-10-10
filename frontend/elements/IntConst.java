package frontend.elements;

import frontend.Token;

public class IntConst extends SyntaxNode {
    public Token token;

    public IntConst(Token token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return token.toString() + "\n";
    }
}
