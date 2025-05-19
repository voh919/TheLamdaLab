import java.util.HashMap;

public class Function implements Expression {
    private final Variable parameter;
    private final Expression body;

    public Function(Variable parameter, Expression body) {
        this.parameter = parameter;
        this.body = body;
    }

    @Override
    public String toString() {
        return "(λ" + parameter.toString() + "." + body.toString() + ")";
    }

    public Expression inline(HashMap<String, Expression> stored) {
        return new Function(parameter, body.inline(stored));
    }
}
