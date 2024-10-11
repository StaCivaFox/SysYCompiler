package frontend;

import frontend.elements.*;
import frontend.elements.Character;
import frontend.elements.Number;

import java.io.IOException;
import java.util.ArrayList;


public class Parser {
    private Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    private Token preRead(int bias) throws IOException {
        int oldPos = lexer.getPos();
        int oldCurLineno = lexer.getCurLineno();
        Token oldCurToken = lexer.peek();
        Token res;
        while (bias > 0) {
            lexer.next();
            bias--;
        }
        res = lexer.peek();
        lexer.restoreFromPreRead(oldPos, oldCurLineno, oldCurToken);
        return res;
    }

    private boolean tokenTypeIs(TokenType tokenType) {
        return lexer.peek().getTokenType().equals(tokenType);
    }

    private boolean tokenTypeIs(TokenType tokenType, int bias) throws IOException {
        Token targetToken = preRead(bias);
        return targetToken.getTokenType().equals(tokenType);
    }

    private void nextSym() throws IOException {
        lexer.next();
    }

    private Token getSym() {
        return lexer.peek();
    }

    public CompUnit parseCompUnit() throws IOException {
        ArrayList<Decl> decls = new ArrayList<>();
        ArrayList<FuncDef> funcDefs = new ArrayList<>();
        MainFuncDef mainFuncDef = null;
        //处理Decl
        while (tokenTypeIs(TokenType.CONSTTK)
                || ((tokenTypeIs(TokenType.INTTK) || tokenTypeIs(TokenType.CHARTK))
                && !tokenTypeIs(TokenType.LPARENT, 2))) {
            decls.add(parseDecl());
        }
        //处理FuncDef
        while (tokenTypeIs(TokenType.VOIDTK)
                || ((tokenTypeIs(TokenType.INTTK) || tokenTypeIs(TokenType.CHARTK))
                && tokenTypeIs(TokenType.LPARENT, 2)
                && !tokenTypeIs(TokenType.MAINTK, 1))) {
            funcDefs.add(parseFuncDef());
        }
        //处理MainFuncDef
        while (tokenTypeIs(TokenType.INTTK) && tokenTypeIs(TokenType.MAINTK, 1)) {
            mainFuncDef = parseMainFuncDef();
        }
        return new CompUnit(decls, funcDefs, mainFuncDef);
    }

    public Decl parseDecl() throws IOException {
        if (tokenTypeIs(TokenType.CONSTTK)) {
            return new Decl(parseConstDecl());
        }
        else {
            return new Decl(parseVarDecl());
        }
    }

    public ConstDecl parseConstDecl() throws IOException {
        BType bType = null;
        ArrayList<ConstDef> constDefs = new ArrayList<>();
        if (tokenTypeIs(TokenType.CONSTTK)) {
            nextSym();
            bType = parseBType();
            while (true) {
                constDefs.add(parseConstDef());
                if (tokenTypeIs(TokenType.COMMA)) {
                    nextSym();
                }
                else if (tokenTypeIs(TokenType.SEMICN)) {
                    break;
                }
                else {
                    //TODO:错误i
                    return new ConstDecl(bType, constDefs);
                }
            }
            nextSym();
            return new ConstDecl(bType, constDefs);
        }
        else {
            //TODO:实验未要求处理的错误
            return null;
        }
    }

    public BType parseBType() throws IOException {
        if (tokenTypeIs(TokenType.INTTK)) {
            nextSym();
            return new BType("int");
        }
        else if (tokenTypeIs(TokenType.CHARTK)) {
            nextSym();
            return new BType("char");
        }
        else {
            //TODO:实验未要求处理的错误
            nextSym();
            return null;
        }
    }

    public ConstDef parseConstDef() throws IOException {
        Ident ident = null;
        ArrayList<ConstExp> constExps = new ArrayList<>();
        ConstInitVal constInitVal = null;
        if (tokenTypeIs(TokenType.IDENFR)) {
            ident = new Ident(getSym());
            nextSym();
            while (tokenTypeIs(TokenType.LBRACK)) {
                nextSym();
                constExps.add(parseConstExp());
                if (tokenTypeIs(TokenType.RBRACK)) nextSym();
                else {
                    //TODO:错误k
                }
            }
            if (tokenTypeIs(TokenType.ASSIGN)) {
                nextSym();
                constInitVal = parseConstInitVal();
            }
            else {
                //TODO:实验未要求处理的错误
            }
        }
        else {
            //TODO:实验未要求处理的错误
        }
        return new ConstDef(ident, constExps, constInitVal);
    }

