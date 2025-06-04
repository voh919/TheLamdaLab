// Vivian Oh and Mia Subrahmanyam

import java.util.HashMap;
import java.util.Set;

public interface Expression {
    String toString();
    Expression inline(HashMap<String, Expression> stored);
    Set<String> freeVars();
}