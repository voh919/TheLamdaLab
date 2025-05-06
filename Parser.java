
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
        return _parse(null);  // Begin recursive parsing
    }

    // Q2: Recursively builds Expression parse tree
    private Expression _parse(Expression exp) {
        if (tokens.size() == 0) {
            return exp; // Base case for recursion
        }

        String s = tokens.get(0);  // Get next token
        tokens = new ArrayList<>(tokens.subList(1, tokens.size())); // Remove it

        // Q3: If token is "\", begin parsing a lambda function
        if (s.equals("\\")) {
            Variable v = new Variable(tokens.get(0));  // Q3: Lambda parameter
            tokens = new ArrayList<>(tokens.subList(2, tokens.size())); // skip var and '.'
            Expression body = _parse(null);            // Q3: Parse body of function
            Function f = new Function(v, body);        // Q3: Construct Function node
            return exp == null ? _parse(f) : _parse(new Application(exp, f)); // Q6: Apply if needed
        }

        // Q5: Handle open parenthesis - parse subexpression inside
        if (s.equals("(")) {
            parens.add((byte) 1); 
            Expression inner = _parse(null);          // Recursively parse inside parentheses
            return _parse(exp == null ? inner : new Application(exp, inner)); // Q6: Apply if needed
        }

        // Q5: Handle close parenthesis - exit current nested expression
        if (s.equals(")")) {
            parens.remove(0); 
            return exp;  // Finish subexpression
        }

        // Q4 & Q6: If token is a variable or next to another expression, it's an application
        Expression variable = new Variable(s);       // Q4: Construct Variable node
        return _parse(exp == null ? variable : new Application(exp, variable)); // Q6: Apply if needed
    }
}