    public ConstInitVal parseConstInitVal() throws IOException {
        ArrayList<ConstExp> constExps = new ArrayList<>();
        StringConst stringConst;
        if (tokenTypeIs(TokenType.LBRACE)) {
            nextSym();
            while (true) {
                if (tokenTypeIs(TokenType.RBRACE)) {
                    nextSym();
                    break;
                }
                constExps.add(parseConstExp());
                if (tokenTypeIs(TokenType.COMMA)) {
                    nextSym();
                }
            }
            return new ConstInitVal(constExps, VarType.Array);
        }
        else if (tokenTypeIs(TokenType.STRCON)) {
            stringConst = new StringConst(getSym());
            nextSym();
            return new ConstInitVal(stringConst);
        }
        else {
            constExps.add(parseConstExp());
            return new ConstInitVal(constExps, VarType.Var);
        }
    }

    public VarDecl parseVarDecl() throws IOException {
        BType bType;
        ArrayList<VarDef> varDefs = new ArrayList<>();

        bType = parseBType();
        while (true) {
            varDefs.add(parseVarDef());
            if (tokenTypeIs(TokenType.COMMA)) nextSym();
            else if (tokenTypeIs(TokenType.SEMICN)) {
                nextSym();
                break;
            }
            else {
                //TODO:错误i
                return new VarDecl(bType, varDefs);
            }
        }
        return new VarDecl(bType, varDefs);
    }

    public VarDef parseVarDef() throws IOException {
        Ident ident;
        ArrayList<ConstExp> constExps = new ArrayList<>();
        InitVal initVal;
        ident = new Ident(getSym());
        nextSym();
        while (tokenTypeIs(TokenType.LBRACK)) {
            nextSym();
            constExps.add(parseConstExp());
            if (tokenTypeIs(TokenType.RBRACK)) nextSym();
            else {
                //TODO:错误k
            }
        }
        if (tokenTypeIs(TokenType.ASSIGN)) {
            nextSym();
            initVal = parseInitVal();
            return new VarDef(ident, constExps, initVal);
        }
        else {
            return new VarDef(ident, constExps);
        }
    }

    public InitVal parseInitVal() throws IOException {
        ArrayList<Exp> exps = new ArrayList<>();
        StringConst stringConst;
        if (tokenTypeIs(TokenType.LBRACE)) {
            nextSym();
            while (true) {
                if (tokenTypeIs(TokenType.RBRACE)) {
                    nextSym();
                    break;
                }
                exps.add(parseExp());
                if (tokenTypeIs(TokenType.COMMA)) {
                    nextSym();
                }
            }
            return new InitVal(exps, VarType.Array);
        }
        else if (tokenTypeIs(TokenType.STRCON)) {
            stringConst = new StringConst(getSym());
            nextSym();
            return new InitVal(stringConst);
        }
        else {
            exps.add(parseExp());
            return new InitVal(exps, VarType.Var);
        }
    }


    public FuncDef parseFuncDef() throws IOException {
        FuncType funcType;
        Ident ident;
        FuncFParams funcFParams;
        Block block = null;
        funcType = parseFuncType();
        ident = new Ident(getSym());
        nextSym();
        if (tokenTypeIs(TokenType.LPARENT)) {
            nextSym();
            if (tokenTypeIs(TokenType.RPARENT)) {
                funcFParams = null;
                nextSym();
                block = parseBlock();
            }
            else {
                //TODO:如果getsym()不属于FIRST(FuncFParams)，则有缺少右小括号的错误j
                funcFParams = parseFuncFParams();
                if (!tokenTypeIs(TokenType.RPARENT)) {
                    //TODO:错误j
                }
                else {
                    nextSym();
                    block = parseBlock();
                }
            }
            return new FuncDef(funcType, ident, funcFParams, block);
        }
        else {
            //TODO:实验未要求处理的错误
            return null;
        }
    }

    public MainFuncDef parseMainFuncDef() throws IOException {
        Block block = null;
        if (tokenTypeIs(TokenType.INTTK)) {
            nextSym();
            if (tokenTypeIs(TokenType.MAINTK)) {
                nextSym();
                if (tokenTypeIs(TokenType.LPARENT)) {
                    nextSym();
                    if (tokenTypeIs(TokenType.RPARENT)) {
                        nextSym();
                    }
                    else {
                        //TODO:error j
                    }
                    block = parseBlock();
                }
                else {
                    //TODO:error:no LParent
                }
            }
            else {
                //TODO:error no main
            }
        }
        else {
            //TODO:error:no int
        }
        return new MainFuncDef(block);
    }

