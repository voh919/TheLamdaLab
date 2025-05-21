public class Runner {

    public static Expression run(Expression exp) {
        // Base case 1: Variable returns itself
        if (exp instanceof Variable) {
            return exp;
        }

        // Base case 2: Application of non-function returns itself
        if (exp instanceof Application) {
            Application app = (Application) exp;
            if (!(app.getFunction() instanceof Function)) {
                return exp;
            }
        }

        // Base case 3: Function returns itself
        if (exp instanceof Function) {
            return exp;
        }

        // Default fallback (for later steps)
        return exp;
    }
}
