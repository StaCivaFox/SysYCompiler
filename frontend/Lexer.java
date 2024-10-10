package frontend;

import utils.ErrorPrinter;

import java.io.IOException;
import java.io.PrintStream;

public class Lexer {
    private String source;
    private Token curToken;
    private int pos = 0;
    private int curLineno = 1;


    public Lexer(String input) throws IOException {
        this.source = input;
        this.pos = 0;
        this.curLineno = 1;
        this.next();
    }

    private boolean isIdentifierNondigit(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private TokenType reserve(String tokenContent) {
        if (tokenContent.equals("main")) return TokenType.MAINTK;
        if (tokenContent.equals("const")) return TokenType.CONSTTK;
        if (tokenContent.equals("int")) return TokenType.INTTK;
        if (tokenContent.equals("char")) return TokenType.CHARTK;
        if (tokenContent.equals("break")) return TokenType.BREAKTK;
        if (tokenContent.equals("continue")) return TokenType.CONTINUETK;
        if (tokenContent.equals("if")) return TokenType.IFTK;
        if (tokenContent.equals("else")) return TokenType.ELSETK;
        if (tokenContent.equals("for")) return TokenType.FORTK;
        if (tokenContent.equals("getint")) return TokenType.GETINTTK;
        if (tokenContent.equals("getchar")) return TokenType.GETCHARTK;
        if (tokenContent.equals("printf")) return TokenType.PRINTFTK;
        if (tokenContent.equals("return")) return TokenType.RETURNTK;
        if (tokenContent.equals("void")) return TokenType.VOIDTK;
        return TokenType.IDENFR;
    }

    private Token getIdentOrReserve() {
        StringBuilder sb = new StringBuilder();
        while (pos < source.length()
                && (isIdentifierNondigit(source.charAt(pos)) || Character.isDigit(source.charAt(pos)))) {
            sb.append(source.charAt(pos));
            pos++;
        }
        String tokenContent = sb.toString();
        TokenType tokenType = reserve(tokenContent);
        return new Token(tokenType, this.curLineno, tokenContent);
    }

    private Token getDigit() {
        StringBuilder sb = new StringBuilder();
        while (pos < source.length() && Character.isDigit((source.charAt(pos)))) {
            sb.append(source.charAt(pos));
            pos++;
        }
        String tokenContent = sb.toString();
        TokenType tokenType = TokenType.INTCON;
        return new Token(tokenType, this.curLineno, tokenContent);
    }

    private Token getStringConst() {
        StringBuilder sb = new StringBuilder();
        //处理开头的双引号
        sb.append(source.charAt(pos));
        pos++;
        while (pos < source.length() && source.charAt(pos) != '\"') {
            sb.append(source.charAt(pos));
            pos++;
        }
        //处理结尾的双引号
        sb.append(source.charAt(pos));
        pos++;
        String tokenContent = sb.toString();
        TokenType tokenType = TokenType.STRCON;
        return new Token(tokenType, this.curLineno, tokenContent);
    }

    private Token getCharConst() {
        StringBuilder sb = new StringBuilder();
        //处理开头的单引号
        sb.append(source.charAt(pos));
        pos++;
        while (pos < source.length() && source.charAt(pos) != '\'') {
            if (source.charAt(pos) == '\\') {
                sb.append(source.charAt(pos));
                pos++;
                sb.append(source.charAt(pos));
                pos++;
            }
            else {
                sb.append(source.charAt(pos));
                pos++;
            }
        }
        //处理结尾的单引号
        sb.append(source.charAt(pos));
        pos++;
        String tokenContent = sb.toString();
        TokenType tokenType = TokenType.CHRCON;
        return new Token(tokenType, this.curLineno, tokenContent);
    }

    //作为next()方法中的“默认分支”
    private Token getSymbol() throws IOException {
        char c = source.charAt(pos);
        StringBuilder sb = new StringBuilder();
        String tokenContent = null;
        TokenType tokenType = null;
        switch (c) {
            case '!': {
                sb.append(c);
                pos++;
                if (pos < source.length() && source.charAt(pos) == '=') {
                    sb.append(source.charAt(pos));
                    pos++;
                    tokenContent = sb.toString();
                    tokenType = TokenType.NEQ;
                }
                else {
                    tokenContent = sb.toString();
                    tokenType = TokenType.NOT;
                }
                break;
            }
            case '&': {     //TODO: 加入错误处理
                sb.append(c);
                pos++;
                if (pos < source.length() && source.charAt(pos) == '&') {
                    sb.append(source.charAt(pos));
                    pos++;
                    tokenContent = sb.toString();
                    tokenType = TokenType.AND;
                }
                else {
                    //TODO: error
                    ErrorPrinter.getInstance().print(String.valueOf(curLineno) + " " + "a");
                    this.next();
                    return this.curToken;
                }
                break;
            }
            case '|': {     //TODO: 加入错误处理
                sb.append(c);
                pos++;
                if (pos < source.length() && source.charAt(pos) == '|') {
                    sb.append(source.charAt(pos));
                    pos++;
                    tokenContent = sb.toString();
                    tokenType = TokenType.OR;
                }
                else {
                    //TODO: error
                    ErrorPrinter.getInstance().print(String.valueOf(curLineno) + " " + "a");
                    this.next();
                    return this.curToken;
                }
                break;
            }
            case '+': {
                tokenContent = String.valueOf(c);
                tokenType = TokenType.PLUS;
                pos++;
                break;
            }
            case '-': {
                tokenContent = String.valueOf(c);
                tokenType = TokenType.MINU;
                pos++;
                break;
            }
            case '*': {
                tokenContent = String.valueOf(c);
                tokenType = TokenType.MULT;
                pos++;
                break;
            }
            case '/': {
                //在这里处理注释
                sb.append(c);
                pos++;
                //处理单行注释
                if (pos < source.length() && source.charAt(pos) == '/') {
                    while (pos < source.length() && source.charAt(pos) != '\n') {
                        //如果需要记录注释内容，可以在这里用sb.append()
                        pos++;
                    }
                    if (pos < source.length()) {    //换行或直接结束
                        pos++;
                        curLineno++;
                    }
                    this.next();
                    return this.curToken;
                }
                //处理多行注释
                else if (pos < source.length() && source.charAt(pos) == '*') {
                    pos++;      //指向注释内容的第一个字符
                    if (pos < source.length() && source.charAt(pos) == '\n') {
                        curLineno++;
                        pos++;
                    }
                    while (pos < source.length()) {
                        while (pos < source.length() && source.charAt(pos) != '*') {
                            pos++;
                            if (source.charAt(pos) == '\n') {
                                curLineno++;
                                pos++;
                            }
                        }
                        while (pos < source.length() && source.charAt(pos) == '*') {
                            pos++;
                        }
                        if (pos < source.length() && source.charAt(pos) == '/') {
                            pos++;
                            break;
                        }
                    }
                    this.next();
                    return this.curToken;
                }
                //除号
                else {
                    tokenContent = sb.toString();
                    tokenType = TokenType.DIV;
                }
                break;
            }
            case '%': {
                tokenContent = String.valueOf(c);
                tokenType = TokenType.MOD;
                pos++;
                break;
            }
            case '<': {
                sb.append(c);
                pos++;
                if (pos < source.length() && source.charAt(pos) == '=') {
                    sb.append(source.charAt(pos));
                    pos++;
                    tokenContent = sb.toString();
                    tokenType = TokenType.LEQ;
                }
                else {
                    tokenContent = sb.toString();
                    tokenType = TokenType.LSS;
                }
                break;
            }
            case '>': {
                sb.append(c);
                pos++;
                if (pos < source.length() && source.charAt(pos) == '=') {
                    sb.append(source.charAt(pos));
                    pos++;
                    tokenContent = sb.toString();
                    tokenType = TokenType.GEQ;
                }
                else {
                    tokenContent = sb.toString();
                    tokenType = TokenType.GRE;
                }
                break;
            }
            case '=': {
                sb.append(c);
                pos++;
                if (pos < source.length() && source.charAt(pos) == '=') {
                    sb.append(source.charAt(pos));
                    pos++;
                    tokenContent = sb.toString();
                    tokenType = TokenType.EQL;
                }
                else {
                    tokenContent = sb.toString();
                    tokenType = TokenType.ASSIGN;
                }
                break;
            }
            case ';': {
                tokenContent = String.valueOf(c);
                tokenType = TokenType.SEMICN;
                pos++;
                break;
            }
            case ',': {
                tokenContent = String.valueOf(c);
                tokenType = TokenType.COMMA;
                pos++;
                break;
            }
            case '(': {
                tokenContent = String.valueOf(c);
                tokenType = TokenType.LPARENT;
                pos++;
                break;
            }
            case ')': {
                tokenContent = String.valueOf(c);
                tokenType = TokenType.RPARENT;
                pos++;
                break;
            }
            case '[': {
                tokenContent = String.valueOf(c);
                tokenType = TokenType.LBRACK;
                pos++;
                break;
            }
            case ']': {
                tokenContent = String.valueOf(c);
                tokenType = TokenType.RBRACK;
                pos++;
                break;
            }
            case '{': {
                tokenContent = String.valueOf(c);
                tokenType = TokenType.LBRACE;
                pos++;
                break;
            }
            case '}': {
                tokenContent = String.valueOf(c);
                tokenType = TokenType.RBRACE;
                pos++;
                break;
            }
            default: {
                //TODO: error
                break;
            }
        }
        return new Token(tokenType, this.curLineno, tokenContent);
    }

    //处理行号的变化
    //处理开头的空白符
    public void next() throws IOException {
        while (pos < source.length() &&
                (source.charAt(pos) == ' ' || source.charAt(pos) == '\n' || source.charAt(pos) == '\t' || source.charAt(pos) == '\r')) {
            if (source.charAt(pos) == '\n') {
                curLineno++;
            }
            pos++;
        }

        if (pos == source.length()) {
            return;
        }
        char c = source.charAt(pos);
        if (isIdentifierNondigit(c)) {
            curToken = getIdentOrReserve();
        }
        else if (Character.isDigit(c)) {
            curToken = getDigit();
        }
        else if (c == '\"') {
            curToken = getStringConst();
        }
        else if (c == '\'') {
            curToken = getCharConst();
        }
        else {
            curToken = getSymbol();
        }
    }

    public Token peek() {
        return this.curToken;
    }

    //词法分析作业用
    public boolean reachEnd() {
        return this.pos == source.length();
    }

    public int getCurLineno() {
        return curLineno;
    }

    public int getPos() {
        return pos;
    }

    //用于返回预读前的状态
    //TODO:单独测试
    public void restoreFromPreRead(int pos, int curLineno, Token curToken) {
        this.pos = pos;
        this.curLineno = curLineno;
        this.curToken = curToken;
    }
}
