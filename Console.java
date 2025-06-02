// λ
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Console {

    // Scanner for user input
    private static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
   
     Map<String, Expression> dictionary = new HashMap<>();
    Lexer lexer = new Lexer();
    Parser parser = new Parser();

    while (true) {
        String input = cleanConsoleInput();

        if (input.equalsIgnoreCase("exit")) {
            break;
        }

        try {
            ArrayList<String> tokens = lexer.tokenize(input);

            // Handle "run" before parsing
            if (!tokens.isEmpty() && tokens.get(0).equals("run")) {
                tokens.remove(0);
                ArrayList<String> resolved = new ArrayList<>();
                for (String t : tokens) {
                    if (dictionary.containsKey(t)) {
                        resolved.addAll(lexer.tokenize(dictionary.get(t).toString()));
                    } else {
                        resolved.add(t);
                    }
                }
                Expression expr = parser.parse(resolved);
                Expression reduced = Runner.run(expr);

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

            // Handle assignment like: name = \f.\x.x
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
                Expression rhsExp = parser.parse(rhsTokens);
                dictionary.put(name, rhsExp);
                System.out.println("Added " + rhsExp.toString() + " as " + name);
                continue;
            }

            ArrayList<String> resolved = new ArrayList<>();
            for (String t : tokens) {
                if (dictionary.containsKey(t)) {
                    resolved.addAll(lexer.tokenize(dictionary.get(t).toString()));
                } else {
                    resolved.add(t);
                }
            }

            Expression expr = parser.parse(resolved);
            System.out.println(expr.toString());

        } catch (Exception e) {
            System.out.println("Error: Could not parse input: \"" + input + "\"");
            System.out.println("Details: " + e.getMessage());
        }
    }

    System.out.println("Goodbye!");
}


	
	
	/*
	 * Collects user input, and ...
	 * ... does a bit of raw string processing to (1) strip away comments,  
	 * (2) remove the BOM character that appears in unicode strings in Windows,
	 * (3) turn all weird whitespace characters into spaces,
	 * and (4) replace all λs with backslashes.
	 */
	
	private static String cleanConsoleInput() {
		System.out.print("> ");
		String raw = in.nextLine();
		String deBOMified = raw.replaceAll("\uFEFF", ""); // remove Byte Order Marker from UTF

		String clean = removeWeirdWhitespace(deBOMified);
		
		return clean.replaceAll("λ", "\\\\");
	}
	
	
	public static String removeWeirdWhitespace(String input) {
		String whitespace_chars = 
				""           // dummy empty string for homogeneity
				+ "\\u0009"  // CHARACTER TABULATION
				+ "\\u000A"  // LINE FEED (LF)
				+ "\\u000B"  // LINE TABULATION
				+ "\\u000C"  // FORM FEED (FF)
				+ "\\u000D"  // CARRIAGE RETURN (CR)
				+ "\\u0020"  // SPACE
				+ "\\u0085"  // NEXT LINE (NEL) 
				+ "\\u00A0"  // NO-BREAK SPACE
				+ "\\u1680"  // OGHAM SPACE MARK
				+ "\\u180E"  // MONGOLIAN VOWEL SEPARATOR
				+ "\\u2000"  // EN QUAD 
				+ "\\u2001"  // EM QUAD 
				+ "\\u2002"  // EN SPACE
				+ "\\u2003"  // EM SPACE
				+ "\\u2004"  // THREE-PER-EM SPACE
				+ "\\u2005"  // FOUR-PER-EM SPACE
				+ "\\u2006"  // SIX-PER-EM SPACE
				+ "\\u2007"  // FIGURE SPACE
				+ "\\u2008"  // PUNCTUATION SPACE
				+ "\\u2009"  // THIN SPACE
				+ "\\u200A"  // HAIR SPACE
				+ "\\u2028"  // LINE SEPARATOR
				+ "\\u2029"  // PARAGRAPH SEPARATOR
				+ "\\u202F"  // NARROW NO-BREAK SPACE
				+ "\\u205F"  // MEDIUM MATHEMATICAL SPACE
				+ "\\u3000"; // IDEOGRAPHIC SPACE 
		Pattern whitespace = Pattern.compile(whitespace_chars);
		Matcher matcher = whitespace.matcher(input);
		String result = input;
		if (matcher.find()) {
			result = matcher.replaceAll(" ");
		}

		return result;
	}

}
