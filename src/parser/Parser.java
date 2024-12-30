package parser;

import tokenizer.Token;
import tokenizer.TokenType;

import java.util.List;

public class Parser {

    public static void execute(List<Token> code) {
        for (int i = 0; i < code.size(); i++) {
            Token token = code.get(i);
            if (token.getType().equals(TokenType.PRINT)) {
                // Handle print statement
                if (i + 1 < code.size() && code.get(i + 1).getType().equals(TokenType.PARENTHESIS) && code.get(i + 1).getValue().equals("(")) {
                    int endIndex = findMatchingParenthesis(code, i + 1);
                    List<Token> expression = code.subList(i + 2, endIndex);
//TODO              int value = evaluateExpression(expression);
                    System.out.println(expression.getLast().getValue());
                    i = endIndex;
                } else {
                    // Handle potential syntax error (missing parentheses)
                    System.err.println("Syntax Error: Expected '(' after 'print'");
                }
            }
            //Handle other statements like
        }

}

    private static int findMatchingParenthesis(List<Token> code, int startIndex) {
        int count = 1;
        for (int i = startIndex + 1; i < code.size(); i++) {
            if (code.get(i).getType().equals(TokenType.PARENTHESIS) && code.get(i).getValue().equals("(")) {
                count++;
            } else if (code.get(i).getType().equals(TokenType.PARENTHESIS) && code.get(i).getValue().equals(")")) {
                count--;
                if (count == 0) {
                    return i;
                }
            }
        }

        throw new RuntimeException("Unmatched parenthesis");
    }
    }
