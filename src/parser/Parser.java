package parser;

import tokenizer.Token;
import tokenizer.TokenType;
import java.util.*;

public class Parser {

    private static final Map<String, Integer> variables = new HashMap<>();
    private static int tokenIterator = 0;
    private static boolean ifCondition = false;

    /**
     * main method of the Parser class. Takes tokenized code as input and iterates over it for different functionality
     * @param code
     */
    public static void execute(List<Token> code)    {
        tokenIterator = 0;

        while (tokenIterator < code.size()) {
            Token token = code.get(tokenIterator);
            if (token.getType().equals(TokenType.PRINT)) {
                // Handle print statement
                if (tokenIterator + 1 < code.size() && code.get(tokenIterator + 1).getType().equals(TokenType.PARENTHESIS) && code.get(tokenIterator + 1).getValue().equals("(")) {
                    int endIndex = tokenIterator + 3;
                    List<Token> expression = code.subList(tokenIterator + 2, endIndex);
                    String thingToPrint = expression.getLast().getValue();

                    if (thingToPrint.matches("\\d+")) {
                        System.out.print(thingToPrint);
                    } else if (thingToPrint.matches("[a-zA-Z0-9]+")) {
                        if (variables.containsKey(thingToPrint)) {
                            System.out.println(variables.get(thingToPrint));
                        } else {
                            throw new RuntimeException("Variable not found");
                        }
                    } else {
                        throw new RuntimeException("Illegal character in print statement");
                    }
                    tokenIterator = endIndex + 1;

                } else {
                    // Handle potential syntax error (missing parentheses)
                    System.err.println("Syntax Error: Expected '(' after 'print'");
                }
            } else if (token.getType().equals(TokenType.IDENTIFIER)) {
                if (code.get(tokenIterator + 1).getType().equals(TokenType.OPERATOR) && code.get(tokenIterator + 1).getValue().equals("=")) {
                    // find where right side of the assignment operation ends
                    List<Token> rightSide = findRightSideOfAssignment(tokenIterator + 1, code);

                    //write in map variable and its value
                    variables.put(token.getValue(), evaluateExpression(rightSide));
                }
            } else if (token.getType().equals(TokenType.NEWLINE)) {
                tokenIterator++;
            } else if (token.getType().equals(TokenType.WHILE)) {
                Map<String, List<Token>> parts = getConditionParts(code);
                List<Token> block = getBlock(code);

                executeBlock(parts, block);
            }
            else if (token.getType().equals(TokenType.IF)) {
                Map<String, List<Token>> parts = getConditionParts(code);
                List<Token> block = getBlock(code);

                List<Token> left = parts.get("leftExpression");
                List<Token> right = parts.get("rightExpression");
                List<Token> operator = parts.get("operator");

                ifCondition = checkCondition(evaluateExpression(left), evaluateExpression(right), operator);


                if (ifCondition) {
                    int storeIter = tokenIterator;
                    execute(block);
                    tokenIterator = storeIter - 1;
                } else {
                    tokenIterator --;
                }

            }
            else if (token.getType().equals(TokenType.ELSE)) {
                List<Token> elseBlock = getBlock(code);


                if (!ifCondition) {
                    int storeIter = tokenIterator;
                    execute(elseBlock);
                    tokenIterator = storeIter - 1;
                } else {
                    tokenIterator = tokenIterator + elseBlock.size() - 6;
                }
            } else if (token.getType().equals(TokenType.DEDENT)) {
                tokenIterator++;
            } else {
                tokenIterator++;
            }

        }
    }

