import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    public static List<Token> tokenize(String input) {
        String tokenPatterns = String.join("|",
                "(?<NUMBER>\\d+)",          // Integer number
                "(?<IDENTIFIER>[a-zA-Z_]\\w*)", // Identifier
                "(?<OPERATOR>[+\\-*/=])",   // Operators
                "(?<SKIP>[ \\t]+)",        // Spaces or tabs
                "(?<MISMATCH>.)"            // Any other character
        );

        Pattern pattern = Pattern.compile(tokenPatterns);
        Matcher matcher = pattern.matcher(input);
        List<Token> tokens = new ArrayList<>();

        while (matcher.find()) {
            if (matcher.group("SKIP") != null) {
                continue;
            } else if (matcher.group("MISMATCH") != null) {
                throw new RuntimeException("Unexpected character: " + matcher.group("MISMATCH"));
            } else if (matcher.group("NUMBER") != null) {
                tokens.add(new Token(TokenType.NUMBER, matcher.group("NUMBER")));
            } else if (matcher.group("IDENTIFIER") != null) {
                tokens.add(new Token(TokenType.IDENTIFIER, matcher.group("IDENTIFIER")));
            } else if (matcher.group("OPERATOR") != null) {
                tokens.add(new Token(TokenType.OPERATOR, matcher.group("OPERATOR")));
            }
        }

        return tokens;
    }
}
