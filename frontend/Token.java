package frontend;

import frontend.elements.SyntaxNode;

import java.math.BigInteger;

public class Token /*extends SyntaxNode*/ {
    private TokenType tokenType;
    private int lineno;
    private String content;

    public Token(TokenType tokenType, int lineno, String content) {
        this.tokenType = tokenType;
        this.lineno = lineno;
        this.content = content;
    }

    public BigInteger getNumberValue() {
        return BigInteger.valueOf(Long.parseLong(this.content));
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public int getLineno() {
        return lineno;
    }

    public String getContent() {
        return this.content;
    }

    @Override
    public String toString() {
        return tokenType.toString() + " " + content;
    }
}

