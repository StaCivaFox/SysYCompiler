package middle;

import frontend.elements.*;
import utils.ErrorReporter;

import java.util.ArrayList;


public class Visitor {
    public CompUnit compUnit;
    public SymbolTable currentSymbolTable;
    public int fieldCnt = 0;

    //TODO:中间代码数据结构

    public Visitor(CompUnit compUnit) {
        fieldCnt++;
        this.compUnit = compUnit;
        this.currentSymbolTable = new SymbolTable(fieldCnt, null);
    }

    public void visitCompUnit(CompUnit compUnit) {
        for (Decl decl : compUnit.decls) {
            visitDecl(decl);
        }
        for (FuncDef funcDef : compUnit.funcDefs) {
            visitFuncDef(funcDef);
        }
        visitMainFuncDef(compUnit.mainFuncDef);
    }

    public void visitDecl(Decl decl) {
        if (decl.constDecl != null)
            visitConstDecl(decl.constDecl);
        else visitVarDecl(decl.varDecl);
    }

    public void visitConstDecl(ConstDecl constDecl) {
        for (ConstDef constDef : constDecl.constDefs) {
            visitConstDef(constDef, constDecl.bType);
        }
    }

    public void visitConstDef(ConstDef constDef, BType bType) {
        Symbol symbol;
        //普通变量
        if (constDef.constExps.isEmpty()) {
            SymbolType symbolType;
            if (bType.type.equals("int")) {
                symbolType = SymbolType.ConstInt;
            }
            else symbolType = SymbolType.ConstChar;
            symbol = new VariableSymbol(currentSymbolTable.id,
                    constDef.ident.name(), constDef.ident.lineno(), symbolType, bType.type, null/*TODO*/);
        }
        //数组
        else {
            SymbolType symbolType;
            if (bType.type.equals("int")) {
                symbolType = SymbolType.ConstIntArray;
            }
            else symbolType = SymbolType.ConstCharArray;
            //TODO:要储存数组长度信息吗？
            symbol = new ArraySymbol(currentSymbolTable.id,
                    constDef.ident.name(), constDef.ident.lineno(), symbolType, bType.type, null/*TODO*/, 1);
            visitConstExp(constDef.constExps.get(0));
        }
        if (!currentSymbolTable.addSymbol(symbol)) {
            //TODO:重定义错误
            ErrorReporter.getInstance().addError(constDef.ident.lineno(), "b");
            return;
        }
        if (constDef.constInitVal.constExps != null) {
            for (ConstExp constExp : constDef.constInitVal.constExps) {
                visitConstExp(constExp);
            }
        }
    }

    public void visitVarDecl(VarDecl varDecl) {
        for (VarDef varDef : varDecl.varDefs) {
            visitVarDef(varDef, varDecl.bType);
        }
    }

    public void visitVarDef(VarDef varDef, BType bType) {
        Symbol symbol;
        //普通变量
        if (varDef.constExps.isEmpty()) {
            SymbolType symbolType;
            if (bType.type.equals("int")) {
                symbolType = SymbolType.Int;
            }
            else {
                symbolType = SymbolType.Char;
            }
            symbol = new VariableSymbol(currentSymbolTable.id,
                    varDef.ident.name(), varDef.ident.lineno(), symbolType, bType.type, null/*TODO*/);
        }
        //数组
        else {
            SymbolType symbolType;
            if (bType.type.equals("int")) {
                symbolType = SymbolType.IntArray;
            }
            else symbolType = SymbolType.CharArray;
            symbol = new ArraySymbol(currentSymbolTable.id,
                    varDef.ident.name(), varDef.ident.lineno(), symbolType, bType.type, null/*TODO*/, 1);
            visitConstExp(varDef.constExps.get(0));
        }
        if (!currentSymbolTable.addSymbol(symbol)) {
            //TODO:重定义错误
            ErrorReporter.getInstance().addError(varDef.ident.lineno(), "b");
            return;
        }
        if (varDef.initVal != null && varDef.initVal.exps != null) {
            for (Exp exp : varDef.initVal.exps) {
                visitExp(exp);
            }
        }
    }

