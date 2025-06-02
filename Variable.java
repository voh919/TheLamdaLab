import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Variable implements Expression {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public Expression inline(HashMap<String, Expression> stored) {
        if (stored.containsKey(name)) {
            return stored.get(name).inline(stored);
        }
        return this;
    }

    public Set<String> freeVars() {
        Set<String> vars = new HashSet<>();
        vars.add(name);
        return vars;
    }
}