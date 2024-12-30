package tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    public static List<Token> tokenize(String input) {
        String tokenPatterns = String.join("|",
                "(?<NUMBER>\\d+)",
                "(?<IDENTIFIER>[a-zA-Z_]\\w*)",
                "(?<OPERATOR>[+\\-*/=<>:])",
                "(?<PARENTHESIS>[()])",
                "(?<SPACE>[ \\t]+)",
                "(?<MISMATCH>.)"
        );

        Pattern pattern = Pattern.compile(tokenPatterns);
        List<Token> tokens = new ArrayList<>();

        Stack<Integer> indentStack = new Stack<>();
        indentStack.push(0); // Ini

        for(String line : input.split("\n")){
            Matcher matcher = pattern.matcher(line);

            int currentIndent = calculateIndent(line);

            // Handle INDENT
            if (currentIndent > indentStack.peek()) {
                tokens.add(new Token(TokenType.INDENT, "indent"));
                indentStack.push(currentIndent);
            }

            // Handle DEDENT
            while (currentIndent < indentStack.peek()) {
                tokens.add(new Token(TokenType.DEDENT, "dedent"));
                indentStack.pop();
            }

            while (matcher.find()) {
                if (matcher.group("MISMATCH") != null) {
                    throw new RuntimeException("Unexpected character: " + matcher.group("MISMATCH"));
                } else if (matcher.group("NUMBER") != null) {
                    tokens.add(new Token(TokenType.NUMBER, matcher.group("NUMBER")));
                } else if (matcher.group("IDENTIFIER") != null) {
                    String identifier = matcher.group("IDENTIFIER");
                    if ("print".equals(identifier)) {
                        tokens.add(new Token(TokenType.PRINT, "print"));
                    } else if ("while".equals(identifier)) {
                        tokens.add(new Token(TokenType.WHILE, "while"));
                    } else {
                        tokens.add(new Token(TokenType.IDENTIFIER, identifier));
                    }
                } else if (matcher.group("OPERATOR") != null) {
                    tokens.add(new Token(TokenType.OPERATOR, matcher.group("OPERATOR")));
                } else if (matcher.group("PARENTHESIS") != null) {
                    tokens.add(new Token(TokenType.PARENTHESIS, matcher.group("PARENTHESIS")));
                }
            }

            tokens.add(new Token(TokenType.NEWLINE,"newline"));

        }
        return tokens;
    }

    private static int calculateIndent(String line) {
        int i = 0;
        while (i < line.length() && Character.isWhitespace(line.charAt(i))) {
            i++;
        }
        return i;
    }
}
