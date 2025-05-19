import java.util.HashMap;

public interface Expression {
    public abstract String toString();  // Pretty printed
    public abstract Expression inline(HashMap<String, Expression> stored);  // Inlines stored variables
}
