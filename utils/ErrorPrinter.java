package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ErrorPrinter {
    private final String errorOutputPath;
    private ErrorPrinter() {
        this.errorOutputPath = "error.txt";
    }

    private static class ErrorPrinterInstance {
        private static final ErrorPrinter INSTANCE = new ErrorPrinter();
    }

    public static ErrorPrinter getInstance() {
        return ErrorPrinterInstance.INSTANCE;
    }

    public void print(String msg) throws IOException {
        Files.write(Paths.get(errorOutputPath), (msg + '\n').getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }
}
