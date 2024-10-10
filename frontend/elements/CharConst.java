package frontend.elements;

import frontend.Token;

public class CharConst extends SyntaxNode {
    public Token token;

    public CharConst(Token token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return token.toString() + "\n";
    }
}
