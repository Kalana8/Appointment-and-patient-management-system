package dental.model;

import java.time.LocalDate;

/** Domain Model class (Task A, Figure 2). Aggregated by Appointment (0..* -- 1). */
public class Patient {

    private int patientId;
    private String name;
    private String address;
    private String contactNumber;
    private LocalDate dateOfBirth;

    public Patient() {
    }

    public Patient(int patientId, String name, String address, String contactNumber, LocalDate dateOfBirth) {
        this.patientId = patientId;
        this.name = name;
        this.address = address;
        this.contactNumber = contactNumber;
        this.dateOfBirth = dateOfBirth;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
