package middle;

import frontend.elements.*;
import middle.IR.*;
import middle.IR.Instructions.*;
import middle.IR.Module;
import middle.IR.Type.ArrayType;
import middle.IR.Type.PointerType;
import middle.IR.Type.Type;
import org.jetbrains.annotations.NotNull;
import utils.ErrorReporter;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Visitor {
    public CompUnit compUnit;
    public SymbolTable currentSymbolTable;
    public int fieldCnt = 0;

    //中间代码数据结构
    public Module currentModule = new Module("Top");
    public Function currentFunction = null;
    public BasicBlock currentBasicBlock = null;
    public int stringConstCnt = 0;

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
        //局部变量
        if (currentSymbolTable.fatherTable != null) {
            //普通变量
            if (constDef.constExps.isEmpty()) {
                //创建alloca指令分配空间，创建新符号，并尝试加入符号表
                SymbolType symbolType;
                Instruction allocaInst;
                if (bType.type.equals("int")) {
                    symbolType = SymbolType.ConstInt;
                    allocaInst = new AllocaInst(currentModule.context.getInt32Ty());
                    //currentBasicBlock.addInstruction(allocaInst);
                } else {
                    symbolType = SymbolType.ConstChar;
                    allocaInst = new AllocaInst(currentModule.context.getInt8Ty());
                    //currentBasicBlock.addInstruction(allocaInst);
                }
                symbol = new VariableSymbol(currentSymbolTable.id,
                        constDef.ident.name(), constDef.ident.lineno(), symbolType, bType.type, allocaInst);
                if (!currentSymbolTable.addSymbol(symbol)) {
                    ErrorReporter.getInstance().addError(constDef.ident.lineno(), "b");
                    return;
                }
                currentBasicBlock.addInstruction(allocaInst);
                //有初值，则去计算初值，并生成store指令；注意计算指令已经在visitConstExp过程中完成插入了
                if (constDef.constInitVal.constExps != null && !constDef.constInitVal.constExps.isEmpty()) {
                    Constant constInitVal = (Constant) visitConstExp(constDef.constInitVal.constExps.get(0));
                    //返回的结果是i32，需要进行类型转换
                    if (symbolType.equals(SymbolType.ConstInt)) {
                        Instruction storeInst = new StoreInst(constInitVal, allocaInst);
                        currentBasicBlock.addInstruction(storeInst);
                    }
                    else {
                        //Instruction truncInst = new TruncInst(constInitVal, currentModule.context.getInt8Ty());
                        Value truncedValue = trunc(constInitVal);
                        Instruction storeInst = new StoreInst(truncedValue, allocaInst);
                        currentBasicBlock.addInstruction(storeInst);
                    }
                }
            }
            //数组
            else {
                //创建符号，并尝试加入符号表
                SymbolType symbolType;
                Instruction allocaInst;
                ArrayType arrayType;
                if (bType.type.equals("int")) {
                    symbolType = SymbolType.ConstIntArray;
                    //计算数组长度
                    Constant arrayLength= (Constant) visitConstExp(constDef.constExps.get(0));
                    //为数组分配空间
                    arrayType =
                            currentModule.context.getArrayType(currentModule.context.getInt32Ty(), arrayLength.getIntValue());
                    //currentBasicBlock.addInstruction(allocaInst);
                } else {
                    symbolType = SymbolType.ConstCharArray;
                    //计算数组长度
                    Constant arrayLength= (Constant) visitConstExp(constDef.constExps.get(0));
                    //为数组分配空间
                    arrayType =
                            currentModule.context.getArrayType(currentModule.context.getInt8Ty(), arrayLength.getIntValue());
                    //currentBasicBlock.addInstruction(allocaInst);
                }
                allocaInst = new AllocaInst(arrayType);
                symbol = new ArraySymbol(currentSymbolTable.id,
                        constDef.ident.name(), constDef.ident.lineno(), symbolType, bType.type, allocaInst, 1);
                if (!currentSymbolTable.addSymbol(symbol)) {
                    ErrorReporter.getInstance().addError(constDef.ident.lineno(), "b");
                    return;
                }
                currentBasicBlock.addInstruction(allocaInst);
                //有初值，则去计算初值，并生成store指令；注意计算指令已经在visitConstExp过程中完成插入了
                //对局部数组赋初值的方法是，对每一个位置进行getelementptr-store操作

                //字符数组部分初始化时，需要将未完全初始化的部分b补0
                //TODO:会出现int[5]="hello"的情况吗？
                if (constDef.constInitVal.stringConst != null) {
                    String strInitval = constDef.constInitVal.stringConst.token.getContent();
                    //构建初值时去掉双引号
                    strInitval = strInitval.substring(1, strInitval.length() - 1);
                    //末尾补0
                    if (calcStringConstLength(strInitval) < arrayType.size) {
                        strInitval = strInitval + "0".repeat(Math.max(0, (arrayType.size - calcStringConstLength(strInitval))));
                    }
                    for (int i = 0; i < calcStringConstLength(strInitval); i++) {
                        Instruction getElementPtr =
                                new GetElementPtr(allocaInst, new Constant(currentModule.context.getInt32Ty(), String.valueOf(i)));
                        currentBasicBlock.addInstruction(getElementPtr);
                        Constant ithChar;
                        if (strInitval.charAt(i) == '\\') {
                            String escapeChar = String.valueOf(strInitval.charAt(i) + strInitval.charAt(i + 1));
                            ithChar = new Constant(currentModule.context.getInt8Ty(), escapeChar);
                            i++;
                        }
                        else {
                            ithChar = new Constant(currentModule.context.getInt8Ty(), String.valueOf(strInitval.charAt(i)));
                        }
                        Instruction storeInst =
                                new StoreInst(ithChar, getElementPtr);
                        currentBasicBlock.addInstruction(storeInst);
                    }
                }
                else {
                    for (int i = 0; i < constDef.constInitVal.constExps.size(); i++) {
                        Instruction getElementPtr =
                                new GetElementPtr(allocaInst, new Constant(currentModule.context.getInt32Ty(), String.valueOf(i)));
                        currentBasicBlock.addInstruction(getElementPtr);
                        //注意类型转换
                        if (symbolType.equals(SymbolType.ConstIntArray)) {
                            Instruction storeInst = new StoreInst((Constant) visitConstExp(constDef.constInitVal.constExps.get(i)), getElementPtr);
                            currentBasicBlock.addInstruction(storeInst);
                        } else {
                            Constant constInitVal = (Constant) visitConstExp(constDef.constInitVal.constExps.get(i));
                            //Instruction truncInst = new TruncInst(constInitVal, currentModule.context.getInt8Ty());
                            Value truncedValue = trunc(constInitVal);
                            Instruction storeInst = new StoreInst(truncedValue, getElementPtr);
                            currentBasicBlock.addInstruction(storeInst);
                        }
                    }
                    //如果是字符数组，且使用大括号的形式赋值，则在这里写末尾补0的逻辑
                    if (symbolType.equals(SymbolType.ConstCharArray) && constDef.constInitVal.constExps.size() < arrayType.size) {
                        int zeroCnt = arrayType.size - constDef.constInitVal.constExps.size();
                        for (int i = 0; i < zeroCnt; i++) {
                            Constant zero = new Constant(currentModule.context.getInt8Ty(), "0");
                            Instruction getElementPtr =
                                    new GetElementPtr(allocaInst, new Constant(currentModule.context.getInt32Ty(), String.valueOf(constDef.constInitVal.constExps.size() + i)));
                            currentBasicBlock.addInstruction(getElementPtr);
                            Instruction storeInst = new StoreInst(zero, getElementPtr);
                            currentBasicBlock.addInstruction(storeInst);
                        }
                    }
                }
            }
        }
        //全局变量
        else {
            //普通变量
            if (constDef.constExps.isEmpty()) {
                //尝试创建符号，加入符号表；对于全局变量，创建即求值
                //常量一定有初值constInitVal
                SymbolType symbolType;
                GlobalVariable globalVariable;
                if (bType.type.equals("int")) {
                    symbolType = SymbolType.ConstInt;
                    Constant globalVariableInit = (Constant) visitConstExp(constDef.constInitVal.constExps.get(0));
                    ArrayList<Value> globalVariableInits = new ArrayList<>();
                    globalVariableInits.add(globalVariableInit);
                    globalVariable =
                            new GlobalVariable(constDef.ident.name(), currentModule.context.getPointerType(currentModule.context.getInt32Ty()), globalVariableInits, false, true);
                }
                else {
                    symbolType = SymbolType.ConstChar;
                    Constant globalVariableInit = (Constant) visitConstExp(constDef.constInitVal.constExps.get(0));
                    //类型转换
                    //Instruction truncInst = new TruncInst(globalVariableInit, currentModule.context.getInt8Ty());
                    //注意这里需要直接求出trunc后的值，而不能插入指令
                    Value truncedValue = trunc(globalVariableInit);
                    ArrayList<Value> globalVariableInits = new ArrayList<>();
                    globalVariableInits.add(truncedValue);
                    globalVariable =
                            new GlobalVariable(constDef.ident.name(), currentModule.context.getPointerType(currentModule.context.getInt8Ty()), globalVariableInits, false, true);
                }
                symbol = new VariableSymbol(currentSymbolTable.id, constDef.ident.name(), constDef.ident.lineno(), symbolType, bType.type, globalVariable);
                if (!currentSymbolTable.addSymbol(symbol)) {
                    ErrorReporter.getInstance().addError(constDef.ident.lineno(), "b");
                    return;
                }
                currentModule.addGlobalVariable(globalVariable);
            }
            //数组
            else {
                //尝试创建符号，加入符号表；对于全局变量，创建即求值
                SymbolType symbolType;
                ArrayType arrayType;
                GlobalVariable globalVariable;
                if (bType.type.equals("int")) {
                    symbolType = SymbolType.ConstIntArray;
                    ArrayList<Value> globalVariableInits = new ArrayList<>();
                    //计算数组长度
                    Constant arrayLength= (Constant) visitConstExp(constDef.constExps.get(0));
                    arrayType =
                            currentModule.context.getArrayType(currentModule.context.getInt32Ty(), arrayLength.getIntValue());

                    //计算初值
                    for (ConstExp constExp : constDef.constInitVal.constExps) {
                        Constant globalVariableInit = (Constant) visitConstExp(constExp);
                        globalVariableInits.add(globalVariableInit);
                    }
                    //末尾补0
                    if (constDef.constInitVal.constExps.size() < arrayType.size) {
                        int zeroCnt = arrayType.size - constDef.constInitVal.constExps.size();
                        for (int i = 0; i < zeroCnt; i++) {
                            Constant zero = new Constant(currentModule.context.getInt32Ty(), "0");
                            globalVariableInits.add(zero);
                        }
                    }
                    globalVariable =
                            new GlobalVariable(constDef.ident.name(), currentModule.context.getPointerType(arrayType), globalVariableInits, false, true);
                }
                else {
                    symbolType = SymbolType.ConstCharArray;
                    ArrayList<Value> globalVariableInits = new ArrayList<>();
                    //计算数组长度
                    Constant arrayLength = (Constant) visitConstExp(constDef.constExps.get(0));
                    arrayType =
                            currentModule.context.getArrayType(currentModule.context.getInt8Ty(), arrayLength.getIntValue());
                    //计算初值
                    if (constDef.constInitVal.stringConst != null) {
                        //构建初值时，去掉双引号
                        String strInitval = constDef.constInitVal.stringConst.token.getContent();
                        strInitval = strInitval.substring(1, strInitval.length() - 1);
                        //末尾补0
                        if (calcStringConstLength(strInitval) < arrayType.size) {
                            strInitval = strInitval + "0".repeat(Math.max(0, (arrayType.size - calcStringConstLength(strInitval))));
                        }
                        Constant globalVariableInit = new Constant(arrayType, strInitval);
                        globalVariableInits.add(globalVariableInit);
                    }
                    else {
                        for (ConstExp constExp : constDef.constInitVal.constExps) {
                            Constant globalVariableInit = (Constant) visitConstExp(constExp);
                            //Instruction truncInst = new TruncInst(globalVariableInit, currentModule.context.getInt8Ty());
                            //对于常数计算，将trunc后的结果求值
                            Value truncedValue = trunc(globalVariableInit);
                            globalVariableInits.add(truncedValue);
                        }
                        //末尾补0
                        if (constDef.constInitVal.constExps.size() < arrayType.size) {
                            int zeroCnt = arrayType.size - constDef.constInitVal.constExps.size();
                            for (int i = 0; i < zeroCnt; i++) {
                                Constant zero = new Constant(currentModule.context.getInt8Ty(), "0");
                                globalVariableInits.add(zero);
                            }
                        }
                    }
                    globalVariable =
                            new GlobalVariable(constDef.ident.name(), currentModule.context.getPointerType(arrayType), globalVariableInits, false, true);
                }
                symbol =
                        new ArraySymbol(currentSymbolTable.id, constDef.ident.name(), constDef.ident.lineno(), symbolType, bType.type, globalVariable, 1);
                if (!currentSymbolTable.addSymbol(symbol)) {
                    ErrorReporter.getInstance().addError(constDef.ident.lineno(), "b");
                    return;
                }
                currentModule.addGlobalVariable(globalVariable);
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
        //局部变量
        if (currentSymbolTable.fatherTable != null) {
            //普通变量
            if (varDef.constExps.isEmpty()) {
                //创建alloca指令分配空间，创建新符号，并尝试加入符号表
                SymbolType symbolType;
                Instruction allocaInst;
                if (bType.type.equals("int")) {
                    symbolType = SymbolType.Int;
                    allocaInst = new AllocaInst(currentModule.context.getInt32Ty());
                } else {
                    symbolType = SymbolType.Char;
                    allocaInst = new AllocaInst(currentModule.context.getInt8Ty());
                }
                symbol = new VariableSymbol(currentSymbolTable.id,
                        varDef.ident.name(), varDef.ident.lineno(), symbolType, bType.type, allocaInst);
                if (!currentSymbolTable.addSymbol(symbol)) {
                    //重定义错误
                    ErrorReporter.getInstance().addError(varDef.ident.lineno(), "b");
                    return;
                }
                currentBasicBlock.addInstruction(allocaInst);
                //有初值，则去计算初值，并生成store指令；注意计算指令已经在visitExp过程中完成插入了
                if (varDef.initVal != null && varDef.initVal.exps != null && !varDef.initVal.exps.isEmpty()) {
                    Value initVal = visitExp(varDef.initVal.exps.get(0));
                    //返回的结果是i32，需要进行类型转换
                    if (symbolType.equals(SymbolType.Int)) {
                        Instruction storeInst = new StoreInst(initVal, allocaInst);
                        currentBasicBlock.addInstruction(storeInst);
                    }
                    else {
                        Value truncedValue = trunc(initVal);
                        Instruction storeInst = new StoreInst(truncedValue, allocaInst);
                        currentBasicBlock.addInstruction(storeInst);
                    }
                }
            }
            //数组
            else {
                //创建符号，并尝试加入符号表
                SymbolType symbolType;
                Instruction allocaInst;
                ArrayType arrayType;
                if (bType.type.equals("int")) {
                    symbolType = SymbolType.IntArray;
                    //计算数组长度
                    Constant arrayLength = (Constant) visitConstExp(varDef.constExps.get(0));
                    //为数组分配空间
                    arrayType =
                            currentModule.context.getArrayType(currentModule.context.getInt32Ty(), arrayLength.getIntValue());
                } else {
                    symbolType = SymbolType.CharArray;
                    //计算数组长度
                    Constant arrayLength = (Constant) visitConstExp(varDef.constExps.get(0));
                    //为数组分配空间
                    arrayType =
                            currentModule.context.getArrayType(currentModule.context.getInt8Ty(), arrayLength.getIntValue());
                }
                allocaInst = new AllocaInst(arrayType);
                symbol = new ArraySymbol(currentSymbolTable.id,
                        varDef.ident.name(), varDef.ident.lineno(), symbolType, bType.type, allocaInst, 1);
                if (!currentSymbolTable.addSymbol(symbol)) {
                    ErrorReporter.getInstance().addError(varDef.ident.lineno(), "b");
                    return;
                }
                currentBasicBlock.addInstruction(allocaInst);
                //有初值，则去计算初值，并生成store指令；注意计算指令已经在visitExp过程中完成插入了
                //对局部数组赋初值的方法是，对每一个位置进行getelementptr-store操作
                //字符数组部分初始化时，需要将未完全初始化的部分b补0
                if (varDef.initVal != null) {
                    if (varDef.initVal.stringConst != null) {
                        String strInitval = varDef.initVal.stringConst.token.getContent();
                        strInitval = strInitval.substring(1, strInitval.length() - 1);
                        //末尾补0
                        if (calcStringConstLength(strInitval) < arrayType.size) {
                            strInitval = strInitval + "0".repeat(Math.max(0, (arrayType.size - calcStringConstLength(strInitval))));
                        }
                        for (int i = 0; i < calcStringConstLength(strInitval); i++) {
                            Instruction getElementPtr =
                                    new GetElementPtr(allocaInst, new Constant(currentModule.context.getInt32Ty(), String.valueOf(i)));
                            currentBasicBlock.addInstruction(getElementPtr);
                            Constant ithChar;
                            if (strInitval.charAt(i) == '\\') {
                                String escapeChar = String.valueOf(strInitval.charAt(i) + strInitval.charAt(i + 1));
                                ithChar = new Constant(currentModule.context.getInt8Ty(), escapeChar);
                                i++;
                            }
                            else {
                                ithChar = new Constant(currentModule.context.getInt8Ty(), String.valueOf(strInitval.charAt(i)));
                            }
                            Instruction storeInst =
                                    new StoreInst(ithChar, getElementPtr);
                            currentBasicBlock.addInstruction(storeInst);
                        }
                    }
                    else {
                        for (int i = 0; i < varDef.initVal.exps.size(); i++) {
                            Instruction getElementPtr =
                                    new GetElementPtr(allocaInst, new Constant(currentModule.context.getInt32Ty(), String.valueOf(i)));
                            currentBasicBlock.addInstruction(getElementPtr);
                            if (symbolType.equals(SymbolType.IntArray)) {
                                Instruction storeInst = new StoreInst(visitExp(varDef.initVal.exps.get(i)), getElementPtr);
                                currentBasicBlock.addInstruction(storeInst);
                            }
                            else {
                                Value initVal = visitExp(varDef.initVal.exps.get(i));
                                Value truncedValue = trunc(initVal);
                                Instruction storeInst = new StoreInst(truncedValue, getElementPtr);
                                currentBasicBlock.addInstruction(storeInst);
                            }
                        }
                        //如果是字符数组，且使用大括号的形式赋值，则在这里写末尾补0的逻辑
                        if (symbolType.equals(SymbolType.CharArray) && varDef.initVal.exps.size() < arrayType.size) {
                            int zeroCnt = arrayType.size - varDef.initVal.exps.size();
                            for (int i = 0; i < zeroCnt; i++) {
                                Constant zero = new Constant(currentModule.context.getInt8Ty(), "0");
                                Instruction getElementPtr =
                                        new GetElementPtr(allocaInst, new Constant(currentModule.context.getInt32Ty(), String.valueOf(varDef.initVal.exps.size() + i)));
                                currentBasicBlock.addInstruction(getElementPtr);
                                Instruction storeInst = new StoreInst(zero, getElementPtr);
                                currentBasicBlock.addInstruction(storeInst);
                            }
                        }
                    }
                }
            }
        }
        //全局变量
        else {
            //普通变量
            if (varDef.constExps.isEmpty()) {
                //尝试创建符号，加入符号表；对于全局变量，创建即求值
                SymbolType symbolType;
                GlobalVariable globalVariable;
                if (bType.type.equals("int")) {
                    symbolType = SymbolType.Int;
                    Value globalVariableInit;
                    //有初值
                    if (varDef.initVal != null && varDef.initVal.exps != null && !varDef.initVal.exps.isEmpty()) {
                        globalVariableInit = visitExp(varDef.initVal.exps.get(0));
                    }
                    //无初值，则置0
                    else {
                        globalVariableInit = new Constant(currentModule.context.getInt32Ty(), "0");
                    }
                    ArrayList<Value> globalVariableInits = new ArrayList<>();
                    globalVariableInits.add(globalVariableInit);
                    globalVariable =
                            new GlobalVariable(varDef.ident.name(), currentModule.context.getPointerType(currentModule.context.getInt32Ty()), globalVariableInits, false, false);
                }
                else {
                    symbolType = SymbolType.Char;
                    Value globalVariableInit;
                    //有初值
                    if (varDef.initVal != null && varDef.initVal.exps != null && !varDef.initVal.exps.isEmpty()) {
                        globalVariableInit = visitExp(varDef.initVal.exps.get(0));
                    }
                    //无初值，则置0
                    else {
                        globalVariableInit = new Constant(currentModule.context.getInt8Ty(), "0");
                    }
                    Value truncedValue = trunc(globalVariableInit);
                    ArrayList<Value> globalVariableInits = new ArrayList<>();
                    globalVariableInits.add(truncedValue);
                    globalVariable =
                            new GlobalVariable(varDef.ident.name(), currentModule.context.getPointerType(currentModule.context.getInt8Ty()), globalVariableInits, false, false);
                }
                symbol = new VariableSymbol(currentSymbolTable.id, varDef.ident.name(), varDef.ident.lineno(), symbolType, bType.type, globalVariable);
                if (!currentSymbolTable.addSymbol(symbol)) {
                    ErrorReporter.getInstance().addError(varDef.ident.lineno(), "b");
                    return;
                }
                currentModule.addGlobalVariable(globalVariable);
            }
            //数组
            //TODO:对于zeroinitializer的输出，在GlobalVariable类中完成，检测到全局数组未赋初值则输出zeroinitializer
            else {
                SymbolType symbolType;
                ArrayType arrayType;
                GlobalVariable globalVariable;
                if (bType.type.equals("int")) {
                    symbolType = SymbolType.IntArray;
                    ArrayList<Value> globalVariableInits = new ArrayList<>();
                    //计算数组长度
                    Constant arrayLength = (Constant) visitConstExp(varDef.constExps.get(0));
                    arrayType =
                            currentModule.context.getArrayType(currentModule.context.getInt32Ty(), arrayLength.getIntValue());
                    //有初值，则计算初值
                    if (varDef.initVal != null && varDef.initVal.exps != null && !varDef.initVal.exps.isEmpty()) {
                        for (Exp exp : varDef.initVal.exps) {
                            Value globalVariableInit = visitExp(exp);
                            globalVariableInits.add(globalVariableInit);
                        }
                        //末尾补0
                        if (varDef.initVal.exps.size() < arrayType.size) {
                            int zeroCnt = arrayType.size - varDef.initVal.exps.size();
                            for (int i = 0; i < zeroCnt; i++) {
                                Constant zero = new Constant(currentModule.context.getInt32Ty(), "0");
                                globalVariableInits.add(zero);
                            }
                        }
                    }
                    //无初值时，创建传入的value是一个空的list（不为null）
                    globalVariable =
                            new GlobalVariable(varDef.ident.name(), currentModule.context.getPointerType(arrayType), globalVariableInits, false, false);
                }
                else {
                    symbolType = SymbolType.CharArray;
                    ArrayList<Value> globalVariableInits = new ArrayList<>();
                    //计算数组长度
                    Constant arrayLength = (Constant) visitConstExp(varDef.constExps.get(0));
                    arrayType =
                            currentModule.context.getArrayType(currentModule.context.getInt8Ty(), arrayLength.getIntValue());
                    //有初值，则计算初值
                    if (varDef.initVal != null) {
                        if (varDef.initVal.stringConst != null) {
                            String strInitval = varDef.initVal.stringConst.token.getContent();
                            strInitval = strInitval.substring(1, strInitval.length() - 1);
                            //末尾补0
                            if (calcStringConstLength(strInitval) < arrayType.size) {
                                strInitval = strInitval + "0".repeat(Math.max(0, (arrayType.size - calcStringConstLength(strInitval))));
                            }
                            Constant globalVariableInit = new Constant(arrayType, strInitval);
                            globalVariableInits.add(globalVariableInit);
                        }
                        else if (varDef.initVal.exps != null){
                            for (Exp exp : varDef.initVal.exps) {
                                Value globalVariableInit = visitExp(exp);
                                Value truncedValue = trunc(globalVariableInit);
                                globalVariableInits.add(truncedValue);
                            }
                            //末尾补0
                            if (varDef.initVal.exps.size() < arrayType.size) {
                                int zeroCnt = arrayType.size - varDef.initVal.exps.size();
                                for (int i = 0; i < zeroCnt; i++) {
                                    Constant zero = new Constant(currentModule.context.getInt8Ty(), "0");
                                    globalVariableInits.add(zero);
                                }
                            }
                        }
                    }
                    globalVariable =
                            new GlobalVariable(varDef.ident.name(), currentModule.context.getPointerType(arrayType), globalVariableInits, false, false);
                }
                symbol =
                        new ArraySymbol(currentSymbolTable.id, varDef.ident.name(), varDef.ident.lineno(), symbolType, bType.type, globalVariable, 1);
                if (!currentSymbolTable.addSymbol(symbol)) {
                    ErrorReporter.getInstance().addError(varDef.ident.lineno(), "b");
                    return;
                }
                currentModule.addGlobalVariable(globalVariable);
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

                }
                //数组
                else if (funcFParam.varType.equals(VarType.Array)) {
                    if (funcFParam.bType.type.equals("int")) fpSymbolType = SymbolType.IntArray;
                    else fpSymbolType = SymbolType.CharArray;
                    Symbol fpSymbol = new ArraySymbol(currentSymbolTable.id,
                            funcFParam.ident.name(), funcFParam.ident.lineno(), fpSymbolType, funcFParam.bType.type, null, 1);
                    funcFParams.add(fpSymbol);

                }
            }
        }
        funcSymbol.setFParams(funcFParams);

        //创建函数Value
        ArrayList<Argument> arguments = new ArrayList<>();
        for (Symbol fParamSymbol : funcFParams) {
            Type argumentType;
            switch (fParamSymbol.type) {
                case Char -> argumentType = currentModule.context.getInt8Ty();
                case IntArray -> argumentType = currentModule.context.getPointerType(currentModule.context.getInt32Ty());
                case CharArray -> argumentType = currentModule.context.getPointerType(currentModule.context.getInt8Ty());
                default -> argumentType = currentModule.context.getInt32Ty();
            }
            Argument argument = new Argument(fParamSymbol.name, argumentType);
            arguments.add(argument);
            fParamSymbol.value = argument;
        }
        Function function = buildFunction(funcSymbol, arguments);
        //设置funcSymbol的Value
        funcSymbol.value = function;
        //尝试把funcSymbol加入符号表
        if (!currentSymbolTable.addSymbol(funcSymbol)) {
            //重定义错误b
            ErrorReporter.getInstance().addError(funcDef.ident.lineno(), "b");
            //return;
        }
        //进入函数
        currentFunction = function;
        //将function加入当前module
        currentModule.addFunction(function);
        //创建第一个基本块
        currentBasicBlock = new BasicBlock(function);
        //将基本块加入到函数中
        currentFunction.addBasicBlock(currentBasicBlock);
        //形参位于新的作用域
        currentSymbolTable = currentSymbolTable.newTable(fieldCnt);
        fieldCnt++;
        //尝试把形参加入符号表
        for (Symbol fParamSymbol : funcFParams) {
            if (!currentSymbolTable.addSymbol(fParamSymbol)) {
                ErrorReporter.getInstance().addError(fParamSymbol.lineno, "b");
            }
            //在函数最开始为每个形参进行alloca-store
            Instruction allocaInst = new AllocaInst(fParamSymbol.value.dataType);
            currentBasicBlock.addInstruction(allocaInst);
            Instruction storeInst = new StoreInst(fParamSymbol.value, allocaInst);
            currentBasicBlock.addInstruction(storeInst);
            //此后再用形参，则是去为它alloca的地址去找，因此需要修改fParamSymbol的value
            fParamSymbol.value = allocaInst;
        }
        //在调用visitBlock()之前，总是已经修改了currentSymbolTable，进入了新的作用域
        visitBlock(funcDef.block, funcSymbol, false);
        if (!funcDef.funcType.type.equals("void") && !funcDef.hasReturn()) {
            //无return错误g
            ErrorReporter.getInstance().addError(funcDef.endLine, "g");
        }
        currentSymbolTable = currentSymbolTable.back();
        currentBasicBlock = null;
        currentFunction = null;
    }

    public void visitMainFuncDef(MainFuncDef mainFuncDef) {
        //创建并进入函数
        Function mainFunction = new Function(currentModule.context.getInt32Ty(), "main", currentModule);
        currentFunction = mainFunction;
        //将function加入当前module
        currentModule.addMainFunction(mainFunction);
        //创建第一个基本块
        currentBasicBlock = new BasicBlock(mainFunction);
        //将基本块加入到函数中
        currentFunction.addBasicBlock(currentBasicBlock);
        //main是保留字，不是标识符，不需要加入符号表
        //进入新的作用域
        currentSymbolTable = currentSymbolTable.newTable(fieldCnt);
        fieldCnt++;
        visitBlock(mainFuncDef.block, null, false);
        if (!mainFuncDef.hasReturn()) {
            //无return错误g
            ErrorReporter.getInstance().addError(mainFuncDef.endLine, "g");
        }
        currentSymbolTable = currentSymbolTable.back();
        currentBasicBlock = null;
        currentFunction = null;
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
        Value addr = visitLVal(stmt.lVal);
        Symbol identSymbol = currentSymbolTable.getSymbol(stmt.lVal.ident.name());
        if (identSymbol != null && (identSymbol.type.equals(SymbolType.ConstInt) || identSymbol.type.equals(SymbolType.ConstChar)
                || identSymbol.type.equals(SymbolType.ConstIntArray) || identSymbol.type.equals(SymbolType.ConstCharArray))) {
            //对常量赋值错误h
            ErrorReporter.getInstance().addError(stmt.lVal.ident.lineno(), "h");
            return;
        }
        //通过identSymbol.value在内存中定位对象，对Exp求值，并赋值
        Value value = visitExp(stmt.exps.get(0));
        Instruction storeInst = new StoreInst(value, addr);
        currentBasicBlock.addInstruction(storeInst);
    }

    public void visitExpressions(Stmt stmt) {
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
        Instruction retInst;
        if (stmt.exps.isEmpty()) {
            retInst = new ReturnInst(currentModule.context.getVoidTy());
        }
        else {
            Value retRes = visitExp(stmt.exps.get(0));
            retInst = new ReturnInst(retRes.dataType, retRes);
        }
        //main函数
        if (funcSymbol == null) {
            currentBasicBlock.addInstruction(retInst);
            return;
        }
        if (funcSymbol.type.equals(SymbolType.VoidFunc) && !stmt.exps.isEmpty()) {
            //return表达式不匹配错误f
            ErrorReporter.getInstance().addError(stmt.returnToken.getLineno(), "f");
            return;
        }
        currentBasicBlock.addInstruction(retInst);
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
            //printf参数个数不匹配错误l
            ErrorReporter.getInstance().addError(stmt.printfToken.getLineno(), "l");
            return;
        }
        for (Exp exp : stmt.exps) {
            visitExp(exp);
        }
    }

    //返回符号表中lVal对应的symbol的value（即它的地址），相当于“找到该左值定位的对象”
    //如果对数组索引，则需要通过getelementptr获取对应的地址
    public Value visitLVal(LVal lVal) {
        Symbol identSymbol = currentSymbolTable.getSymbol(lVal.ident.name());
        if (identSymbol == null) {
            ErrorReporter.getInstance().addError(lVal.ident.lineno(), "c");
            return null;
        }
        Value value = identSymbol.value;
        //普通变量，其value是一个i32*或i8*
        if (identSymbol instanceof VariableSymbol) {
            return value;
        }
        //数组
        else if (identSymbol instanceof ArraySymbol) {
            //没有对数组索引，则返回数组首元素的地址
            Instruction getElementPtr;
            if (lVal.exps == null || lVal.exps.isEmpty()) {
                getElementPtr = new GetElementPtr(value, new Constant(currentModule.context.getInt32Ty(), "0"));
            }
            //否则，获取对应的索引地址
            else {
                //本实验中只考虑一维数组
                //当然，得益于类型系统，多维数组也很好实现
                //数组symbol的value一定是一个指针；如果它的refType也是一个指针，则需要先load出来，获取数组的基地址
                if (((PointerType) value.dataType).refType instanceof PointerType) {
                    Instruction loadInst = new LoadInst(value);
                    currentBasicBlock.addInstruction(loadInst);
                    getElementPtr = new GetElementPtr(loadInst, visitExp(lVal.exps.get(0)));
                }
                else {
                    getElementPtr = new GetElementPtr(value, visitExp(lVal.exps.get(0)));
                }
            }
            currentBasicBlock.addInstruction(getElementPtr);
            return getElementPtr;
        }
        //unreachable code;函数标识符不是左值
        else return null;
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

    /********** help functions **********/
    public Value zext(Value value) {
        if (!value.dataType.equals(currentModule.context.getInt32Ty())) {
            //如果是常数，直接计算出结果
            if (value instanceof Constant) {
                return new Constant(currentModule.context.getInt32Ty(), ((Constant) value).constantData);
            }
            //否则生成zext指令
            else {
                Instruction zextInst = new ZextInst(value, currentModule.context.getInt32Ty());
                currentBasicBlock.addInstruction(zextInst);
                return zextInst;
            }
        }
        else {
            return value;
        }
    }

    public Value trunc(Value value) {
        //如果已经是char，则无需trunc
        if (value.dataType.equals(currentModule.context.getInt8Ty())) {
            return value;
        }
        //如果是常数，直接计算出结果
        if (value instanceof Constant) {
            int originData = ((Constant) value).getIntValue();
            int newData = originData & 0xff;
            return new Constant(currentModule.context.getInt8Ty(), String.valueOf(newData));
        }
        //生成trunc指令
        Instruction truncInst = new TruncInst(value, currentModule.context.getInt8Ty());
        currentBasicBlock.addInstruction(truncInst);
        return truncInst;
    }

    public Constant evalConstantBinaryCalc(Constant constant1, Constant constant2, Op op) {
        if (op.opType.equals(Op.OpType.add)) {
            return new Constant(currentModule.context.getInt32Ty(), String.valueOf(constant1.getIntValue() + constant2.getIntValue()));
        }
        else if (op.opType.equals(Op.OpType.sub)) {
            return new Constant(currentModule.context.getInt32Ty(), String.valueOf(constant1.getIntValue() - constant2.getIntValue()));
        }
        else if (op.opType.equals(Op.OpType.mul)) {
            return new Constant(currentModule.context.getInt32Ty(), String.valueOf(constant1.getIntValue() * constant2.getIntValue()));
        }
        else if (op.opType.equals(Op.OpType.sdiv)) {
            return new Constant(currentModule.context.getInt32Ty(), String.valueOf(constant1.getIntValue() / constant2.getIntValue()));
        }
        else if (op.opType.equals(Op.OpType.srem)) {
            return new Constant(currentModule.context.getInt32Ty(), String.valueOf(constant1.getIntValue() % constant2.getIntValue()));
        }
        //supposed to be unreachable
        else {
            return null;
        }
    }

    public Value buildBinaryCalcInstruction(Value value1, Value value2, Op op) {
        //如果是常数，直接求值
        if (value1 instanceof Constant && value2 instanceof Constant) {
            return evalConstantBinaryCalc((Constant) value1, (Constant) value2, op);
        }
        //否则，创建并插入BinaryInstruction，并返回该指令实例
        Value zextedValue1 = zext(value1);
        Value zextedValue2 = zext(value2);
        Instruction BinaryInst = new BinaryInst(op, zextedValue1, zextedValue2);
        currentBasicBlock.addInstruction(BinaryInst);
        return BinaryInst;
    }

    private int calcStringConstLength(String str) {
        Pattern pattern = Pattern.compile("\\\\."); // 匹配反斜杠及其后的字符
        Matcher matcher = pattern.matcher(str);
        int length = 0;
        int lastIndex = 0;

        while (matcher.find()) {
            length += matcher.start() - lastIndex; // 计算普通字符部分
            length++; // 转义字符算 1 个
            lastIndex = matcher.end();
        }

        length += str.length() - lastIndex; // 加上最后剩余的普通字符部分
        return length;
    }

    @NotNull
    private Function buildFunction(FuncSymbol funcSymbol, ArrayList<Argument> arguments) {
        Type returnType;
        switch (funcSymbol.type) {
            case CharFunc -> returnType = currentModule.context.getInt8Ty();
            case VoidFunc -> returnType = currentModule.context.getVoidTy();
            default -> returnType = currentModule.context.getInt32Ty();
        }
        Function function;
        if (arguments.isEmpty()) {
            function = new Function(returnType, funcSymbol.name, currentModule);
        }
        else {
            function = new Function(returnType, funcSymbol.name, arguments, currentModule);
        }
        return function;
    }

    /********** help functions end **********/

    public Value visitExp(Exp exp) {
        return visitAddExp(exp.addExp);
    }

    public void visitCond(Cond cond) {
        visitLOrExp(cond.lOrExp);
    }

    public Value visitAddExp(AddExp addExp) {
        if (addExp.mulExps.size() == 1)
            return visitMulExp(addExp.mulExps.get(0));
        Value value1 = visitMulExp(addExp.mulExps.get(0));
        Value value2 = visitMulExp(addExp.mulExps.get(1));
        Op op = new Op(Op.Op2Type(addExp.addExpOps.get(0)));
        Value res = buildBinaryCalcInstruction(value1, value2, op);
        for (int i = 2; i < addExp.mulExps.size(); i++) {
            Value value = visitMulExp(addExp.mulExps.get(i));
            op = new Op(Op.Op2Type(addExp.addExpOps.get(i - 1)));
            res = buildBinaryCalcInstruction(res, value, op);
        }
        return res;
    }

    public Value visitMulExp(MulExp mulExp) {
        if (mulExp.unaryExps.size() == 1)
            return visitUnaryExp(mulExp.unaryExps.get(0));
        Value value1 = visitUnaryExp(mulExp.unaryExps.get(0));
        Value value2 = visitUnaryExp(mulExp.unaryExps.get(1));
        Op op = new Op(Op.Op2Type(mulExp.mulExpOps.get(0)));
        Value res = buildBinaryCalcInstruction(value1, value2, op);
        for (int i = 2; i < mulExp.unaryExps.size(); i++) {
            Value value = visitUnaryExp(mulExp.unaryExps.get(i));
            op = new Op(Op.Op2Type(mulExp.mulExpOps.get(i - 1)));
            res = buildBinaryCalcInstruction(res, value, op);
        }
        return res;
    }

    public Value visitUnaryExp(UnaryExp unaryExp) {
        if (unaryExp.primaryExp != null) return visitPrimaryExp(unaryExp.primaryExp);
        //函数调用
        else if (unaryExp.ident != null) {
            Symbol identSymbol = currentSymbolTable.getSymbol(unaryExp.ident.name());
            if (identSymbol == null) {
                //变量未定义错误c
                ErrorReporter.getInstance().addError(unaryExp.ident.lineno(), "c");
                return null;
            }
            if (identSymbol instanceof FuncSymbol) {
                FuncSymbol funcIdentSymbol = (FuncSymbol) identSymbol;
                //如果调用语句传入了实参
                if (unaryExp.funcRParams != null) {
                    if (funcIdentSymbol.fParamCnt != unaryExp.funcRParams.exps.size()) {
                        //形实参个数不匹配错误d
                        ErrorReporter.getInstance().addError(unaryExp.ident.lineno(), "d");
                        return null;
                    }
                    //获得实参列表
                    ArrayList<Value> arguments = new ArrayList<>();
                    for (Exp exp : unaryExp.funcRParams.exps) {
                        arguments.add(visitExp(exp));
                    }
                    //历史遗留，暂时不动，用不了再说
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
                            //形实参类型不匹配错误e
                            ErrorReporter.getInstance().addError(unaryExp.ident.lineno(), "e");
                            return null;
                        }
                    }
                    //能够进行到这里说明传参正确，生成call指令
                    //从符号表获得function的value
                    Function function = (Function) funcIdentSymbol.value;
                    //生成call指令
                    Instruction callInst = new CallInst(function, arguments);
                    currentBasicBlock.addInstruction(callInst);
                    //返回call指令的实例，可以理解为该unaryExp的计算结果是函数调用的返回值
                    return callInst;
                }
                //如果调用语句没传入实参
                else {
                    if (funcIdentSymbol.fParamCnt != 0) {
                        //形实参个数不匹配错误d
                        ErrorReporter.getInstance().addError(unaryExp.ident.lineno(), "d");
                        return null;
                    }
                    //能够进行到这里说明传参正确，生成call指令
                    //从符号表获得function的value
                    Function function = (Function) funcIdentSymbol.value;
                    //生成call指令
                    Instruction callInst = new CallInst(function, new ArrayList<>()/*空实参表*/);
                    currentBasicBlock.addInstruction(callInst);
                    //返回call指令的实例，可以理解为该unaryExp的计算结果是函数调用的返回值
                    return callInst;
                }
            }
            //unreachable code
            return null;
        }
        //UnaryOp UnaryExp
        else {
            if (unaryExp.unaryOp.op.equals("-")) {
                Value value1 = new Constant(currentModule.context.getInt32Ty(), "0");
                Value value2 = visitUnaryExp(unaryExp.unaryExp);
                Op op = new Op(Op.OpType.sub);
                return buildBinaryCalcInstruction(value1, value2, op);
            }
            else if (unaryExp.unaryOp.op.equals("+")) {
                return visitUnaryExp(unaryExp.unaryExp);
            }
            else {
                //!a <=> a == 0
                Value value1 = new Constant(currentModule.context.getInt32Ty(), "0");
                Value value2 = visitUnaryExp(unaryExp.unaryExp);
                Op op = new Op(Op.OpType.eq);
                Instruction icmpInst = new IcmpInst(op, value1, value2);
                currentBasicBlock.addInstruction(icmpInst);
                return icmpInst;
            }
        }
    }

    public Value visitPrimaryExp(PrimaryExp primaryExp) {
        if (primaryExp.exp != null) return visitExp(primaryExp.exp);
        else if (primaryExp.lVal != null) {
            Value lValAddr = visitLVal(primaryExp.lVal);
            //需要对表达式求值；这里的求值即对左值进行evaluate
            Symbol identSymbol = currentSymbolTable.getSymbol(primaryExp.lVal.ident.name());
            //如果左值是一个“数组名”，evaluate后的值是“数组元素的首地址”，是一个referenced type为数组元素类型（i32或i8）的指针
            //也即visitLVal()的返回值
            //如果identSymbol为null，则instanceof ArraySymbol会返回false，所以这里不用特殊处理
            if (identSymbol instanceof ArraySymbol && (primaryExp.lVal.exps == null || primaryExp.lVal.exps.isEmpty())) {
                return lValAddr;
            }
            //否则，evaluate是取出该左值定位的对象的值，即生成和返回一条load指令实例
            else {
                Instruction loadInst = new LoadInst(lValAddr);
                currentBasicBlock.addInstruction(loadInst);
                return loadInst;
            }
        }
        else if (primaryExp.number != null) {
            return new Constant(currentModule.context.getInt32Ty(), primaryExp.getNumber());
        }
        else {
            //但凡是表达式，计算结果的返回值都是i32；常数的类型也都是i32。如果需要截断，则是在赋值时进行截断。
            return new Constant(currentModule.context.getInt32Ty(), primaryExp.getCharacter());
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
