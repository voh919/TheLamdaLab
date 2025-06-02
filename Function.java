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

    public String toString() {
        return "(\u03bb" + parameter.toString() + "." + body.toString() + ")";
    }

    public Expression inline(HashMap<String, Expression> stored) {
        return new Function(parameter, body.inline(stored));
    }

    public Set<String> freeVars() {
        Set<String> vars = new HashSet<>(body.freeVars());
        vars.remove(parameter.toString());
        return vars;
    }
}