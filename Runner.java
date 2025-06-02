import java.util.HashSet;
import java.util.Set;

public class Runner {

    public static Expression run(Expression exp) {
        if (exp instanceof Variable) {
            return exp;
        }

        if (exp instanceof Function) {
            Function f = (Function) exp;
            Expression reducedBody = run(f.getBody());
            return new Function(f.getParameter(), reducedBody);
        }

        if (exp instanceof Application) {
            Application app = (Application) exp;
            Expression func = run(app.getFunction());
            Expression arg = run(app.getArgument());

            if (func instanceof Function) {
                Function f = (Function) func;

                // Alpha-rename to avoid variable capture
                Expression renamedBody = alphaRename(f.getBody(), f.getParameter().toString(), new HashSet<>());
                Expression substituted = substitute(renamedBody, f.getParameter().toString(), arg);
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
            if (f.getParameter().toString().equals(varName)) {
                return f;
            }
            return new Function(f.getParameter(), substitute(f.getBody(), varName, value));
        }

        return body;
        
    }

        // Alpha-renaming: ensure no bound variable name conflicts with free vars in 'value'
    private static Expression alphaRename(Expression body, String oldName, Set<String> usedNames) {
        if (body instanceof Variable) {
            return body;
        }

        if (body instanceof Application) {
            Application app = (Application) body;
            return new Application(
                alphaRename(app.getFunction(), oldName, usedNames),
                alphaRename(app.getArgument(), oldName, usedNames)
            );
        }

        if (body instanceof Function) {
            Function f = (Function) body;
            String paramName = f.getParameter().toString();

            if (paramName.equals(oldName)) {
                // Already using the desired parameter name, no change needed
                return new Function(f.getParameter(), alphaRename(f.getBody(), oldName, usedNames));
            }

            // If the parameter name conflicts with used names, rename it
            if (usedNames.contains(paramName)) {
                String newName = generateUniqueName(usedNames);
                Variable newParam = new Variable(newName);
                Expression renamedBody = substitute(f.getBody(), paramName, newParam);
                usedNames.add(newName);
                return new Function(newParam, alphaRename(renamedBody, oldName, usedNames));
            }

            usedNames.add(paramName);
            return new Function(f.getParameter(), alphaRename(f.getBody(), oldName, usedNames));
        }

        return body;
    }

    // Utility to generate a unique variable name
    private static String generateUniqueName(Set<String> used) {
        String base = "v";
        int counter = 0;
        while (used.contains(base + counter)) {
            counter++;
        }
        return base + counter;
    }
    

}
