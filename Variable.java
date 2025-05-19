import java.util.HashMap;

public class Variable implements Expression {
	private String name;
	
	public Variable(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}

	public Expression inline(HashMap<String, Expression> stored) {
        if (stored.containsKey(name)) {
            return stored.get(name).inline(stored);  // Recursive inlining
        }
        return this;
    }

}