    public FuncType parseFuncType() throws IOException {
        if (tokenTypeIs(TokenType.VOIDTK)) {
            nextSym();
            return new FuncType("void");
        }
        else if (tokenTypeIs(TokenType.INTTK)) {
            nextSym();
            return new FuncType("int");
        }
        else if (tokenTypeIs(TokenType.CHARTK)) {
            nextSym();
            return new FuncType("char");
        }
        else {
            //TODO:error: wrong func type
            nextSym();
            return null;
        }
    }

    public FuncFParams parseFuncFParams() throws IOException {
        ArrayList<FuncFParam> funcFParams = new ArrayList<>();
        funcFParams.add(parseFuncFParam());
        while (true) {
            if (!tokenTypeIs(TokenType.COMMA)) {
                break;
            }
            nextSym();      //跳过逗号
            funcFParams.add(parseFuncFParam());
        }
        return new FuncFParams(funcFParams);
    }

    public FuncFParam parseFuncFParam() throws IOException {
        BType bType;
        Ident ident;
        bType = parseBType();
        ident = new Ident(getSym());
        nextSym();
        if (tokenTypeIs(TokenType.LBRACK)) {
            nextSym();
            if (tokenTypeIs(TokenType.RBRACK)) {
                nextSym();
            }
            else {
                //TODO:error k
            }
            return new FuncFParam(bType, ident, VarType.Array);
        }
        else {
            return new FuncFParam(bType, ident, VarType.Var);
        }
    }

    public Block parseBlock() throws IOException {
        ArrayList<BlockItem> blockItems = new ArrayList<>();
        if (tokenTypeIs(TokenType.LBRACE)) {
            nextSym();
            while (!tokenTypeIs(TokenType.RBRACE)) {
                blockItems.add(parseBlockItem());
            }
            nextSym();
            return new Block(blockItems);
        }
        else {
            //TODO:error:no LBrace
            return null;
        }
    }

    public BlockItem parseBlockItem() throws IOException {
        if (tokenTypeIs(TokenType.CONSTTK) || tokenTypeIs(TokenType.INTTK) || tokenTypeIs(TokenType.CHARTK)) {
            return new BlockItem(parseDecl());
        }
        else {
            return new BlockItem(parseStmt());
        }
    }

