public class Function implements Expression {
    private final Variable parameter;
    private final Expression body;

    public Function(Variable parameter, Expression body) {
        this.parameter = parameter;
        this.body = body;
    }

    @Override
    public String toString() {
        return "λ" + parameter + "." + body.toString();
    }
}
