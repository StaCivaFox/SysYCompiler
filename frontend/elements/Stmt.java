package frontend.elements;

import frontend.Token;

import java.util.ArrayList;

public class Stmt extends SyntaxNode {
    public enum StmtType {
        Assignment,
        Expression,
        Block,
        Branch_If,
        Branch_Else,
        Loop_For,
        Break,
        Continue,
        Return,
        AssignmentInputInt,
        AssignmentInputChar,
        Print
    }

    public LVal lVal;
    public ArrayList<Exp> exps;
    public Block block;
    public Cond cond;
    public ArrayList<Stmt> stmts;
    public ForStmt forStmtInit;
    public ForStmt forStmtLoop;
    public StringConst stringConst;
    public StmtType stmtType;

    public Token breakToken;
    public Token continueToken;

    public Token returnToken;
    public Token printfToken;

    //根据需要补充构造函数
    public Stmt(StmtType stmtType, LVal lVal, ArrayList<Exp> exps) {
        this.lVal = lVal;
        this.exps = exps;
        this.stmtType = stmtType;
        childrenNodes.add(lVal);
        childrenNodes.addAll(exps);
    }

    public Stmt(StmtType stmtType, ArrayList<Exp> exps) {
        this.exps = exps;
        this.stmtType = stmtType;
        childrenNodes.addAll(exps);
    }

    public Stmt(StmtType stmtType, Block block) {
        this.block = block;
        this.stmtType = stmtType;
        childrenNodes.add(block);
    }

    public Stmt(StmtType stmtType, Cond cond, ArrayList<Stmt> stmts) {
        this.cond = cond;
        this.stmts = stmts;
        this.stmtType = stmtType;
        childrenNodes.add(cond);
        childrenNodes.addAll(stmts);
    }

    public Stmt(StmtType stmtType, ForStmt forStmtInit, Cond cond, ForStmt forStmtLoop,
                ArrayList<Stmt> stmts) {
        this.stmtType = stmtType;
        this.stmts = stmts;
        this.forStmtInit = forStmtInit;
        this.cond = cond;
        this.forStmtLoop = forStmtLoop;
        if (forStmtInit != null) {
            childrenNodes.add(forStmtInit);
        }
        if (cond != null) {
            childrenNodes.add(cond);
        }
        if (forStmtLoop != null) {
            childrenNodes.add(forStmtLoop);
        }
        childrenNodes.addAll(stmts);
    }

    public Stmt(StmtType stmtType, Token token) {
        this.stmtType = stmtType;
        if (stmtType.equals(StmtType.Break)) {
            this.breakToken = token;
        }
        else this.continueToken = token;
    }

    public Stmt(StmtType stmtType, ArrayList<Exp> exps, Token token) {
        this.stmtType = stmtType;
        this.exps = exps;
        this.returnToken = token;
        childrenNodes.addAll(exps);
    }

    public Stmt(StmtType stmtType, LVal lVal) {
        this.lVal = lVal;
        this.stmtType = stmtType;
        childrenNodes.add(lVal);
    }

    public Stmt(StmtType stmtType, StringConst stringConst, ArrayList<Exp> exps, Token token) {
        this.stringConst = stringConst;
        this.exps = exps;
        this.stmtType = stmtType;
        this.printfToken = token;
        childrenNodes.add(stringConst);
        childrenNodes.addAll(exps);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        switch (stmtType) {
            case Assignment:
                sb.append(lVal.toString());
                sb.append("ASSIGN =\n");
                sb.append(exps.get(0).toString());
                sb.append("SEMICN ;\n");
                break;
            case Expression:
                if (exps != null && !exps.isEmpty()) {
                    sb.append(exps.get(0).toString());
                }
                sb.append("SEMICN ;\n");
                break;
            case Block:
                sb.append(block.toString());
                break;
            case Branch_If:
            case Branch_Else:
                sb.append("IFTK if\n");
                sb.append("LPARENT (\n");
                sb.append(cond.toString());
                sb.append("RPARENT )\n");
                sb.append(stmts.get(0).toString());
                if (stmtType.equals(StmtType.Branch_Else)) {
                    sb.append("ELSETK else\n");
                    sb.append(stmts.get(1).toString());
                }
                break;
            case Loop_For:
                sb.append("FORTK for\n");
                sb.append("LPARENT (\n");
                if (forStmtInit != null)
                    sb.append(forStmtInit.toString());
                sb.append("SEMICN ;\n");
                if (cond != null)
                    sb.append(cond.toString());
                sb.append("SEMICN ;\n");
                if (forStmtLoop != null)
                    sb.append(forStmtLoop.toString());
                sb.append("RPARENT )\n");
                sb.append(stmts.get(0).toString());
                break;
            case Break:
                sb.append("BREAKTK break\n");
                sb.append("SEMICN ;\n");
                break;
            case Continue:
                sb.append("CONTINUETK continue\n");
                sb.append("SEMICN ;\n");
                break;
            case Return:
                sb.append("RETURNTK return\n");
                if (exps != null && !exps.isEmpty()) {
                    sb.append(exps.get(0).toString());
                }
                sb.append("SEMICN ;\n");
                break;
            case AssignmentInputInt:
                sb.append(lVal.toString());
                sb.append("ASSIGN =\n");
                sb.append("GETINTTK getint\n");
                sb.append("LPARENT (\n");
                sb.append("RPARENT )\n");
                sb.append("SEMICN ;\n");
                break;
            case AssignmentInputChar:
                sb.append(lVal.toString());
                sb.append("ASSIGN =\n");
                sb.append("GETCHARTK getchar\n");
                sb.append("LPARENT (\n");
                sb.append("RPARENT )\n");
                sb.append("SEMICN ;\n");
                break;
            case Print:
                sb.append("PRINTFTK printf\n");
                sb.append("LPARENT (\n");
                sb.append(stringConst.toString());
                for (Exp exp : exps) {
                    sb.append("COMMA ,\n");
                    sb.append(exp.toString());
                }
                sb.append("RPARENT )\n");
                sb.append("SEMICN ;\n");
                break;
        }
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
