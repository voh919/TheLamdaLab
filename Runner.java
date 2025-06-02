import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Runner {
    private static HashMap<String, Expression> stored = new HashMap<>();

    // Method to set stored definitions (called from Parser)
    public static void setStored(HashMap<String, Expression> storedMap) {
        stored = storedMap;
    }

    public static Expression run(Expression exp) {
        if (exp instanceof Variable) {
            Variable var = (Variable) exp;
            // Use the inline method to resolve stored variables
            Expression resolved = var.inline(stored);
            if (resolved != var) {
                // If the variable was resolved to something else, run that
                return run(resolved);
            }
            return var; // Return the variable if it's not stored
        }

        if (exp instanceof Function) {
            Function f = (Function) exp;
            return new Function(f.getParameter(), fullyReduce(f.getBody()));
        }

        if (exp instanceof Application) {
            Application app = (Application) exp;
            Expression func = run(app.getFunction());
            Expression arg = app.getArgument();

            if (func instanceof Function) {
                Function f = (Function) func;
                return fullyReduce(substitute(f.getBody(), f.getParameter().getName(), arg));
            } else {
                return new Application(func, arg);
            }
        }

        return exp;
    }
    
    public static Expression fullyReduce(Expression exp) {
        Expression previous;
        Expression current = exp;

        do {
            previous = current;
            current = run(previous);
        } while (!current.toString().equals(previous.toString()));

        return current;
    }

    private static Expression substitute(Expression body, String paramName, Expression arg) {
        // Perform capture-avoiding substitution
        if (body instanceof Variable) {
            Variable var = (Variable) body;
            return var.getName().equals(paramName) ? arg : var;
        }

        if (body instanceof Function) {
            Function f = (Function) body;
            String param = f.getParameter().getName();

            if (param.equals(paramName)) {
                return f; // No substitution, shadowed
            }

            Set<String> freeVars = arg.freeVars();
            if (freeVars.contains(param)) {
                // Perform alpha conversion to avoid variable capture
                String newParam = generateFreshVariable(param, body, arg);
                Expression renamedBody = substitute(f.getBody(), param, new Variable(newParam));
                return new Function(new Variable(newParam), substitute(renamedBody, paramName, arg));
            }

            return new Function(f.getParameter(), substitute(f.getBody(), paramName, arg));
        }

        if (body instanceof Application) {
            Application app = (Application) body;
            return new Application(
                substitute(app.getFunction(), paramName, arg),
                substitute(app.getArgument(), paramName, arg)
            );
        }

        return body;
    }

    private static String generateFreshVariable(String base, Expression... exprs) {
        Set<String> allVars = new HashSet<>();
        for (Expression e : exprs) {
            allVars.addAll(e.freeVars());
        }

        int counter = 1;
        String candidate = base + counter;
        while (allVars.contains(candidate)) {
            counter++;
            candidate = base + counter;
        }

        return candidate;
    }
}