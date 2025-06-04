// Vivian Oh and Mia Subrahmanyam

import java.util.*;

public class Lexer {
    public static ArrayList<String> tokenize(String input) {
        Set<Character> symbols = Set.of('(', ')', '\\', 'λ', '.', '=', ';');
        ArrayList<String> tokens = new ArrayList<>();
        String token = "";

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == ';') break;

            if (Character.isWhitespace(c)) {
                if (!token.isEmpty()) {
                    tokens.add(token);
                    token = "";
                }
            } 
            else if (symbols.contains(c)) {
                if (!token.isEmpty()) {
                    tokens.add(token);
                    token = "";
                }
                tokens.add(String.valueOf(c));
            } 
            else {
                token += c;
            }
        }

        if (!token.isEmpty()) {
            tokens.add(token);
        }
        
        return tokens;
    }
}


