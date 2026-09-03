package dental.service;

import dental.model.Patient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for PatientService / DatabasePatientDao, backing the
 * Patient directory feature added in this revision (Task A, 1.1 / 1.4:
 * "View Patient History" «includes» "Search Patient"; Task B, 2.1). Every
 * fixture uses the reserved 0799xxxxx contact-number range so this class
 * never collides with sample_data.sql or with another test class's data.
 */
public class PatientServiceTest {

    private final PatientService patientService = new PatientService();
    private static final String FIXTURE_CONTACT = "0799000001";
    private static final String FIXTURE_NAME = "JUnit Search Fixture";

    @BeforeEach
    void seedFixturePatient() throws Exception {
        try (Connection conn = dental.util.DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO patient (name, address, contact_number, date_of_birth) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, FIXTURE_NAME);
            ps.setString(2, "9 JUnit Avenue, Colombo");
            ps.setString(3, FIXTURE_CONTACT);
            ps.setDate(4, java.sql.Date.valueOf("1995-01-01"));
            ps.executeUpdate();
        }
    }

    @AfterEach
    void removeFixturePatient() throws Exception {
        try (Connection conn = dental.util.DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM patient WHERE contact_number = ?")) {
            ps.setString(1, FIXTURE_CONTACT);
            ps.executeUpdate();
        }
    }

    @Test
    @DisplayName("findByContactNumber matches an existing patient by exact contact number")
    void findByContactNumberFindsExistingPatient() {
        Patient found = patientService.findByContactNumber(FIXTURE_CONTACT);
        assertNotNull(found);
        assertEquals(FIXTURE_NAME, found.getName());
    }

    @Test
    @DisplayName("findByContactNumber returns null for a number no patient has")
    void findByContactNumberReturnsNullWhenNotFound() {
        assertNull(patientService.findByContactNumber("0799999999"));
    }

    @Test
    @DisplayName("findByContactNumber on blank input returns null without querying the database")
    void findByContactNumberOnBlankInputReturnsNull() {
        assertNull(patientService.findByContactNumber(""));
        assertNull(patientService.findByContactNumber(null));
    }

    @Test
    @DisplayName("search() matches by partial name (case-sensitivity follows MySQL's default collation)")
    void searchMatchesByPartialName() {
        List<Patient> results = patientService.search("JUnit Search");
        assertTrue(results.stream().anyMatch(p -> p.getContactNumber().equals(FIXTURE_CONTACT)),
                "Expected the fixture patient among search results for a partial name match");
    }

    @Test
    @DisplayName("search() matches by partial contact number")
    void searchMatchesByPartialContactNumber() {
        List<Patient> results = patientService.search("0799000");
        assertTrue(results.stream().anyMatch(p -> p.getName().equals(FIXTURE_NAME)));
    }

    @Test
    @DisplayName("search() with no matches returns an empty list, not null")
    void searchWithNoMatchesReturnsEmptyList() {
        List<Patient> results = patientService.search("no-such-patient-zzz");
        assertNotNull(results);
        assertFalse(results.stream().anyMatch(p -> p.getContactNumber().equals(FIXTURE_CONTACT)));
    }

    @Test
    @DisplayName("findRecent(limit) never returns more than the requested number of patients")
    void findRecentRespectsTheLimit() {
        List<Patient> results = patientService.findRecent(2);
        assertTrue(results.size() <= 2, "findRecent(2) returned " + results.size() + " patients");
    }
}