    /**
     * for boolean conditions, separates line into 3 pieces: left integer, right integer and operator it is checked with.
     * @param code: copy of list of tokens
     * @return A map where each key is a category (String) and the value is a list of tokens (List<Token>) belonging to that category.
     */
    private static Map<String, List<Token>> getConditionParts(List<Token> code) {
        Map<String, List<Token>> parts = new HashMap<>();

        //find whole condition
        int iter = tokenIterator;
        while (!code.get(iter).getValue().equals(":")) {
            iter++;
        }
        List<Token> condition = code.subList(tokenIterator + 1, iter);

        int expIter = 0;
        while (expIter < condition.size() && !condition.get(expIter).getValue().matches("([<>=!])")) {
            expIter++;
        }

        parts.put("leftExpression", new ArrayList<>(condition.subList(0, expIter)));
        List<Token> operator = new ArrayList<>();
        operator.add(condition.get(expIter));
        if (condition.get(expIter + 1).getType().equals(TokenType.OPERATOR)) {
            operator.add(condition.get(expIter + 1));
            expIter++;
        }


        parts.put("operator", operator);

        int operatorIndex = expIter;

        while (expIter < condition.size() && !condition.get(expIter).getValue().equals(":")) {
            expIter++;
        }
        tokenIterator += expIter + 1;
        parts.put("rightExpression", new ArrayList<>(condition.subList(operatorIndex + 1, expIter)));

        return parts;
    }

    /**
     * executes block of for loops
     * @param condition: condition that needs to be checked, divided in 3 parts
     * @param block: list of tokens that need to be executed
     */
    private static void executeBlock(Map<String, List<Token>> condition, List<Token> block) {
        List<Token> left = condition.get("leftExpression");

        List<Token> right = condition.get("rightExpression");
        List<Token> operator = condition.get("operator");

        boolean currValue = checkCondition(evaluateExpression(left), evaluateExpression(right), operator);

        int iteratorStore = tokenIterator;
        while (currValue) {
            execute(block);
            currValue = checkCondition(evaluateExpression(left), evaluateExpression(right), operator);
        }
        tokenIterator = iteratorStore;

    }

    /**
     * checks condition given operator and values on both sides of it.
     * @param firstNum
     * @param secondNum
     * @param operator
     * @return
     */
    private static boolean checkCondition(int firstNum, int secondNum, List<Token> operator) {
        StringBuilder oper = new StringBuilder();
        for (Token token : operator) {
            if (token.getType().equals(TokenType.OPERATOR)) {
                oper.append(token.getValue());
            }
        }
        String fullOperator = oper.toString();
        switch (fullOperator) {
            case "<" -> {
                return firstNum < secondNum;
            }
            case ">" -> {
                return firstNum > secondNum;
            }
            case ">=" -> {
                return firstNum >= secondNum;
            }
            case "<=" -> {
                return firstNum <= secondNum;
            }
            case "==" -> {
                return firstNum == secondNum;
            }
            case "!=" -> {
                return firstNum != secondNum;
            }
            default -> throw new RuntimeException("Invalid operator for boolean condition");

        }
    }

    /**
     * extracts list that contains block elements from full list.
     * @param code: copy of full list
     * @return
     */
    private static List<Token> getBlock(List<Token> code) {
        int iter = tokenIterator + 1;
        while (iter < code.size() && !code.get(iter).getType().equals(TokenType.DEDENT)) {
            iter++;
        }
        List<Token> block = code.subList(tokenIterator + 2, iter);
        tokenIterator = iter + 1;

        // Create a copy of the block list to avoid concurrent modification
        List<Token> blockCopy = new ArrayList<>(block);

        for (Token token : blockCopy) {
            if (token.getType().equals(TokenType.INDENT)) {
                block.remove(token);
            }
        }

        return block;
    }

    private static final Stack<Token> expressionTokens = new Stack<>();

