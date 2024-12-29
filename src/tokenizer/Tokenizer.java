package tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    public static List<Token> tokenize(String input) {
        String tokenPatterns = String.join("|",
                "(?<NUMBER>\\d+)",              // Integer number
                "(?<IDENTIFIER>[a-zA-Z_]\\w*)", // Identifier (letters, underscores, and digits)
                "(?<OPERATOR>[+\\-*/=<>:])",    // Operators (including <, >, +, -, *, /, =, etc.)
                "(?<PARENTHESIS>[()])",
                "(?<INDENT>(?<=^|\\n)[ \\t]+)", // Single tab character as INDENT
                "(?<SPACE>[ \\t]+)",
                "(?<MISMATCH>.)"                // Any other character (unexpected)
        );


        Pattern pattern = Pattern.compile(tokenPatterns);
        Matcher matcher = pattern.matcher(input);
        List<Token> tokens = new ArrayList<>();

        int currentIndentLevel = -1;

        while (matcher.find()) {
            if (matcher.group("INDENT") != null) {
                String indent = matcher.group("INDENT");
                int indentLevel = indent.length(); // Count the number of spaces/tabs for indentation

                // Handle dedentation
                if (indentLevel < currentIndentLevel) {
                    // If indentation is less, add a DEDENT token
                    tokens.add(new Token(TokenType.DEDENT, "dedent"));
                }

                // Update the current indentation level
                currentIndentLevel = indentLevel;

                // Add the INDENT token
                tokens.add(new Token(TokenType.INDENT, indent));
            } else if (matcher.group("MISMATCH") != null) {
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

        return tokens;
    }
}