    public void visitFuncDef(FuncDef funcDef) {
        SymbolType symbolType;
        if (funcDef.funcType.type.equals("void")) {
            symbolType = SymbolType.VoidFunc;
        }
        else if (funcDef.funcType.type.equals("int")) {
            symbolType = SymbolType.IntFunc;
        }
        else symbolType = SymbolType.CharFunc;
        FuncSymbol funcSymbol = new FuncSymbol(currentSymbolTable.id,
                funcDef.ident.name(), funcDef.ident.lineno(), symbolType, null, 0, null);
        if (!currentSymbolTable.addSymbol(funcSymbol)) {
            //TODO:重定义错误b
            ErrorReporter.getInstance().addError(funcDef.ident.lineno(), "b");
            //return;
        }
        //形参位于新的作用域
        currentSymbolTable = currentSymbolTable.newTable(fieldCnt);
        fieldCnt++;
        ArrayList<Symbol> funcFParams = new ArrayList<>();
        if (funcDef.funcFParams != null) {
            for (FuncFParam funcFParam : funcDef.funcFParams.funcFParams) {
                SymbolType fpSymbolType;
                //普通变量
                if (funcFParam.varType.equals(VarType.Var)) {
                    if (funcFParam.bType.type.equals("int")) fpSymbolType = SymbolType.Int;
                    else fpSymbolType = SymbolType.Char;
                    Symbol fpSymbol = new VariableSymbol(currentSymbolTable.id,
                            funcFParam.ident.name(), funcFParam.ident.lineno(), fpSymbolType, funcFParam.bType.type, null);
                    funcFParams.add(fpSymbol);
                    if (!currentSymbolTable.addSymbol(fpSymbol)) {
                        //TODO:重定义错误
                        ErrorReporter.getInstance().addError(fpSymbol.lineno, "b");
                        continue;
                    }
                }
                //数组
                else if (funcFParam.varType.equals(VarType.Array)) {
                    if (funcFParam.bType.type.equals("int")) fpSymbolType = SymbolType.IntArray;
                    else fpSymbolType = SymbolType.CharArray;
                    Symbol fpSymbol = new ArraySymbol(currentSymbolTable.id,
                            funcFParam.ident.name(), funcFParam.ident.lineno(), fpSymbolType, funcFParam.bType.type, null, 1);
                    funcFParams.add(fpSymbol);
                    if (!currentSymbolTable.addSymbol(fpSymbol)) {
                        //TODO:重定义错误
                        ErrorReporter.getInstance().addError(fpSymbol.lineno, "b");
                        continue;
                    }
                }
            }
        }
        funcSymbol.setFParams(funcFParams);
        //在调用visitBlock()之前，总是已经修改了currentSymbolTable，进入了新的作用域
        visitBlock(funcDef.block, funcSymbol, false);
        if (!funcDef.funcType.type.equals("void") && !funcDef.hasReturn()) {
            //TODO:无return错误g
            ErrorReporter.getInstance().addError(funcDef.endLine, "g");
        }
        currentSymbolTable = currentSymbolTable.back();
    }

    public void visitMainFuncDef(MainFuncDef mainFuncDef) {
        currentSymbolTable = currentSymbolTable.newTable(fieldCnt);
        fieldCnt++;
        visitBlock(mainFuncDef.block, null, false);
        if (!mainFuncDef.hasReturn()) {
            //TODO:无return错误g
            ErrorReporter.getInstance().addError(mainFuncDef.endLine, "g");
        }
        currentSymbolTable = currentSymbolTable.back();
    }

    public void visitBlock(Block block, FuncSymbol funcSymbol, boolean inLoop) {
        for (BlockItem blockItem : block.blockItems) {
            visitBlockItem(blockItem, funcSymbol, inLoop);
        }
    }


    public void visitBlockItem(BlockItem blockItem, FuncSymbol funcSymbol, boolean inLoop) {
        if (blockItem.decl != null) visitDecl(blockItem.decl);
        else visitStmt(blockItem.stmt, funcSymbol, inLoop);
    }

