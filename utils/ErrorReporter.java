package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class Error implements Comparable<Error> {
    public int lineno;
    public String msg;

    public Error(int lineno, String msg) {
        this.lineno = lineno;
        this.msg = msg;
    }

    @Override
    public int compareTo(Error o) {
        return this.lineno - o.lineno;
    }

    @Override
    public String toString() {
        return lineno + " " + msg;
    }
}

public class ErrorReporter {
    private final String errorOutputPath;
    private ArrayList<Error> errors;

    private ErrorReporter() {
        this.errorOutputPath = "error.txt";
        this.errors = new ArrayList<>();
    }

    private static class ErrorPrinterInstance {
        private static final ErrorReporter INSTANCE = new ErrorReporter();
    }

    public static ErrorReporter getInstance() {
        return ErrorPrinterInstance.INSTANCE;
    }

    public void addError(int lineno, String msg) {
        errors.add(new Error(lineno, msg));
    }

    public boolean hasError() {
        return !this.errors.isEmpty();
    }

    public void printError() throws IOException {
        StringBuilder sb = new StringBuilder();
        Collections.sort(errors);
        for (int i = 0; i < errors.size() - 1; i++) {
            sb.append(errors.get(i).toString());
            sb.append("\n");
        }
        sb.append(errors.get(errors.size() - 1).toString());
        Files.write(Paths.get(errorOutputPath), sb.toString().getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }
}
