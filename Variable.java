import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Variable implements Expression {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Expression inline(HashMap<String, Expression> stored) {
        if (stored.containsKey(name)) {
            return stored.get(name).inline(stored);
        }
        return this;
    }

    @Override
    public Set<String> freeVars() {
        HashSet<String> vars = new HashSet<>();
        vars.add(name);
        return vars;
    }

    // Equals and hashCode help avoid issues in Sets and Maps
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Variable)) return false;
        Variable other = (Variable) o;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