    public Stmt parseStmt() throws IOException {
        LVal lVal;
        ArrayList<Exp> exps = new ArrayList<>();
        Block block;
        Cond cond;
        ArrayList<Stmt> stmts = new ArrayList<>();
        ForStmt forStmtInit;
        ForStmt forStmtLoop;
        StringConst stringConst;
        Stmt.StmtType type;

        //if
        if (tokenTypeIs(TokenType.IFTK)) {
            nextSym();      //feed left parent
            if (tokenTypeIs(TokenType.LPARENT)) {
                nextSym();
                cond = parseCond();
                if (tokenTypeIs(TokenType.RPARENT)) {
                    nextSym();
                }
                else {
                    //TODO:error j
                }
                stmts.add(parseStmt());
                if (tokenTypeIs(TokenType.ELSETK)) {
                    nextSym();
                    stmts.add(parseStmt());
                    type = Stmt.StmtType.Branch_Else;
                }
                else {
                    type = Stmt.StmtType.Branch_If;
                }
                return new Stmt(type, cond, stmts);
            }
            else {
                //TODO:error wrong if
                return null;
            }
        }
        //for
        else if (tokenTypeIs(TokenType.FORTK)) {
            nextSym();
            if (tokenTypeIs(TokenType.LPARENT)) {
                nextSym();
                //try parsing init ForStmt
                if (tokenTypeIs(TokenType.SEMICN)) {
                    forStmtInit = null;
                    nextSym();
                }
                else {
                    forStmtInit = parseForStmt();
                    nextSym();      //skip semicolon
                }
                //try parsing cond
                if (tokenTypeIs(TokenType.SEMICN)) {
                    cond = null;
                    nextSym();
                }
                else {
                    cond = parseCond();
                    nextSym();      //skip semicolon
                }
                //try parsing loop ForStmt
                if (tokenTypeIs(TokenType.RPARENT)) {
                    forStmtLoop = null;
                    nextSym();
                }
                else {
                    forStmtLoop = parseForStmt();
                    nextSym();      //skip right parent
                }
                stmts.add(parseStmt());
                return new Stmt(Stmt.StmtType.Loop_For, forStmtInit, cond, forStmtLoop, stmts);
            }
            else {
                //TODO:error wrong for
                return null;
            }
        }
        //break
        else if (tokenTypeIs(TokenType.BREAKTK)) {
            type = Stmt.StmtType.Break;
            nextSym();
            if (!tokenTypeIs(TokenType.SEMICN)) {
                //TODO:error i
            }
            else {
                nextSym();
            }
            return new Stmt(type);
        }
        //continue
        else if (tokenTypeIs(TokenType.CONTINUETK)) {
            type = Stmt.StmtType.Continue;
            nextSym();
            if (!tokenTypeIs(TokenType.SEMICN)) {
                //TODO: error i
            }
            else {
                nextSym();
            }
            return new Stmt(type);
        }
        //return
        else if (tokenTypeIs(TokenType.RETURNTK)) {
            type = Stmt.StmtType.Return;
            nextSym();
            if (tokenTypeIs(TokenType.SEMICN)) {
                nextSym();      //skip semicolon
            }
            else {
                //TODO:如果getsym() 不属于 FISRT(Exp)，则有缺少分号的错误i
                exps.add(parseExp());
                if (!tokenTypeIs(TokenType.SEMICN)) {
                    //TODO: error i
                }
                else {
                    nextSym();
                }
            }
            return new Stmt(type, exps);
        }
        //printf
        else if (tokenTypeIs(TokenType.PRINTFTK)) {
            type = Stmt.StmtType.Print;
            nextSym();
            if (tokenTypeIs(TokenType.LPARENT)) {
                nextSym();
                stringConst = new StringConst(getSym());
                nextSym();
                while (tokenTypeIs(TokenType.COMMA)) {
                    nextSym();
                    exps.add(parseExp());
                }
                if (tokenTypeIs(TokenType.RPARENT)) {
                    nextSym();
                    if (tokenTypeIs(TokenType.SEMICN)) {
                        nextSym();
                    }
                    else {
                        //TODO:error i
                    }
                }
                else {
                    //TODO:error j
                }
                return new Stmt(type, stringConst, exps);
            }
            else {
                //TODO:error wrong printf
                return null;
            }
        }
        //Block
        else if (tokenTypeIs(TokenType.LBRACE)) {
            type = Stmt.StmtType.Block;
            block = parseBlock();
            return new Stmt(type, block);
        }
        //空语句
        else if (tokenTypeIs(TokenType.SEMICN)) {
            type = Stmt.StmtType.Expression;
            nextSym();
            return new Stmt(type, exps);
        }
        //赋值或不赋值
        else {
            /*FIRST(LVal) = {Ident}
            * FIRST(Exp) = {'(', Ident, Number, Character, Unary Op }
            * */
            Token first = getSym();
            TokenType firstType = first.getTokenType();
            //Exp
            if (firstType.equals(TokenType.LPARENT)
                    || firstType.equals(TokenType.INTCON)
                    || firstType.equals(TokenType.CHRCON)
                    || (firstType.equals(TokenType.PLUS) || firstType.equals(TokenType.MINU) || firstType.equals(TokenType.NOT))
                    /*|| (firstType.equals(TokenType.IDENFR) && !tokenTypeIs(TokenType.ASSIGN, 1))*/) {
                type = Stmt.StmtType.Expression;
                exps.add(parseExp());
                if (tokenTypeIs(TokenType.SEMICN)) {
                    nextSym();
                }
                else {
                    //TODO:error i
                }
                return new Stmt(type, exps);
            }
            //Assignment
            else {
                int foreseeStep = 1;
                int expFlag = 1;
                while (!tokenTypeIs(TokenType.SEMICN, foreseeStep)) {
                    if (tokenTypeIs(TokenType.ASSIGN, foreseeStep)) {
                        expFlag = 0;
                        break;
                    }
                    foreseeStep++;
                }
                if (expFlag == 1) {
                    type = Stmt.StmtType.Expression;
                    exps.add(parseExp());
                    if (tokenTypeIs(TokenType.SEMICN)) {
                        nextSym();
                    }
                    else {
                        //TODO:error i
                    }
                    return new Stmt(type, exps);
                }
                else {
                    lVal = parseLVal();
                    if (tokenTypeIs(TokenType.ASSIGN)) {
                        nextSym();
                        //input assignment
                        if (tokenTypeIs(TokenType.GETINTTK)) {
                            type = Stmt.StmtType.AssignmentInputInt;
                            nextSym();
                            nextSym();      //skip left parent
                            if (tokenTypeIs(TokenType.RPARENT)) {
                                nextSym();
                                if (tokenTypeIs(TokenType.SEMICN)) {
                                    nextSym();
                                } else {
                                    //TODO: error i
                                }
                            } else {
                                //TODO: error j
                            }
                            return new Stmt(type, lVal);
                        } else if (tokenTypeIs(TokenType.GETCHARTK)) {
                            type = Stmt.StmtType.AssignmentInputChar;
                            nextSym();
                            nextSym();      //skip left parent
                            if (tokenTypeIs(TokenType.RPARENT)) {
                                nextSym();
                                if (tokenTypeIs(TokenType.SEMICN)) {
                                    nextSym();
                                } else {
                                    //TODO: error i
                                }
                            } else {
                                //TODO: error j
                            }
                            return new Stmt(type, lVal);
                        }
                        //LVal = Exp
                        else {
                            type = Stmt.StmtType.Assignment;
                            exps.add(parseExp());
                            if (tokenTypeIs(TokenType.SEMICN)) {
                                nextSym();
                            } else {
                                //TODO: error i
                            }
                            return new Stmt(type, lVal, exps);
                        }
                    }
                    else {
                        //TODO: wrong assignment
                        return null;
                    }
                }
            }
        }
    }

