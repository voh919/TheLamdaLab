import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Application implements Expression {
    private final Expression function;
    private final Expression argument;

    public Application(Expression function, Expression argument) {
        this.function = function;
        this.argument = argument;
    }

    public Expression getFunction() {
        return function;
    }

    public Expression getArgument() {
        return argument;
    }

    @Override
    public String toString() {
        String funcStr = function.toString();
        String argStr = argument.toString();
        
        // Only add parentheses around the argument if it's an application
        if (argument instanceof Application) {
            argStr = "(" + argStr + ")";
        }
        
        return funcStr + " " + argStr;
    }

    @Override
    public Expression inline(HashMap<String, Expression> stored) {
        return new Application(function.inline(stored), argument.inline(stored));
    }

    @Override
    public Set<String> freeVars() {
        Set<String> vars = new HashSet<>(function.freeVars());
        vars.addAll(argument.freeVars());
        return vars;
    }
}