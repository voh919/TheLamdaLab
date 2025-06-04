// Vivian Oh and Mia Subrahmanyam

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Runner {
    private static HashMap<String, Expression> stored = new HashMap<>();

    public static void setStored(HashMap<String, Expression> storedMap) {
        stored = storedMap;
    }

    public static Expression run(Expression exp) {
        return fullyReduce(exp);
    }
    
    public static Expression fullyReduce(Expression exp) {
        Expression previous;
        Expression current = exp;

        do {
            previous = current;
            current = reduceOneStep(previous);
        } while (!current.toString().equals(previous.toString()));

        return current;
    }

    private static Expression reduceOneStep(Expression exp) {
        if (exp instanceof Variable) {
            Variable var = (Variable) exp;
            Expression resolved = var.inline(stored);

            if (resolved != var) {
                return resolved;
            }
            return var;
        }

        if (exp instanceof Function) {
            Function f = (Function) exp;
            Expression reducedBody = reduceOneStep(f.getBody());

            if (!reducedBody.toString().equals(f.getBody().toString())) {
                return new Function(f.getParameter(), reducedBody);
            }
            return f;
        }

        if (exp instanceof Application) {
            Application app = (Application) exp;
            Expression func = reduceOneStep(app.getFunction());
            Expression arg = app.getArgument();

            if (func instanceof Function) {
                Function f = (Function) func;
                return substitute(f.getBody(), f.getParameter().getName(), arg);
            } 
            else {
                Expression reducedArg = reduceOneStep(arg);

                if (!func.toString().equals(app.getFunction().toString())) {
                    return new Application(func, arg);
                } 
                else if (!reducedArg.toString().equals(arg.toString())) {
                    return new Application(func, reducedArg);
                } 
                else {
                    return app;
                }
                
            }
        }

        return exp;
    }

    private static Expression substitute(Expression body, String paramName, Expression arg) {
        if (body instanceof Variable) {
            Variable var = (Variable) body;
            return var.getName().equals(paramName) ? arg : var;
        }

        if (body instanceof Function) {
            Function f = (Function) body;
            String param = f.getParameter().getName();

            if (param.equals(paramName)) {
                return f;
            }

            Set<String> freeVars = arg.freeVars();
            if (freeVars.contains(param)) {
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