    public ForStmt parseForStmt() throws IOException {
        LVal lVal = parseLVal();
        nextSym();      //skip '='
        Exp exp = parseExp();
        return new ForStmt(lVal, exp);
    }

    public Exp parseExp() throws IOException {
        return new Exp(parseAddExp());
    }

    public Cond parseCond() throws IOException {
        return new Cond(parseLOrExp());
    }

    public LVal parseLVal() throws IOException {
        Ident ident = new Ident(getSym());
        ArrayList<Exp> exps = new ArrayList<>();
        nextSym();
        if (tokenTypeIs(TokenType.LBRACK)) {
            nextSym();
            exps.add(parseExp());
            if (tokenTypeIs(TokenType.RBRACK)) {
                nextSym();
            }
            else {
                //TODO:error k
            }
            return new LVal(ident, exps);
        }
        else {
            return new LVal(ident);
        }
    }

    public PrimaryExp parsePrimaryExp() throws IOException {
        Exp exp;
        LVal lVal;
        Number number;
        Character character;
        if (tokenTypeIs(TokenType.LPARENT)) {
            nextSym();
            exp = parseExp();
            if (tokenTypeIs(TokenType.RPARENT)) {
                nextSym();
            }
            else {
                //TODO:error j
            }
            return new PrimaryExp(exp);
        }
        else if (tokenTypeIs(TokenType.INTCON)) {
            return new PrimaryExp(parseNumber());
        }
        else if (tokenTypeIs(TokenType.CHRCON)) {
            return new PrimaryExp(parseCharacter());
        }
        else {
            return new PrimaryExp(parseLVal());
        }
    }

    public Number parseNumber() throws IOException {
        IntConst intConst = new IntConst(getSym());
        nextSym();
        return new Number(intConst);
    }

    public Character parseCharacter() throws IOException {
        CharConst charConst = new CharConst(getSym());
        nextSym();
        return new Character(charConst);
    }

    public UnaryExp parseUnaryExp() throws IOException {
        PrimaryExp primaryExp;
        Ident ident;
        FuncRParams funcRParams;
        UnaryOp unaryOp;
        UnaryExp unaryExp;
        if (tokenTypeIs(TokenType.IDENFR) && tokenTypeIs(TokenType.LPARENT, 1)) {
            ident = new Ident(getSym());
            nextSym();      //skip func name
            nextSym();      //skip left parent
            if (tokenTypeIs(TokenType.RPARENT)) {
                funcRParams = null;
                nextSym();
            }
            else {
                //TODO:如果getsym()不属于FIRST(FuncRParams)，则说明有缺少右小括号的错误
                funcRParams = parseFuncRParams();
                if (tokenTypeIs(TokenType.RPARENT)) {
                    nextSym();
                } else {
                    //TODO:error j
                }
            }
            return new UnaryExp(ident, funcRParams);
        }
        else if (tokenTypeIs(TokenType.PLUS) || tokenTypeIs(TokenType.MINU) || tokenTypeIs(TokenType.NOT)) {
            unaryOp = parseUnaryOp();
            unaryExp = parseUnaryExp();
            return new UnaryExp(unaryOp, unaryExp);
        }
        else {
            primaryExp = parsePrimaryExp();
            return new UnaryExp(primaryExp);
        }
    }