    /**
     * evaluates expression
     * @param expression: expression that needs to be evaluated
     * @return result integer of evaluation
     */
    private static Integer evaluateExpression(List<Token> expression) {

        // Create a defensive copy to avoid potential concurrent modification issues
        List<Token> expressionCopy = new ArrayList<>(expression);

        // Check size using the copy to avoid modifying the original list
        if (expressionCopy.size() == 1) {
            Token firstToken = expressionCopy.getFirst();
            if (firstToken.getType().equals(TokenType.NUMBER)) {
                return Integer.parseInt(firstToken.getValue());
            } else if (firstToken.getType().equals(TokenType.IDENTIFIER)) {
                return variables.get(firstToken.getValue());
            }
        }


        for (Token token : expressionCopy) {

            if (token.getType().equals(TokenType.NUMBER) || token.getType().equals(TokenType.IDENTIFIER) || token.getType().equals(TokenType.OPERATOR)) {
                if (expressionTokens.isEmpty() && ((token.getType().equals(TokenType.IDENTIFIER) || token.getType().equals(TokenType.NUMBER)))) {
                    expressionTokens.push(token);
                }
                switch (token.getType()) {
                    case TokenType.NUMBER -> {
                        if (expressionTokens.peek().getType().equals(TokenType.OPERATOR)) {
                            expressionTokens.push(calculateCurrentExpression(Integer.parseInt(token.getValue())));
                        }
                    }
                    case TokenType.IDENTIFIER -> {
                        if (expressionTokens.peek().getType().equals(TokenType.OPERATOR)) {
                            expressionTokens.push(calculateCurrentExpression(variables.get(token.getValue())));
                        }
                    }
                    case TokenType.OPERATOR -> {
                        if (expressionTokens.isEmpty() || expressionTokens.peek().getType().equals(TokenType.OPERATOR)) {
                            throw new RuntimeException("Illegal syntax");
                        } else {
                            expressionTokens.push(token);
                        }

                    }
                }
            } else {
                throw new RuntimeException("Illegal expression format");
            }
        }

        if (expressionTokens.size() > 1) {
            throw new RuntimeException("wrong right side in expression");
        }

        try {
            int ans = Integer.parseInt(expressionTokens.peek().getValue());
            expressionTokens.pop();
            return ans;
        } catch (NumberFormatException e) {
            int ans = variables.get(expressionTokens.pop().getValue());
            expressionTokens.pop();
            return ans;
        }


    }

    /**
     * retrieves elements from stack and performs operations on them
     * @param currentNumValue: token that triggered evaluation
     * @return single Token with result of evaluation as value to replace 3 stack elements: first number, operator and second number.
     */
    private static Token calculateCurrentExpression(int currentNumValue) {
        int firstNum = 0;
        Token operator = expressionTokens.pop();
        if (expressionTokens.peek().getType().equals(TokenType.NUMBER)) {
            firstNum = Integer.parseInt(expressionTokens.pop().getValue());
        } else if (expressionTokens.peek().getType().equals(TokenType.IDENTIFIER)) {
            firstNum = variables.get(expressionTokens.pop().getValue());
        }

        int currAns = 0;

        switch (operator.getValue()) {
            case "+" -> currAns = firstNum + currentNumValue;
            case "-" -> currAns = firstNum - currentNumValue;
            case "*" -> currAns = firstNum * currentNumValue;
            case "/" -> currAns = firstNum / currentNumValue;
            case "%" -> currAns = firstNum % currentNumValue;
        }

        return new Token(TokenType.NUMBER, String.valueOf(currAns));
    }

    /**
     * extracts right side of = simbol in assignment operations to then work on it
     * @param equalsIndex index of = symbol
     * @param code code of full code
     * @return list of tokens on the right side of =
     */
    private static List<Token> findRightSideOfAssignment(int equalsIndex, List<Token> code) {
        int endIndex = equalsIndex;
        if (!code.get(equalsIndex).getValue().equals("=")) {
            throw new RuntimeException("= sign expected");
        } else {
            while (!code.get(endIndex).getType().equals(TokenType.NEWLINE)) {
                endIndex++;
            }
        }
        tokenIterator = endIndex;
        return code.subList(equalsIndex + 1, endIndex);
    }
}
