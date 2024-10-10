import frontend.Lexer;
import frontend.Parser;
import frontend.Token;
import frontend.TokenType;
import frontend.elements.CompUnit;

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
        String parserOutputPath = "parser.txt";
        CompUnit compUnit = parser.parseCompUnit();
        System.out.println(compUnit.toString());
        /*Files.write(Paths.get(parserOutputPath), compUnit.toString().getBytes(),
                StandardOpenOption.APPEND, StandardOpenOption.CREATE);*/
    }
}