    public UnaryOp parseUnaryOp() throws IOException {
        if (tokenTypeIs(TokenType.PLUS)) {
            nextSym();
            return new UnaryOp("+");
        }
        else if (tokenTypeIs(TokenType.MINU)) {
            nextSym();
            return new UnaryOp("-");
        }
        else if (tokenTypeIs(TokenType.NOT)) {
            nextSym();
            return new UnaryOp("!");
        }
        else return null;
    }

    public FuncRParams parseFuncRParams() throws IOException {
        ArrayList<Exp> exps = new ArrayList<>();
        exps.add(parseExp());
        while (tokenTypeIs(TokenType.COMMA)) {
            nextSym();
            exps.add(parseExp());
        }
        return new FuncRParams(exps);
    }

    public MulExp parseMulExp() throws IOException {
        ArrayList<UnaryExp> unaryExps = new ArrayList<>();
        ArrayList<Token> mulExpOps = new ArrayList<>();
        unaryExps.add(parseUnaryExp());
        while (tokenTypeIs(TokenType.MULT) || tokenTypeIs(TokenType.DIV) || tokenTypeIs(TokenType.MOD)) {
            mulExpOps.add(getSym());
            nextSym();
            unaryExps.add(parseUnaryExp());
        }
        return new MulExp(unaryExps, mulExpOps);
    }

    public AddExp parseAddExp() throws IOException {
        ArrayList<MulExp> mulExps = new ArrayList<>();
        ArrayList<Token> addExpOps = new ArrayList<>();
        mulExps.add(parseMulExp());
        while (tokenTypeIs(TokenType.PLUS) || tokenTypeIs(TokenType.MINU)) {
            addExpOps.add(getSym());
            nextSym();
            mulExps.add(parseMulExp());
        }
        return new AddExp(mulExps, addExpOps);
    }

    public RelExp parseRelExp() throws IOException {
        ArrayList<AddExp> addExps = new ArrayList<>();
        ArrayList<Token> relExpOps = new ArrayList<>();
        addExps.add(parseAddExp());
        while (tokenTypeIs(TokenType.LSS) || tokenTypeIs(TokenType.GRE)
                || tokenTypeIs(TokenType.LEQ) || tokenTypeIs(TokenType.GEQ)) {
            relExpOps.add(getSym());
            nextSym();
            addExps.add(parseAddExp());
        }
        return new RelExp(addExps, relExpOps);
    }

    public EqExp parseEqExp() throws IOException {
        ArrayList<RelExp> relExps = new ArrayList<>();
        ArrayList<Token> eqExpOps = new ArrayList<>();
        relExps.add(parseRelExp());
        while (tokenTypeIs(TokenType.EQL) || tokenTypeIs(TokenType.NEQ)) {
            eqExpOps.add(getSym());
            nextSym();
            relExps.add(parseRelExp());
        }
        return new EqExp(relExps, eqExpOps);
    }

    public LAndExp parseLAndExp() throws IOException {
        ArrayList<EqExp> eqExps = new ArrayList<>();
        ArrayList<Token> lAndExpOps = new ArrayList<>();
        eqExps.add(parseEqExp());
        while (tokenTypeIs(TokenType.AND)) {
            lAndExpOps.add(getSym());
            nextSym();
            eqExps.add(parseEqExp());
        }
        return new LAndExp(eqExps, lAndExpOps);
    }

    public LOrExp parseLOrExp() throws IOException {
        ArrayList<LAndExp> lAndExps = new ArrayList<>();
        ArrayList<Token> lOrExpOps = new ArrayList<>();
        lAndExps.add(parseLAndExp());
        while (tokenTypeIs(TokenType.OR)) {
            lOrExpOps.add(getSym());
            nextSym();
            lAndExps.add(parseLAndExp());
        }
        return new LOrExp(lAndExps, lOrExpOps);
    }

    public ConstExp parseConstExp() throws IOException {
        return new ConstExp(parseAddExp());
    }
}