    public void visitAssignment(Stmt stmt) {
        visitLVal(stmt.lVal);
        Symbol identSymbol = currentSymbolTable.getSymbol(stmt.lVal.ident.name());
        if (identSymbol != null && (identSymbol.type.equals(SymbolType.ConstInt) || identSymbol.type.equals(SymbolType.ConstChar)
                || identSymbol.type.equals(SymbolType.ConstIntArray) || identSymbol.type.equals(SymbolType.ConstCharArray))) {
            //TODO:对常量赋值错误h
            ErrorReporter.getInstance().addError(stmt.lVal.ident.lineno(), "h");
            return;
        }
        //TODO:中间代码生成作业
        //通过identSymbol.value在内存中定位对象，对Exp求值，并赋值
        visitExp(stmt.exps.get(0));
    }

    public void visitExpressions(Stmt stmt) {
        //TODO:中间代码生成作业
        if (stmt.exps != null && !stmt.exps.isEmpty()) visitExp(stmt.exps.get(0));
    }


    public void visitIfStmt(Stmt stmt, FuncSymbol funcSymbol, boolean inLoop) {
        visitCond(stmt.cond);
        visitStmt(stmt.stmts.get(0), funcSymbol, inLoop);
        if (stmt.stmts.size() > 1) {
            visitStmt(stmt.stmts.get(1), funcSymbol, inLoop);
        }
        //TODO:中间代码生成作业
    }

    public void visitLoop(Stmt stmt, FuncSymbol funcSymbol) {
        if (stmt.forStmtInit != null) visitForStmt(stmt.forStmtInit);
        if (stmt.cond != null) visitCond(stmt.cond);
        if (stmt.forStmtLoop != null) visitForStmt(stmt.forStmtLoop);
        visitStmt(stmt.stmts.get(0), funcSymbol, true);
    }

    public void visitBreak(Stmt stmt, boolean inLoop) {
        if (!inLoop) {
            //TODO:非循环块中出现break错误m
            ErrorReporter.getInstance().addError(stmt.breakToken.getLineno(), "m");
            return;
        }
    }

    public void visitContinue(Stmt stmt, boolean inLoop) {
        if (!inLoop) {
            //TODO:非循环块中出现continue错误m
            ErrorReporter.getInstance().addError(stmt.continueToken.getLineno(), "m");
            return;
        }
    }

    public void visitReturn(Stmt stmt, FuncSymbol funcSymbol) {
        for (Exp exp : stmt.exps) {
            visitExp(exp);
        }
        if (funcSymbol == null) {
            return;
        }
        if (funcSymbol.type.equals(SymbolType.VoidFunc) && !stmt.exps.isEmpty()) {
            //TODO:return表达式不匹配错误f
            ErrorReporter.getInstance().addError(stmt.returnToken.getLineno(), "f");
            return;
        }
    }

    public void visitIntInput(Stmt stmt) {
        visitLVal(stmt.lVal);
        Symbol identSymbol = currentSymbolTable.getSymbol(stmt.lVal.ident.name());
        if (identSymbol != null && (identSymbol.type.equals(SymbolType.ConstInt) || identSymbol.type.equals(SymbolType.ConstChar)
                || identSymbol.type.equals(SymbolType.ConstIntArray) || identSymbol.type.equals(SymbolType.ConstCharArray))) {
            //TODO:对常量赋值错误h
            ErrorReporter.getInstance().addError(stmt.lVal.ident.lineno(), "h");
            return;
        }
        //TODO:中间代码生成作业
    }

    public void visitCharInput(Stmt stmt) {
        visitLVal(stmt.lVal);
        Symbol identSymbol = currentSymbolTable.getSymbol(stmt.lVal.ident.name());
        if (identSymbol != null && (identSymbol.type.equals(SymbolType.ConstInt) || identSymbol.type.equals(SymbolType.ConstChar)
                || identSymbol.type.equals(SymbolType.ConstIntArray) || identSymbol.type.equals(SymbolType.ConstCharArray))) {
            //TODO:对常量赋值错误h
            ErrorReporter.getInstance().addError(stmt.lVal.ident.lineno(), "h");
            return;
        }
        //TODO:中间代码生成作业
    }

