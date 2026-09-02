package dental.dao;

import dental.model.Patient;
import dental.util.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC-backed implementation of IPatientDao (DAO Pattern, Lecture 9).
 * Backs the Patient directory / client-history feature: every returning
 * client is looked up (and, at registration time, deduplicated) by
 * contact_number rather than by re-typing their details each visit.
 */
public class DatabasePatientDao implements IPatientDao {

    private static final String SELECT_COLUMNS =
            "SELECT patient_id, name, address, contact_number, date_of_birth FROM patient ";

    @Override
    public Patient findByContactNumber(String contactNumber) {
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_COLUMNS + "WHERE contact_number = ?")) {
            ps.setString(1, contactNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error looking up patient by contact number", e);
        }
        return null;
    }

    @Override
    public List<Patient> search(String query) {
        List<Patient> results = new ArrayList<>();
        String sql = SELECT_COLUMNS + "WHERE name LIKE ? OR contact_number LIKE ? ORDER BY name";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + query.trim() + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching patients", e);
        }
        return results;
    }

    @Override
    public List<Patient> findAll() {
        List<Patient> results = new ArrayList<>();
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_COLUMNS + "ORDER BY name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listing patients", e);
        }
        return results;
    }

    @Override
    public List<Patient> findRecent(int limit) {
        List<Patient> results = new ArrayList<>();
        String sql = SELECT_COLUMNS + "ORDER BY patient_id DESC LIMIT ?";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listing recent patients", e);
        }
        return results;
    }

    private Patient mapRow(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setPatientId(rs.getInt("patient_id"));
        patient.setName(rs.getString("name"));
        patient.setAddress(rs.getString("address"));
        patient.setContactNumber(rs.getString("contact_number"));
        patient.setDateOfBirth(rs.getObject("date_of_birth", LocalDate.class));
        return patient;
    }
}
