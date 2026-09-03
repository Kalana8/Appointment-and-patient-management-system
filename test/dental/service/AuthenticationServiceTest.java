package dental.service;

import dental.model.StaffUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Integration test (real DAO, real database -- see run-tests.sh for the
 * -Ddental.db.name=sunrise_dental_clinic_test flag) for AuthenticationService,
 * which realises the "Login" use case (Task A, Figure 1) and its Sequence
 * Diagram (Task A, Figure 3). Fixture data comes from
 * database/sample_data.sql applied to the test schema, matching what a
 * fresh clone of this project already ships with.
 *
 * Requirement traced: FR1 "User Authentication (Login)" (assignment brief).
 */
public class AuthenticationServiceTest {

    private final AuthenticationService authService = new AuthenticationService();

    @Test
    @DisplayName("Valid receptionist credentials authenticate successfully")
    void validReceptionistCredentialsSucceed() {
        StaffUser user = authService.authenticate("receptionist1", "recep123");
        assertNotNull(user, "receptionist1/recep123 is seeded by sample_data.sql and must authenticate");
        assertEquals("RECEPTIONIST", user.getRole());
        assertEquals("Nadeesha Perera", user.getFullName());
    }

    @Test
    @DisplayName("Valid administrator credentials authenticate successfully")
    void validAdministratorCredentialsSucceed() {
        StaffUser user = authService.authenticate("admin1", "admin123");
        assertNotNull(user);
        assertEquals("ADMINISTRATOR", user.getRole());
    }

    @Test
    @DisplayName("Wrong password for a real username returns null, not an exception")
    void wrongPasswordReturnsNull() {
        assertNull(authService.authenticate("receptionist1", "wrongpassword"));
    }

    @Test
    @DisplayName("Unknown username returns null")
    void unknownUsernameReturnsNull() {
        assertNull(authService.authenticate("no_such_user", "anything"));
    }

    @Test
    @DisplayName("Blank/null credentials are rejected before any DAO lookup")
    void blankCredentialsReturnNullWithoutThrowing() {
        assertNull(authService.authenticate("", "recep123"));
        assertNull(authService.authenticate("receptionist1", ""));
        assertNull(authService.authenticate(null, null));
    }
}