    public void visitPrint(Stmt stmt) {
        if (stmt.stringConst.paramNum() != stmt.exps.size()) {
            //TODO:printf参数个数不匹配错误l
            ErrorReporter.getInstance().addError(stmt.printfToken.getLineno(), "l");
            return;
        }
        for (Exp exp : stmt.exps) {
            visitExp(exp);
        }
    }

    public void visitLVal(LVal lVal) {
        Symbol identSymbol = currentSymbolTable.getSymbol(lVal.ident.name());
        if (identSymbol == null) {
            ErrorReporter.getInstance().addError(lVal.ident.lineno(), "c");
            return;
        }

        /*if (identSymbol instanceof ArraySymbol) {
            visitExp(lVal.exps.get(0));
        }*/
        if (lVal.exps != null && !lVal.exps.isEmpty()) {
            visitExp(lVal.exps.get(0));
        }
    }

    public void visitStmt(Stmt stmt, FuncSymbol funcSymbol, boolean inLoop) {
        if (stmt.stmtType.equals(Stmt.StmtType.Assignment)) visitAssignment(stmt);
        else if (stmt.stmtType.equals(Stmt.StmtType.Expression)) visitExpressions(stmt);
        else if (stmt.stmtType.equals(Stmt.StmtType.Block)) {
            currentSymbolTable = currentSymbolTable.newTable(fieldCnt);
            fieldCnt++;
            visitBlock(stmt.block, funcSymbol, inLoop);
            currentSymbolTable = currentSymbolTable.back();
        }
        else if (stmt.stmtType.equals(Stmt.StmtType.Branch_If)) visitIfStmt(stmt, funcSymbol, inLoop);
        else if (stmt.stmtType.equals(Stmt.StmtType.Branch_Else)) visitIfStmt(stmt, funcSymbol, inLoop);
        else if (stmt.stmtType.equals(Stmt.StmtType.Loop_For)) visitLoop(stmt, funcSymbol);
        else if (stmt.stmtType.equals(Stmt.StmtType.Break)) visitBreak(stmt, inLoop);
        else if (stmt.stmtType.equals(Stmt.StmtType.Continue)) visitContinue(stmt, inLoop);
        else if (stmt.stmtType.equals(Stmt.StmtType.Return)) visitReturn(stmt, funcSymbol);
        else if (stmt.stmtType.equals(Stmt.StmtType.AssignmentInputInt)) visitIntInput(stmt);
        else if (stmt.stmtType.equals(Stmt.StmtType.AssignmentInputChar)) visitCharInput(stmt);
        else if (stmt.stmtType.equals(Stmt.StmtType.Print)) visitPrint(stmt);
    }

    public void visitForStmt(ForStmt forStmt) {
        visitLVal(forStmt.lVal);
        Symbol identSymbol = currentSymbolTable.getSymbol(forStmt.lVal.ident.name());
        if (identSymbol != null && (identSymbol.type.equals(SymbolType.ConstInt) || identSymbol.type.equals(SymbolType.ConstChar)
                || identSymbol.type.equals(SymbolType.ConstIntArray) || identSymbol.type.equals(SymbolType.ConstCharArray))) {
            //TODO:对常量赋值错误h
            ErrorReporter.getInstance().addError(forStmt.lVal.ident.lineno(), "h");
            return;
        }
        //TODO:中间代码生成作业
    }

    public Value visitExp(Exp exp) {
        //TODO:中间代码生成作业
        return visitAddExp(exp.addExp);
    }

    public void visitCond(Cond cond) {
        //TODO:中间代码生成作业
        visitLOrExp(cond.lOrExp);
    }

    public Value visitAddExp(AddExp addExp) {
        //TODO:中间代码生成作业 求值
        for (MulExp mulExp : addExp.mulExps) {
            visitMulExp(mulExp);
        }
        return null;
    }

    public Value visitMulExp(MulExp mulExp) {
        //TODO:中间代码生成作业 求值
        for (UnaryExp unaryExp : mulExp.unaryExps) {
            visitUnaryExp(unaryExp);
        }
        return null;
    }

