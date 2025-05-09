import java.text.ParseException;
import java.util.ArrayList;

public class Parser {
    private ArrayList<String> tokens;          
    private ArrayList<Byte> parens = new ArrayList<>(); 
    private String firstToken = "";

    // take in the array list
    public Expression parse(ArrayList<String> tokenList) throws ParseException {
        tokens = tokenList;
        if (tokens.size() == 0) {
            return new Variable("");  // empty
        }
        firstToken = tokens.get(0);
        return parseTree(null);  // send to parseTree to start parsing
    }
    private Expression parseTree(Expression exp) {
        if (tokens.size() == 0) {
            return exp;
        }

        String first = tokens.get(0);  // first token
        tokens = new ArrayList<>(tokens.subList(1, tokens.size())); // remove first element from subsequent tree

        // parsing for lambda
        if (first.equals("\\")) {
            Variable v = new Variable(tokens.get(0)); 
            tokens = new ArrayList<>(tokens.subList(2, tokens.size())); // 2 because skip dot
            Expression body = parseTree(null);            // Q3: Parse body of function
            Function f = new Function(v, body);        // creates function with just new tree
            return exp == null ? parseTree(f) : parseTree(new Application(exp, f)); // parses non-null rest of line
        }

    
        if (first.equals("(")) {
            parens.add((byte) 1); // casting 1 as a byte-type and then adding it to parens
            Expression inner = parseTree(null);          // Recursively parse inside parentheses
            return parseTree(exp == null ? inner : new Application(exp, inner)); // add paren
        }

        
        if (first.equals(")")) {
            parens.remove(0); // check close-paren
            return exp;  
        }

        Expression variable = new Variable(first);       // new variable
        return parseTree(exp == null ? variable : new Application(exp, variable)); 
    }

    // public Expression store (Expression exp) {
       
    // }
    
}


