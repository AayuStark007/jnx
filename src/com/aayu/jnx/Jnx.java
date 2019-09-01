package com.aayu.jnx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.aayu.jnx.Constants.FLAG_RUN_PROMPT;
import static com.aayu.jnx.Constants.FLAG_RUN_FILE;

public class Jnx {

    private static String inFile;
    private static int flag;

    static boolean hadError = false;

    private static Jnx instance;

    private Jnx() {
        flag = FLAG_RUN_PROMPT;
    }

    private Jnx(String _inFile) {
        inFile = _inFile;
        flag = FLAG_RUN_FILE;
    }

    public static Jnx GetInstance() {
        if (instance == null) {
            instance = new Jnx();
        }

        return instance;
    }


    public void SetFilePath(String _inFile) {
        inFile = _inFile;
        flag = FLAG_RUN_FILE;
    }

    public void init() throws IOException {
        if (flag == FLAG_RUN_PROMPT || inFile.length() == 0) {
            runPrompt();
        } else {
            runFile();
        }
    }

    private static void runFile() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(inFile));
        run(new String(bytes, Charset.defaultCharset()));

        if (hadError) {
            System.exit(65);
        }
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("> ");
            run(reader.readLine());
            hadError = false;
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();

        // Stop if syntax error.
        if (hadError) return;

        System.out.println(new AstPrinter().print(expression));
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }
}
