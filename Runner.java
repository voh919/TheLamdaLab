public class Runner {

    public static Expression run(Expression exp) {
        // Inline definitions, if any (optional, can skip if unused)
        return reduce(exp);
    }

    private static Expression reduce(Expression exp) {
        if (exp instanceof Variable) {
            return exp;
        }

        if (exp instanceof Function) {
            Function f = (Function) exp;
            Expression reducedBody = reduce(f.getBody());
            return new Function(f.getParameter(), reducedBody);
        }

        if (exp instanceof Application) {
            Application app = (Application) exp;
            Expression function = reduce(app.getFunction());
            Expression argument = reduce(app.getArgument());

            if (function instanceof Function) {
                Function f = (Function) function;
                Expression substituted = substitute(f.getBody(), f.getParameter().toString(), argument);
                return reduce(substituted);  // Keep reducing after substitution
            }

            return new Application(function, argument);
        }

        return exp;  // fallback
    }

    private static Expression substitute(Expression body, String varName, Expression replacement) {
        if (body instanceof Variable) {
            Variable v = (Variable) body;
            return v.toString().equals(varName) ? replacement : v;
        }

        if (body instanceof Function) {
            Function f = (Function) body;
            String paramName = f.getParameter().toString();

            // Avoid variable capture
            if (paramName.equals(varName)) {
                return f;  // Skip substitution inside shadowed scope
            }

            Expression newBody = substitute(f.getBody(), varName, replacement);
            return new Function(f.getParameter(), newBody);
        }

        if (body instanceof Application) {
            Application app = (Application) body;
            Expression newFunc = substitute(app.getFunction(), varName, replacement);
            Expression newArg = substitute(app.getArgument(), varName, replacement);
            return new Application(newFunc, newArg);
        }

        return body;
    }
}
