
import java.text.ParseException;
import java.util.ArrayList;

public class Parser {

	public static ArrayList<String> preParse(ArrayList<String> tokens) {
		for (int i = 0; i < tokens.size(); i++) {
			String x = tokens.get(i);
			
			if(x.equals("\\")) {
				tokens.add(i, "(");
				tokens.add(tokens.size()-1, ")");
			}

		}
		return tokens;
	}
	
	/*
	 * Turns a set of tokens into an expression.  Comment this back in when you're ready.
	 */
	public Expression parse(ArrayList<String> tokens) throws ParseException {
		Variable var = new Variable(tokens.get(0));
		
		// This is nonsense code, just to show you how to thrown an Exception.
		// To throw it, type "error" at the console.
		if (var.toString().equals("error")) {
			throw new ParseException("User typed \"Error\" as the input!", 0);
		}
		
		return var;
	}
}
