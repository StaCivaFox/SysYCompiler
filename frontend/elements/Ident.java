package frontend.elements;

import frontend.Token;

public class Ident extends SyntaxNode{
    public Token token;

    public Ident(Token token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return token.toString() + "\n";
    }

    public String name() {
        return token.getContent();
    }

    public int lineno() {
        return token.getLineno();
    }
}
