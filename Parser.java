import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
    private ArrayList<String> tokens;          
    private ArrayList<Byte> parens = new ArrayList<>(); 
    private String firstToken = "";
    private static HashMap<String, Expression> stored = new HashMap<>();  // Made static


    // take in the array list
    public Expression parse(ArrayList<String> tokenList) throws ParseException {
    tokens = tokenList;
    if (tokens.size() == 0) {
        return new Variable("");  // empty
    }

    // Check if it's a definition like: name = expression
    if (tokens.size() > 2 && tokens.get(1).equals("=")) {
        String name = tokens.get(0);
        ArrayList<String> rest = new ArrayList<>(tokens.subList(2, tokens.size()));
        Expression parsed = parse(rest);  // Use same instance, not new Parser()
        if (stored.containsKey(name)){
            System.out.println(name + " is already defined.");
        }
        else {
            stored.put(name, parsed);
            System.out.println("Added " + parsed.inline(stored) + " as " + name);
        }
        return parsed;
    }

    firstToken = tokens.get(0);
    return parseTree(null);
}

    private Expression parseTree(Expression exp) {
    if (tokens.size() == 0) {
        return exp;
    }

    String first = tokens.get(0);
    tokens = new ArrayList<>(tokens.subList(1, tokens.size()));

    if (first.equals("\\")) {
    Variable v = new Variable(tokens.get(0));
    tokens = new ArrayList<>(tokens.subList(2, tokens.size())); // skip dot
    Expression body = parseTree(null);
    Function f = new Function(v, body);
    return exp == null ? f : new Application(exp, f);
    }


    if (first.equals("(")) {
        parens.add((byte) 1);
        Expression inner = parseTree(null);
        return parseTree(exp == null ? inner : new Application(exp, inner));
    }

    if (first.equals(")")) {
        parens.remove(0);
        return exp;
    }
    // Special built-in: run (like a keyword)
    if (first.equals("run")) {
        Expression toRun = parseTree(null); // get the next expression
        // Inline any stored variables before running
        Expression inlined = toRun.inline(stored);
        Expression result = Runner.run(inlined); // evaluate it
        return parseTree(exp == null ? result : new Application(exp, result));
    }

    // Check if it's a stored expression
    Expression variable;
    if (stored.containsKey(first)) {
        variable = stored.get(first);
    } else {
        variable = new Variable(first);
    }

    return parseTree(exp == null ? variable : new Application(exp, variable));
}
}