
import java.text.ParseException;
import java.util.ArrayList;

public class Parser {
    private ArrayList<String> tokens;          // Used for Q1: parsing an ArrayList of tokens
    private ArrayList<Byte> parens = new ArrayList<>(); // Used for Q5: handling parentheses
    private String firstToken = "";

    // Q1: Entry point - Takes ArrayList of tokens and starts parsing
    public Expression parse(ArrayList<String> tokenList) throws ParseException {
        tokens = tokenList;
        if (tokens.size() == 0) {
            return new Variable("");  // Q4: Edge case for empty input
        }
        firstToken = tokens.get(0);
        return parseTree(null);  // Begin recursive parsing
    }

    // Q2: Recursively builds Expression parse tree
    private Expression parseTree(Expression exp) {
        if (tokens.size() == 0) {
            return exp; // Base case for recursion
        }

        String first = tokens.get(0);  // Get next token
        tokens = new ArrayList<>(tokens.subList(1, tokens.size())); // Remove it

        // Q3: If token is "\", begin parsing a lambda function
        if (first.equals("\\")) {
            Variable v = new Variable(tokens.get(0));  // Q3: Lambda parameter
            tokens = new ArrayList<>(tokens.subList(2, tokens.size())); // skip var and '.'
            Expression body = parseTree(null);            // Q3: Parse body of function
            Function f = new Function(v, body);        // Q3: Construct Function node
            return exp == null ? parseTree(f) : parseTree(new Application(exp, f)); // Q6: Apply if needed
        }

        // Q5: Handle open parenthesis - parse subexpression inside
        if (first.equals("(")) {
            parens.add((byte) 1); // casting 1 as a byte-type and then adding it to parens
            Expression inner = parseTree(null);          // Recursively parse inside parentheses
            return parseTree(exp == null ? inner : new Application(exp, inner)); // Q6: Apply if needed
        }

        // Q5: Handle close parenthesis - exit current nested expression
        if (first.equals(")")) {
            parens.remove(0); 
            return exp;  // Finish subexpression
        }

        // Q4 & Q6: If token is a variable or next to another expression, it's an application
        Expression variable = new Variable(first);       // Q4: Construct Variable node
        return parseTree(exp == null ? variable : new Application(exp, variable)); // Q6: Apply if needed
    }
}








// old pre-parser code
     // Pre-parser that wraps raw lambda functions in parentheses
// 	public static ArrayList<String> addParens(ArrayList<String> tokens) {
// 		ArrayList<String> result = new ArrayList<>();
// 		int i = 0;

//     while (i < tokens.size()) {
//         String token = tokens.get(i);

//         if (token.equals("\\") || token.equals("λ")) {
//             result.add("(");        // open parentheses for lambda
//             result.add(token);      // add \ or λ

//             i++;
//             if (i < tokens.size()) result.add(tokens.get(i++)); // var
//             if (i < tokens.size() && tokens.get(i).equals(".")) result.add(tokens.get(i++));

//             int parenDepth = 0;
//             while (i < tokens.size()) {
//                 String t = tokens.get(i);

//                 // Detect lambda body boundaries
//                 if (t.equals("(")) parenDepth++;
//                 if (t.equals(")")) {
//                     if (parenDepth == 0) break;
//                     parenDepth--;
//                 }

//                 result.add(t);
//                 i++;
//             }

//             result.add(")"); // close the lambda
//         } else {
//             result.add(token);
//             i++;
//         }
//     }

//     return result;
// }