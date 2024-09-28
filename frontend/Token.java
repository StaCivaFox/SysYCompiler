package frontend;

import java.math.BigInteger;

public class Token {
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

    @Override
    public String toString() {
        return tokenType.toString() + " " + content;
    }
}

