import parser.Parser;
import tokenizer.Token;
import tokenizer.Tokenizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PythonInterpreter {
    public static String readFileToString(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    public static void exec(String fileName) throws IOException {
        List<Token> list = Tokenizer.tokenize(readFileToString(fileName));

        Parser.execute(list);
    }


}