    public Value visitUnaryExp(UnaryExp unaryExp) {
        if (unaryExp.primaryExp != null) return visitPrimaryExp(unaryExp.primaryExp);
        else if (unaryExp.ident != null) {
            Symbol identSymbol = currentSymbolTable.getSymbol(unaryExp.ident.name());
            if (identSymbol == null) {
                //TODO:变量未定义错误c
                ErrorReporter.getInstance().addError(unaryExp.ident.lineno(), "c");
                return null;
            }
            if (identSymbol instanceof FuncSymbol) {
                FuncSymbol funcIdentSymbol = (FuncSymbol) identSymbol;
                if (unaryExp.funcRParams != null) {
                    if (funcIdentSymbol.fParamCnt != unaryExp.funcRParams.exps.size()) {
                        //TODO:形实参个数不匹配错误d
                        ErrorReporter.getInstance().addError(unaryExp.ident.lineno(), "d");
                        return null;
                    }
                    for (Exp exp : unaryExp.funcRParams.exps) {
                        visitExp(exp);
                    }
                    for (int i = 0; i < funcIdentSymbol.fParamCnt; i++) {
                        SymbolType fpType = funcIdentSymbol.fParamList.get(i).type;
                        SymbolType rpType = unaryExp.funcRParams.exps.get(i).getType(currentSymbolTable);
                        boolean fpIsVar = fpType.equals(SymbolType.Int) || fpType.equals(SymbolType.Char);
                        boolean fpIsArray = fpType.equals(SymbolType.IntArray) || fpType.equals(SymbolType.CharArray);
                        if (rpType != null
                                && ((fpIsVar && (rpType.equals(SymbolType.IntArray) || rpType.equals(SymbolType.CharArray)))
                                || (fpIsArray && !(rpType.equals(SymbolType.IntArray) || rpType.equals(SymbolType.CharArray)))
                                || (fpType.equals(SymbolType.CharArray) && rpType.equals(SymbolType.IntArray))
                                || (fpType.equals(SymbolType.IntArray) && rpType.equals(SymbolType.CharArray)))) {
                            //TODO:形实参类型不匹配错误e
                            ErrorReporter.getInstance().addError(unaryExp.ident.lineno(), "e");
                            return null;
                        }
                    }
                }
                else {
                    if (funcIdentSymbol.fParamCnt != 0) {
                        //TODO:形实参个数不匹配错误d
                        ErrorReporter.getInstance().addError(unaryExp.ident.lineno(), "d");
                        return null;
                    }
                }
            }
            //TODO:中间代码生成作业 求值
            return null;
        }
        else {
            //TODO:中间代码生成作业 求值
            return visitUnaryExp(unaryExp.unaryExp);
        }
    }

    public Value visitPrimaryExp(PrimaryExp primaryExp) {
        if (primaryExp.exp != null) return visitExp(primaryExp.exp);
        else if (primaryExp.lVal != null) {
            visitLVal(primaryExp.lVal);
            //TODO:中间代码生成作业 求值
            return null;
        }
        else if (primaryExp.number != null) {
            //TODO:中间代码生成作业 求值
            return null;
        }
        else {
            //TODO:中间代码生成作业 求值
            return null;
        }
    }

    public void visitLOrExp(LOrExp lOrExp) {
        for (LAndExp lAndExp : lOrExp.lAndExps) {
            visitLAndExp(lAndExp);
        }
    }

    public Value visitLAndExp(LAndExp lAndExp) {
        for (EqExp eqExp : lAndExp.eqExps) {
            visitEqExp(eqExp);
        }
        //TODO:中间代码生成
        return null;
    }

    public Value visitEqExp(EqExp eqExp) {
        for (RelExp relExp : eqExp.relExps) {
            visitRelExp(relExp);
        }
        //TODO:中间代码生成
        return null;
    }

    public Value visitRelExp(RelExp relExp) {
        for (AddExp addExp : relExp.addExps) {
            visitAddExp(addExp);
        }
        //TODO:中间代码生成
        return null;
    }

    public Value visitConstExp(ConstExp constExp) {
        return visitAddExp(constExp.addExp);
    }

}
