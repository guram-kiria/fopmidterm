import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Token> list = new ArrayList<>();
        String code = "print (1 + 5)";
        list = Tokenizer.tokenize(code);


        int ans = Parser.parseAndEvaluate(list);
        System.out.println(ans);
    }
}
