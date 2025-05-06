public class Application implements Expression {
    private Expression function;
    private Expression argument;

    public Application(Expression function, Expression argument) {
        this.function = function;
        this.argument = argument;
    }

    @Override
    public String toString() {
        return "(" + function.toString() + " " + argument.toString() + ")";
    }
}
