import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Function implements Expression {
    private final Variable parameter;
    private final Expression body;

    public Function(Variable parameter, Expression body) {
        this.parameter = parameter;
        this.body = body;
    }

    public Variable getParameter() {
        return parameter;
    }

    public Expression getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "(λ" + parameter + "." + body + ")";
    }

    @Override
    public Expression inline(HashMap<String, Expression> stored) {
        return new Function(parameter, body.inline(stored));
    }

    @Override
    public Set<String> freeVars() {
        HashSet<String> free = new HashSet<>(body.freeVars());
        free.remove(parameter.getName());
        return free;
    }
}
