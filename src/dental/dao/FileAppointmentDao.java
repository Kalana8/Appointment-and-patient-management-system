package dental.dao;

import dental.model.Appointment;
import dental.model.Patient;
import dental.model.TreatmentType;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Text-file-backed implementation of IAppointmentDao (DAO Pattern). See
 * FileUserDao for the rationale. One pipe-delimited line per appointment:
 * appointmentNumber|patientName|address|contact|dentistName|treatmentName|baseCost|date|time|status
 */
public class FileAppointmentDao implements IAppointmentDao {

    private final String filePath;
    private final AtomicInteger sequence = new AtomicInteger(0);

    public FileAppointmentDao(String filePath) {
        this.filePath = filePath;
        ensureFileExists();
        sequence.set(findAll().size());
    }

    private void ensureFileExists() {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Could not create appointment data file: " + filePath, e);
            }
        }
    }

    @Override
    public Appointment findByNumber(String appointmentNumber) {
        for (Appointment appointment : findAll()) {
            if (appointment.getAppointmentNumber().equals(appointmentNumber)) {
                return appointment;
            }
        }
        return null;
    }

    @Override
    public String save(Appointment appointment) {
        // Assumption A4: the appointment number is generated here, not supplied by the caller.
        String generatedNumber = String.format("APT-F%05d", sequence.incrementAndGet());
        appointment.setAppointmentNumber(generatedNumber);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(String.join("|",
                    generatedNumber,
                    appointment.getPatient().getName(),
                    appointment.getPatient().getAddress(),
                    appointment.getPatient().getContactNumber(),
                    appointment.getDentistName(),
                    appointment.getTreatmentType().getName(),
                    appointment.getTreatmentType().getBaseCost().toString(),
                    appointment.getAppointmentDate().toString(),
                    appointment.getAppointmentTime().toString(),
                    "SCHEDULED"));
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error writing appointment record to file", e);
        }
        return generatedNumber;
    }

    @Override
    public List<Appointment> findAll() {
        List<Appointment> appointments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] p = line.split("\\|");
                Appointment appointment = new Appointment();
                appointment.setAppointmentNumber(p[0]);

                Patient patient = new Patient();
                patient.setName(p[1]);
                patient.setAddress(p[2]);
                patient.setContactNumber(p[3]);
                appointment.setPatient(patient);

                appointment.setDentistName(p[4]);

                TreatmentType treatmentType = new TreatmentType();
                treatmentType.setName(p[5]);
                treatmentType.setBaseCost(new BigDecimal(p[6]));
                appointment.setTreatmentType(treatmentType);

                appointment.setAppointmentDate(LocalDate.parse(p[7]));
                appointment.setAppointmentTime(LocalTime.parse(p[8]));
                appointment.setStatus(p[9]);
                appointments.add(appointment);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading appointment data file", e);
        }
        return appointments;
    }

    @Override
    public List<Appointment> findByDate(LocalDate date) {
        List<Appointment> results = new ArrayList<>();
        for (Appointment appointment : findAll()) {
            if (appointment.getAppointmentDate().equals(date)) {
                results.add(appointment);
            }
        }
        return results;
    }

    @Override
    public List<Appointment> findByPatientId(int patientId) {
        // The flat file format does not persist a patient_id (only the
        // denormalised name/address/contact per line), so patient identity
        // can't be matched here the way the database mode does. File mode
        // is kept purely as an alternative DAO-pattern implementation for
        // the "Register"/"Search by number" use cases; the Patient
        // directory / client-history feature requires database mode.
        return new ArrayList<>();
    }

    @Override
    public boolean updateStatus(String appointmentNumber, String newStatus) {
        List<String> lines = new ArrayList<>();
        boolean updated = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] p = line.split("\\|");
                if (p[0].equals(appointmentNumber)) {
                    p[9] = newStatus;
                    line = String.join("|", p);
                    updated = true;
                }
                lines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading appointment data file", e);
        }
        if (updated) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            } catch (IOException e) {
                throw new RuntimeException("Error updating appointment status in file", e);
            }
        }
        return updated;
    }
}
