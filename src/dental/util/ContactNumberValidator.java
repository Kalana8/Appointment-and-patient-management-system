package dental.util;

import java.util.regex.Pattern;

/**
 * Shared contact-number validation helper (Task C, TDD demonstration -
 * see test/dental/util/ContactNumberValidatorTest.java, written first).
 *
 * Extracted from the phone-number regex that used to be defined inline
 * inside AppointmentService.validateAppointmentDetails(). PatientService
 * deliberately does NOT call isValid() from its search() method, since a
 * receptionist may search by a partial contact number (or by name
 * instead); strict format validation only applies where a contact number
 * is being registered or matched exactly, which is what
 * AppointmentService uses it for. Keeping the rule itself in one place
 * still means AppointmentService's registration check and any future
 * exact-match lookup share one definition rather than risking two regexes
 * drifting apart, matching the same separation-of-concerns reasoning
 * already used for DatabaseConnectionManager (Task B, 2.4.1) and
 * DaoFactory (2.4.2).
 */
public final class ContactNumberValidator {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^07\\d{8}$");

    private ContactNumberValidator() {
        // Static helper only - never instantiated.
    }

    /** True if candidate is exactly 10 digits, starting with 07. Null/blank is false, never an exception. */
    public static boolean isValid(String candidate) {
        if (candidate == null) {
            return false;
        }
        return PHONE_PATTERN.matcher(candidate).matches();
    }

    /** Trims surrounding whitespace only; does not otherwise alter the digits. Callers should validate separately. */
    public static String normalise(String candidate) {
        return candidate == null ? null : candidate.trim();
    }
}
