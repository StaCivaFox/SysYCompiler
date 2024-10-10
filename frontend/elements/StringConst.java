package frontend.elements;

import frontend.Token;

public class StringConst extends SyntaxNode{
    public Token token;

    public StringConst(Token token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return this.token.toString() + "\n";
    }
}
