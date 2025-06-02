// λ
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Console {

    private static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        Map<String, Expression> dictionary = new HashMap<>();
        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        while (true) {
            String input = cleanConsoleInput();

            if (input.equalsIgnoreCase("exit")) break;

            try {
                ArrayList<String> tokens = lexer.tokenize(input);

                // Handle "run" expression
                if (!tokens.isEmpty() && tokens.get(0).equals("run")) {
                    tokens.remove(0);
                    Expression parsed = parseWithSubstitutions(tokens, dictionary, lexer, parser);
                    System.out.println("Before reduction: " + parsed);
                    Expression reduced = Runner.run(parsed);
                    System.out.println("After reduction: " + reduced);

                    String resultStr = reduced.toString();
                    if (resultStr.equals("(λx.(λy.x))")) {
                        System.out.println("true");
                    } else if (resultStr.equals("(λx.(λy.y))")) {
                        System.out.println("false");
                    } else {
                        System.out.println(resultStr);
                    }
                    continue;
                }

                // Handle named assignment: name = λ...
                if (tokens.contains("=")) {
                    int eqIndex = tokens.indexOf("=");
                    if (eqIndex != 1) {
                        System.out.println("Invalid assignment syntax.");
                        continue;
                    }

                    String name = tokens.get(0);
                    if (dictionary.containsKey(name)) {
                        System.out.println(name + " is already defined.");
                        continue;
                    }

                    ArrayList<String> rhsTokens = new ArrayList<>(tokens.subList(2, tokens.size()));
                    Expression rhsParsed = parseWithSubstitutions(rhsTokens, dictionary, lexer, parser);
                    System.out.println("RHS before reduction: " + rhsParsed);
                    Expression reduced = Runner.run(rhsParsed);
                    System.out.println("RHS after reduction: " + reduced);

                    dictionary.put(name, reduced);
                    System.out.println("Stored reduced expression for " + name + ": " + reduced);
                    continue;
                }

                // Otherwise, parse and show normal expression
                Expression parsed = parseWithSubstitutions(tokens, dictionary, lexer, parser);
                System.out.println(parsed.toString());

            } catch (Exception e) {
                System.out.println("Error: Could not parse input: \"" + input + "\"");
                System.out.println("Details: " + e.getMessage());
            }
        }

        System.out.println("Goodbye!");
    }

    private static Expression parseWithSubstitutions(ArrayList<String> tokens, Map<String, Expression> dict, Lexer lexer, Parser parser) throws Exception {
        ArrayList<String> resolved = new ArrayList<>();
        for (String t : tokens) {
            if (dict.containsKey(t)) {
                // Expand the stored expression as tokens
                resolved.addAll(lexer.tokenize(dict.get(t).toString()));
            } else {
                resolved.add(t);
            }
        }
        Expression parsed = parser.parse(resolved);
        return parsed;
    }

    private static String cleanConsoleInput() {
        System.out.print("> ");
        String raw = in.nextLine();
        String deBOMified = raw.replaceAll("\uFEFF", "");
        String clean = removeWeirdWhitespace(deBOMified);
        return clean.replaceAll("λ", "\\\\");
    }

    public static String removeWeirdWhitespace(String input) {
        String whitespace_chars = ""
                + "\\u0009" + "\\u000A" + "\\u000B" + "\\u000C" + "\\u000D" + "\\u0020"
                + "\\u0085" + "\\u00A0" + "\\u1680" + "\\u180E" + "\\u2000" + "\\u2001"
                + "\\u2002" + "\\u2003" + "\\u2004" + "\\u2005" + "\\u2006" + "\\u2007"
                + "\\u2008" + "\\u2009" + "\\u200A" + "\\u2028" + "\\u2029" + "\\u202F"
                + "\\u205F" + "\\u3000";
        Pattern whitespace = Pattern.compile(whitespace_chars);
        Matcher matcher = whitespace.matcher(input);
        String result = input;
        if (matcher.find()) {
            result = matcher.replaceAll(" ");
        }
        return result;
    }
}
