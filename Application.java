import java.util.HashMap;

public class Application implements Expression {
    private final Expression function;
    private final Expression argument;

    public Application(Expression function, Expression argument) {
        this.function = function;
        this.argument = argument;
    }

    @Override
    public String toString() {
        return "(" + function.toString() + " " + argument.toString() + ")";
    }
    public Expression inline(HashMap<String, Expression> stored) {
        return new Application(function.inline(stored), argument.inline(stored));
    }
}
