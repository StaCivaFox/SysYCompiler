import frontend.Lexer;
import frontend.Parser;
import frontend.Token;
import frontend.TokenType;
import frontend.elements.CompUnit;
import middle.IR.Module;
import middle.Visitor;
import utils.ErrorReporter;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Compiler {
    public static void main(String[] args) throws IOException {
        String fileName = "testfile.txt";
        String source = Files.readString(Paths.get(fileName));
        source += "\n";
        Lexer lexer = new Lexer(source);
        Parser parser = new Parser(lexer);
        //词法分析作业输出代码
        /*String lexerOutputPath = "lexer.txt";

        while (!lexer.reachEnd()) {
            Token token = lexer.peek();
            lexer.next();
            //System.out.println(token);
            Files.write(Paths.get(lexerOutputPath), (token.toString() + '\n').getBytes(),
                    StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        }*/

        //语法分析作业输出代码
        /*String parserOutputPath = "parser.txt";
        CompUnit compUnit = parser.parseCompUnit();
        //System.out.println(compUnit.toString());
        Files.write(Paths.get(parserOutputPath), compUnit.toString().getBytes(),
                StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        if (ErrorReporter.getInstance().hasError()) {
            ErrorReporter.getInstance().printError();
        }*/

        CompUnit compUnit = parser.parseCompUnit();

        //语义分析作业输出代码
        /*String symbolOutputPath = "symbol.txt";
        Visitor visitor = new Visitor(compUnit);
        visitor.visitCompUnit(compUnit);
        Files.write(Paths.get(symbolOutputPath), visitor.currentSymbolTable.toString().getBytes(),
                StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        if (ErrorReporter.getInstance().hasError()) {
            ErrorReporter.getInstance().printError();
        }*/
        //中间代码生成
        String llvmOutputPath = "llvm_ir.txt";
        Visitor visitor = new Visitor(compUnit);
        visitor.visitCompUnit(compUnit);
        Module irModule = visitor.currentModule;
        if (ErrorReporter.getInstance().hasError()) {
            ErrorReporter.getInstance().printError();
            return;
        }
        Files.write(Paths.get(llvmOutputPath), irModule.toString().getBytes(),
                StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }
}
