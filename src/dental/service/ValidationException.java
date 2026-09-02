package dental.service;

import java.util.List;

/**
 * Thrown by the Service layer when user-supplied input fails validation
 * (Task B requirement: "implement proper validation mechanisms in order
 * to restrict invalid entries to the system"). Carries every individual
 * validation failure so the Controller (Servlet) can redisplay them all
 * at once rather than one at a time.
 */
public class ValidationException extends Exception {

    private final List<String> errors;

    public ValidationException(List<String> errors) {
        super(String.join("; ", errors));
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
