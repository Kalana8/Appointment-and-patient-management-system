package dental.service;

import dental.model.Appointment;
import dental.model.Patient;
import dental.model.TreatmentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Integration tests for AppointmentService: validateAppointmentDetails()
 * («include» step from Task A, Figure 1), registerAppointment() (realises
 * Task A, Figure 4), searchAppointment() (Task A, Figure 5) and
 * updateStatus() (the new «extends» use case, Task A 1.4 revision /
 * Figure 5).
 *
 * Requirements traced: FR2 "Register New Appointment", FR3 "Display
 * Appointment Details" (assignment brief), plus this revision's
 * appointment-status-management addition.
 */
public class AppointmentServiceTest {

    private final AppointmentService appointmentService = new AppointmentService();

    // Appointment numbers created by this test class, cleaned up in
    // @AfterEach so re-running the suite never accumulates leftover rows.
    private final List<String> createdAppointmentNumbers = new ArrayList<>();

    @AfterEach
    void cleanUp() throws Exception {
        try (java.sql.Connection conn = dental.util.DatabaseConnectionManager.getInstance().getConnection()) {
            for (String number : createdAppointmentNumbers) {
                try (java.sql.PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM appointment WHERE appointment_number = ?")) {
                    ps.setString(1, number);
                    ps.executeUpdate();
                }
            }
            // Only ever removes the JUnit fixture patient this class itself
            // creates (0770000001) -- never touches real clinic data, and
            // is a no-op once no appointment references it any more.
            try (java.sql.PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM patient WHERE contact_number = ? "
                    + "AND patient_id NOT IN (SELECT patient_id FROM appointment)")) {
                ps.setString(1, "0770000001");
                ps.executeUpdate();
            }
        }
        createdAppointmentNumbers.clear();
    }

    private Patient validPatient() {
        Patient p = new Patient();
        p.setName("JUnit Fixture Patient");
        p.setAddress("1 Test Lane, Colombo");
        p.setContactNumber("0770000001");
        return p;
    }

    // ---------- validateAppointmentDetails() ----------

    @Test
    @DisplayName("A fully valid appointment produces zero validation errors")
    void validAppointmentHasNoErrors() {
        List<String> errors = appointmentService.validateAppointmentDetails(
                validPatient(), "Dr. Test", new TreatmentType(1, "Dental Checkup", new BigDecimal("1500")),
                LocalDate.now().plusDays(1), LocalTime.of(10, 0));
        assertTrue(errors.isEmpty(), "Expected no validation errors, got: " + errors);
    }

    @Test
    @DisplayName("Every required field left blank/null produces its own error message")
    void allFieldsMissingProducesAllErrors() {
        List<String> errors = appointmentService.validateAppointmentDetails(
                null, "", null, null, null);
        // 7 checks: patient name, patient address, contact number, dentist
        // name, treatment type, appointment date, appointment time.
        assertEquals(7, errors.size(), "One error per missing field: " + errors);
    }

    @Test
    @DisplayName("An invalid contact number format is rejected (delegates to ContactNumberValidator)")
    void invalidContactNumberFormatIsRejected() {
        Patient p = validPatient();
        p.setContactNumber("12345");
        List<String> errors = appointmentService.validateAppointmentDetails(
                p, "Dr. Test", new TreatmentType(1, "Dental Checkup", new BigDecimal("1500")),
                LocalDate.now().plusDays(1), LocalTime.of(10, 0));
        assertTrue(errors.stream().anyMatch(e -> e.contains("valid Sri Lankan mobile number")));
    }

    @Test
    @DisplayName("A past appointment date is rejected")
    void pastDateIsRejected() {
        List<String> errors = appointmentService.validateAppointmentDetails(
                validPatient(), "Dr. Test", new TreatmentType(1, "Dental Checkup", new BigDecimal("1500")),
                LocalDate.now().minusDays(1), LocalTime.of(10, 0));
        assertTrue(errors.stream().anyMatch(e -> e.contains("cannot be in the past")));
    }

    // ---------- registerAppointment() / searchAppointment() ----------

    @Test
    @DisplayName("registerAppointment persists the appointment and generates a findable appointment number")
    void registerThenSearchRoundTrip() throws ValidationException {
        String number = appointmentService.registerAppointment(
                validPatient(), "Dr. JUnit", new TreatmentType(1, "Dental Checkup", new BigDecimal("1500")),
                LocalDate.now().plusDays(2), LocalTime.of(9, 30), 1);
        createdAppointmentNumbers.add(number);

        assertNotNull(number);
        assertTrue(number.matches("APT-\\d+"), "Appointment number should follow the APT-nnnnn format: " + number);

        Appointment found = appointmentService.searchAppointment(number);
        assertNotNull(found);
        assertEquals(number, found.getAppointmentNumber());
        assertEquals("Dr. JUnit", found.getDentistName());
        assertEquals("SCHEDULED", found.getStatus());
    }

    @Test
    @DisplayName("registerAppointment throws ValidationException rather than persisting invalid input")
    void invalidRegistrationThrowsAndDoesNotPersist() {
        assertThrows(ValidationException.class, () -> appointmentService.registerAppointment(
                null, "", null, null, null, 1));
    }

    @Test
    @DisplayName("searchAppointment returns null for an appointment number that does not exist")
    void searchingAnUnknownAppointmentNumberReturnsNull() {
        assertNull(appointmentService.searchAppointment("APT-99999999"));
    }

    // ---------- updateStatus() (new «extends» use case) ----------

    @Test
    @DisplayName("updateStatus marks a SCHEDULED appointment COMPLETED and it is visible on the next read")
    void updateStatusToCompletedPersists() throws ValidationException {
        String number = appointmentService.registerAppointment(
                validPatient(), "Dr. JUnit", new TreatmentType(1, "Dental Checkup", new BigDecimal("1500")),
                LocalDate.now().plusDays(3), LocalTime.of(14, 0), 1);
        createdAppointmentNumbers.add(number);

        boolean updated = appointmentService.updateStatus(number, "COMPLETED");
        assertTrue(updated);

        Appointment reloaded = appointmentService.searchAppointment(number);
        assertEquals("COMPLETED", reloaded.getStatus());
    }

    @Test
    @DisplayName("updateStatus on an unknown appointment number returns false rather than throwing")
    void updateStatusOnUnknownAppointmentReturnsFalse() {
        assertFalse(appointmentService.updateStatus("APT-99999999", "CANCELLED"));
    }
}
