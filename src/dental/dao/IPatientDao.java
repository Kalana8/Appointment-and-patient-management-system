package dental.dao;

import dental.model.Patient;

import java.util.List;

/**
 * DAO Pattern interface (Task A / Lecture 9) for the Patient directory /
 * client-history feature. contact_number is treated as the patient's
 * natural/business key throughout the application (DatabaseAppointmentDao
 * already looks patients up by contact_number before creating a new row,
 * and the database now enforces this with a UNIQUE constraint -- see
 * database/schema.sql and database/migration_patient_uniqueness.sql),
 * even though patient_id remains the technical primary key used for
 * foreign keys, which is the more robust, standard design.
 */
public interface IPatientDao {
    Patient findByContactNumber(String contactNumber);
    /** Matches on name OR contact number (partial, case-insensitive). */
    List<Patient> search(String query);
    List<Patient> findAll();
    /**
     * The most recently registered patients, newest first. patient_id is
     * AUTO_INCREMENT so a higher id is a later registration -- there is no
     * separate "registered on" timestamp column on patient, so this is the
     * natural ordering rather than an extra column just for the dashboard.
     */
    List<Patient> findRecent(int limit);
}
