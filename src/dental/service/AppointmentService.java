package dental.service;

import dental.dao.DaoFactory;
import dental.dao.IAppointmentDao;
import dental.model.Appointment;
import dental.model.Patient;
import dental.model.TreatmentType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service Layer class (Task A, Figure 2). Realises the "Register New
 * Appointment" and "View Appointment Details" sequence diagrams.
 * validateAppointmentDetails() is the concrete implementation of the
 * «include» relationship from the Use Case Diagram (Figure 1).
 */
public class AppointmentService {

    // Sri Lankan mobile numbers: 07XXXXXXXX (10 digits, starts with 07)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^07\\d{8}$");

    private final IAppointmentDao appointmentDao;

    public AppointmentService() {
        this.appointmentDao = DaoFactory.createAppointmentDao();
    }

    /**
     * Registers a new appointment after validating it (mandatory «include»
     * step). Returns the system-generated appointment number.
     */
    public String registerAppointment(Patient patient, String dentistName, TreatmentType treatmentType,
                                       LocalDate appointmentDate, LocalTime appointmentTime,
                                       int registeredByStaffId) throws ValidationException {
        List<String> errors = validateAppointmentDetails(patient, dentistName, treatmentType,
                appointmentDate, appointmentTime);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDentistName(dentistName.trim());
        appointment.setTreatmentType(treatmentType);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setRegisteredBy(registeredByStaffId);

        // appointmentNumber itself is generated inside the DAO/database
        // trigger (Assumption A4), never here in the service.
        return appointmentDao.save(appointment);
    }

    /**
     * Validates appointment input («include» relationship on the Use Case
     * Diagram). Returns the list of human-readable error messages; an
     * empty list means the input is valid.
     */
    public List<String> validateAppointmentDetails(Patient patient, String dentistName, TreatmentType treatmentType,
                                                     LocalDate appointmentDate, LocalTime appointmentTime) {
        List<String> errors = new ArrayList<>();

        if (patient == null || isBlank(patient.getName())) {
            errors.add("Patient name is required.");
        }
        if (patient == null || isBlank(patient.getAddress())) {
            errors.add("Patient address is required.");
        }
        if (patient == null || isBlank(patient.getContactNumber())) {
            errors.add("Contact number is required.");
        } else if (!PHONE_PATTERN.matcher(patient.getContactNumber().trim()).matches()) {
            errors.add("Contact number must be a valid Sri Lankan mobile number, e.g. 0771234567.");
        }
        if (isBlank(dentistName)) {
            errors.add("Dentist name is required.");
        }
        if (treatmentType == null) {
            errors.add("Treatment type must be selected.");
        }
        if (appointmentDate == null) {
            errors.add("Appointment date is required.");
        } else if (appointmentDate.isBefore(LocalDate.now())) {
            errors.add("Appointment date cannot be in the past.");
        }
        if (appointmentTime == null) {
            errors.add("Appointment time is required.");
        }

        return errors;
    }

    /** Realises "Search Appointment by Number", «included» by View Appointment Details. */
    public Appointment searchAppointment(String appointmentNumber) {
        if (isBlank(appointmentNumber)) {
            return null;
        }
        return appointmentDao.findByNumber(appointmentNumber.trim());
    }

    public List<Appointment> findByDate(LocalDate date) {
        return appointmentDao.findByDate(date);
    }

    public List<Appointment> findAll() {
        return appointmentDao.findAll();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
