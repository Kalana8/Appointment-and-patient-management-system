package dental.dao;

import dental.model.Appointment;
import dental.model.Patient;
import dental.model.TreatmentType;
import dental.util.DatabaseConnectionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC-backed implementation of IAppointmentDao (DAO Pattern, Lecture 9).
 * appointment_number is intentionally left for the database trigger
 * trg_generate_appointment_number to populate (Assumption A4) -- this
 * class reads it back afterwards with LAST_INSERT_ID(), rather than
 * generating it in Java.
 */
public class DatabaseAppointmentDao implements IAppointmentDao {

    @Override
    public Appointment findByNumber(String appointmentNumber) {
        String sql = "SELECT a.appointment_id, a.appointment_number, a.dentist_name, a.appointment_date, " +
                     "a.appointment_time, a.status, a.registered_by, " +
                     "p.patient_id, p.name AS patient_name, p.address, p.contact_number, p.date_of_birth, " +
                     "t.treatment_type_id, t.name AS treatment_name, t.base_cost " +
                     "FROM appointment a " +
                     "JOIN patient p ON p.patient_id = a.patient_id " +
                     "JOIN treatment_type t ON t.treatment_type_id = a.treatment_type_id " +
                     "WHERE a.appointment_number = ?";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, appointmentNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error looking up appointment by number", e);
        }
        return null;
    }

    @Override
    public String save(Appointment appointment) {
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                int patientId = findOrCreatePatient(conn, appointment.getPatient());

                String sql = "INSERT INTO appointment (patient_id, dentist_name, treatment_type_id, " +
                             "appointment_date, appointment_time, registered_by) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, patientId);
                    ps.setString(2, appointment.getDentistName());
                    ps.setInt(3, appointment.getTreatmentType().getTreatmentTypeId());
                    ps.setObject(4, appointment.getAppointmentDate());
                    ps.setTime(5, Time.valueOf(appointment.getAppointmentTime()));
                    ps.setInt(6, appointment.getRegisteredBy());
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        int appointmentId = keys.getInt(1);
                        String generatedNumber;
                        try (PreparedStatement lookup = conn.prepareStatement(
                                "SELECT appointment_number FROM appointment WHERE appointment_id = ?")) {
                            lookup.setInt(1, appointmentId);
                            try (ResultSet rs = lookup.executeQuery()) {
                                rs.next();
                                generatedNumber = rs.getString(1);
                            }
                        }
                        conn.commit();
                        return generatedNumber;
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving appointment", e);
        }
    }

    @Override
    public List<Appointment> findAll() {
        return runListQuery(
                "SELECT a.appointment_id, a.appointment_number, a.dentist_name, a.appointment_date, " +
                "a.appointment_time, a.status, a.registered_by, " +
                "p.patient_id, p.name AS patient_name, p.address, p.contact_number, p.date_of_birth, " +
                "t.treatment_type_id, t.name AS treatment_name, t.base_cost " +
                "FROM appointment a " +
                "JOIN patient p ON p.patient_id = a.patient_id " +
                "JOIN treatment_type t ON t.treatment_type_id = a.treatment_type_id " +
                "ORDER BY a.appointment_date DESC, a.appointment_time DESC", null);
    }

    @Override
    public List<Appointment> findByDate(LocalDate date) {
        return runListQuery(
                "SELECT a.appointment_id, a.appointment_number, a.dentist_name, a.appointment_date, " +
                "a.appointment_time, a.status, a.registered_by, " +
                "p.patient_id, p.name AS patient_name, p.address, p.contact_number, p.date_of_birth, " +
                "t.treatment_type_id, t.name AS treatment_name, t.base_cost " +
                "FROM appointment a " +
                "JOIN patient p ON p.patient_id = a.patient_id " +
                "JOIN treatment_type t ON t.treatment_type_id = a.treatment_type_id " +
                "WHERE a.appointment_date = ? ORDER BY a.appointment_time", date);
    }

    @Override
    public List<Appointment> findByPatientId(int patientId) {
        List<Appointment> results = new ArrayList<>();
        String sql = "SELECT a.appointment_id, a.appointment_number, a.dentist_name, a.appointment_date, " +
                     "a.appointment_time, a.status, a.registered_by, " +
                     "p.patient_id, p.name AS patient_name, p.address, p.contact_number, p.date_of_birth, " +
                     "t.treatment_type_id, t.name AS treatment_name, t.base_cost " +
                     "FROM appointment a " +
                     "JOIN patient p ON p.patient_id = a.patient_id " +
                     "JOIN treatment_type t ON t.treatment_type_id = a.treatment_type_id " +
                     "WHERE a.patient_id = ? ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listing patient history", e);
        }
        return results;
    }

    @Override
    public boolean updateStatus(String appointmentNumber, String newStatus) {
        String sql = "UPDATE appointment SET status = ? WHERE appointment_number = ?";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, appointmentNumber);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating appointment status", e);
        }
    }

    private List<Appointment> runListQuery(String sql, LocalDate dateParam) {
        List<Appointment> results = new ArrayList<>();
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (dateParam != null) {
                ps.setObject(1, dateParam);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listing appointments", e);
        }
        return results;
    }

    /** Finds an existing patient by contact number, or inserts a new patient row. */
    private int findOrCreatePatient(Connection conn, Patient patient) throws SQLException {
        try (PreparedStatement find = conn.prepareStatement(
                "SELECT patient_id FROM patient WHERE contact_number = ?")) {
            find.setString(1, patient.getContactNumber());
            try (ResultSet rs = find.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        try (PreparedStatement insert = conn.prepareStatement(
                "INSERT INTO patient (name, address, contact_number, date_of_birth) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, patient.getName());
            insert.setString(2, patient.getAddress());
            insert.setString(3, patient.getContactNumber());
            insert.setObject(4, patient.getDateOfBirth());
            insert.executeUpdate();
            try (ResultSet keys = insert.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(rs.getInt("appointment_id"));
        appointment.setAppointmentNumber(rs.getString("appointment_number"));
        appointment.setDentistName(rs.getString("dentist_name"));
        appointment.setAppointmentDate(rs.getObject("appointment_date", LocalDate.class));
        appointment.setAppointmentTime(rs.getTime("appointment_time").toLocalTime());
        appointment.setStatus(rs.getString("status"));
        appointment.setRegisteredBy(rs.getInt("registered_by"));

        Patient patient = new Patient();
        patient.setPatientId(rs.getInt("patient_id"));
        patient.setName(rs.getString("patient_name"));
        patient.setAddress(rs.getString("address"));
        patient.setContactNumber(rs.getString("contact_number"));
        patient.setDateOfBirth(rs.getObject("date_of_birth", LocalDate.class));
        appointment.setPatient(patient);

        TreatmentType treatmentType = new TreatmentType();
        treatmentType.setTreatmentTypeId(rs.getInt("treatment_type_id"));
        treatmentType.setName(rs.getString("treatment_name"));
        treatmentType.setBaseCost(rs.getBigDecimal("base_cost"));
        appointment.setTreatmentType(treatmentType);

        return appointment;
    }
}
