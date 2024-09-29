import frontend.Lexer;
import frontend.Token;
import frontend.TokenType;

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
        //词法分析作业输出代码
        String lexerOutputPath = "lexer.txt";

        while (!lexer.reachEnd()) {
            Token token = lexer.peek();
            lexer.next();
            //System.out.println(token);
            Files.write(Paths.get(lexerOutputPath), (token.toString() + '\n').getBytes(),
                    StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        }
    }
}
