package dental.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TDD demonstration (Task C rationale, section 3.2): this test class was
 * written BEFORE dental.util.ContactNumberValidator existed. The class did
 * not compile the first time this suite was run (red), was then created
 * with the minimum logic to satisfy every case below (green), and the
 * duplicate phone-format regex previously inlined in AppointmentService was
 * refactored to call it instead (refactor) -- the classic JUnit red-green-
 * refactor cycle taught generically in software engineering practice, kept
 * here as a genuine, reproducible example rather than only claimed in
 * prose.
 *
 * Business rule under test (unchanged from AppointmentService's original
 * inline regex): a valid Sri Lankan mobile number is exactly 10 digits,
 * starting with "07".
 */
public class ContactNumberValidatorTest {

    @ParameterizedTest(name = "\"{0}\" is a valid contact number")
    @ValueSource(strings = {"0771234567", "0701234567", "0759999999", "0712223333"})
    void validNumbersAreAccepted(String candidate) {
        assertTrue(ContactNumberValidator.isValid(candidate),
                candidate + " should be accepted (10 digits, starts with 07)");
    }

    @ParameterizedTest(name = "\"{0}\" is rejected")
    @ValueSource(strings = {
            "123456789",     // does not start with 07
            "07712345",      // too short
            "077123456789",  // too long
            "0771234abc",    // non-numeric
            "0871234567",    // wrong prefix (08, not 07)
            " 0771234567",   // leading whitespace not trimmed by the caller
    })
    void invalidNumbersAreRejected(String candidate) {
        assertFalse(ContactNumberValidator.isValid(candidate),
                candidate + " should be rejected");
    }

    @ParameterizedTest(name = "null/blank input \"{0}\" is rejected, not thrown")
    @NullAndEmptySource
    void nullAndBlankAreRejectedNotThrown(String candidate) {
        assertFalse(ContactNumberValidator.isValid(candidate));
    }

    @Test
    void trimmedInputWithSurroundingWhitespaceIsAccepted() {
        assertTrue(ContactNumberValidator.isValid("0771234567".trim()));
        // normalise() is the helper AppointmentService/PatientService call
        // before storing or searching, so callers never have to remember
        // to trim() themselves.
        assertTrue(ContactNumberValidator.isValid(ContactNumberValidator.normalise("  0771234567  ")));
    }

    @Test
    void normaliseTrimsWithoutChangingValidDigits() {
        assertTrue(ContactNumberValidator.normalise("  0712223333  ").equals("0712223333"));
    }

    @CsvSource({
            "0771234567, true",
            "0991234567, false",
    })
    @ParameterizedTest(name = "isValid({0}) == {1}")
    void tableDrivenSpotChecks(String candidate, boolean expected) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, ContactNumberValidator.isValid(candidate));
    }
}
