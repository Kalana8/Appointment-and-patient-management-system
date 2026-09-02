package dental.service;

import dental.dao.DaoFactory;
import dental.dao.IPatientDao;
import dental.model.Patient;

import java.util.List;

/**
 * Service Layer class backing the Patient directory / client-history
 * feature: lets front-desk staff look a returning client up once (by name
 * or contact number) instead of re-entering their details on every visit,
 * and see every appointment that client has ever had.
 */
public class PatientService {

    private final IPatientDao patientDao = DaoFactory.createPatientDao();

    public Patient findByContactNumber(String contactNumber) {
        if (isBlank(contactNumber)) {
            return null;
        }
        return patientDao.findByContactNumber(contactNumber.trim());
    }

    public List<Patient> search(String query) {
        return patientDao.search(query.trim());
    }

    public List<Patient> findAll() {
        return patientDao.findAll();
    }

    /** Most recently registered patients, for the dashboard's "Recent Patients" panel. */
    public List<Patient> findRecent(int limit) {
        return patientDao.findRecent(limit);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
