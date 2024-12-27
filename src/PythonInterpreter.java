import java.util.*;

public class PythonInterpreter {
    private static final Map<String, Integer> variables = new HashMap<>();

    public static void main(String[] args) {

    }

    public static void execute(String code){
        List<String> commands = new ArrayList<>();
        for(String line : code.split(";")){
            if(line.matches("\\s*print.*")){
                handlePrint(line);
            } else if(line.contains("=")){
                handleAssignment(line);
            }
        }
    }

    private static int handleArithmeticOperation(String line){
        String[] operands;
        String operation = "";
        if (line.contains("+")){
            operation = "\\+";
        } else if(line.contains("-")){
            operation = "-";
        } else if(line.contains("*")){
            operation = "\\*";
        } else if(line.contains("/")){
            operation = "/";
        }else if(line.contains("%")){
            operation = "%";
        }else{
            return Integer.parseInt(line);  //TODO handle condition when input is not integer
        }

        operands = line.split(operation);
        String loperand = operands[0].trim();
        String roperand = operands[1].trim();
        int val1 = 0, val2 = 0;
        if(loperand.matches("\\d+")){
            val1 = Integer.parseInt(loperand);
        } else if (variables.containsKey(loperand)){
            val1 = variables.get(loperand);
        } else{
            System.out.println("Wrong input"); //TODO replace wrong output text with exception
        }

        if(roperand.matches("\\d+")){
            val2 = Integer.parseInt(roperand);
        } else if (variables.containsKey(roperand)){
            val2 = variables.get(roperand);
        } else{
            System.out.println("Wrong input"); //TODO replace wrong output text with exception
        }

        return switch (operation) {
            case "\\+" -> val1 + val2;
            case "-" -> val1 - val2;
            case "\\*" -> val1 * val2;
            case "/" -> val1 / val2;
            case "%" -> val1 % val2;
            default -> -1;
        };

    }

    private static void handleAssignment(String line) {
        String[] parts = line.split("=");
        String varName = parts[0].trim();
        String expression = parts[1].trim();
        int value = handleArithmeticOperation(expression);
        variables.put(varName, value);
    }

    private static void handlePrint(String line) {
        String thingToPrint = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
        if(thingToPrint.contains("+") || thingToPrint.contains("-") || thingToPrint.contains("*") || thingToPrint.contains("/") || thingToPrint.contains("%")){
            int numToPrint = handleArithmeticOperation(thingToPrint);
            System.out.println(numToPrint);
        } else {
            System.out.println(variables.get(thingToPrint));
        }

    }
}
