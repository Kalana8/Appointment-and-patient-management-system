package dental.service;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pure unit tests for ValidationException, the mechanism behind Task B's
 * "implement proper validation mechanisms" requirement (2.6): it must
 * carry every individual error so the Controller can redisplay them all
 * at once, and its message must be human-readable for logs.
 */
public class ValidationExceptionTest {

    @Test
    void getErrorsReturnsExactlyTheListItWasConstructedWith() {
        List<String> errors = Arrays.asList("Patient name is required.", "Dentist name is required.");
        ValidationException ex = new ValidationException(errors);
        assertEquals(2, ex.getErrors().size());
        assertTrue(ex.getErrors().contains("Patient name is required."));
        assertTrue(ex.getErrors().contains("Dentist name is required."));
    }

    @Test
    void messageJoinsAllErrorsForLogging() {
        List<String> errors = Arrays.asList("Contact number is required.", "Appointment date is required.");
        ValidationException ex = new ValidationException(errors);
        assertEquals("Contact number is required.; Appointment date is required.", ex.getMessage());
    }

    @Test
    void singleErrorListWorks() {
        ValidationException ex = new ValidationException(List.of("Treatment type must be selected."));
        assertEquals(1, ex.getErrors().size());
    }
}
