package frontend.elements;

import frontend.Token;

public class StringConst extends SyntaxNode{
    public Token token;

    public StringConst(Token token) {
        this.token = token;
    }

    public int paramNum() {
        int res = 0;
        for (int i = 0; i < token.getContent().length() - 1; i++) {
            if (token.getContent().charAt(i) == '%' &&
                    (token.getContent().charAt(i + 1) == 'd' || token.getContent().charAt(i + 1) == 'c'))
                res++;
        }
        return res;
    }

    @Override
    public String toString() {
        return this.token.toString() + "\n";
    }
}
