// Updated Runner.java with precise alpha-conversion and minimal renaming
import java.util.*;

public class Runner {

    public static Expression run(Expression exp) {
        if (exp instanceof Variable) {
            return exp;
        }

        if (exp instanceof Function) {
            Function f = (Function) exp;
            return new Function(f.getParameter(), run(f.getBody()));
        }

        if (exp instanceof Application) {
            Application app = (Application) exp;
            Expression func = run(app.getFunction());
            Expression arg = run(app.getArgument());

            if (func instanceof Function) {
                Function f = (Function) func;
                String paramName = f.getParameter().toString();
                Set<String> argFreeVars = arg.freeVars();

                Expression bodyToUse = f.getBody();

                // Rename if the paramName is in the argument's free vars
                if (argFreeVars.contains(paramName)) {
                    String newName = generateFreshName(paramName, argFreeVars, bodyToUse.freeVars());
                    Variable newParam = new Variable(newName);
                    bodyToUse = substitute(bodyToUse, paramName, newParam);
                    f = new Function(newParam, bodyToUse); // replace function with renamed one
                    paramName = newName;
                }

                Expression substituted = substitute(f.getBody(), paramName, arg);
                return run(substituted);
            }

            return new Application(func, arg);
        }

        return exp;
    }

    private static Expression substitute(Expression body, String varName, Expression value) {
        if (body instanceof Variable) {
            Variable v = (Variable) body;
            return v.toString().equals(varName) ? value : v;

        } else if (body instanceof Application) {
            Application app = (Application) body;
            return new Application(
                substitute(app.getFunction(), varName, value),
                substitute(app.getArgument(), varName, value)
            );

        } else if (body instanceof Function) {
            Function f = (Function) body;
            String param = f.getParameter().toString();

            if (param.equals(varName)) {
                return f; // Skip substitution due to shadowing
            }

            Set<String> valueFree = value.freeVars();
            Set<String> bodyFree = f.getBody().freeVars();

            if (valueFree.contains(param)) {
                String newName = generateFreshName(param, valueFree, bodyFree);
                Variable newParam = new Variable(newName);
                Expression renamedBody = substitute(f.getBody(), param, newParam);
                Expression newBody = substitute(renamedBody, varName, value);
                return new Function(newParam, newBody);
            } else {
                return new Function(f.getParameter(), substitute(f.getBody(), varName, value));
            }
        }

        return body;
    }

    private static String generateFreshName(String base, Set<String>... avoidSets) {
        int i = 1;
        while (true) {
            final String candidate = base + i;
            boolean conflict = false;
            for (Set<String> set : avoidSets) {
                if (set.contains(candidate)) {
                    conflict = true;
                    break;
                }
            }
            if (!conflict) return candidate;
            i++;
        }
    }
}
