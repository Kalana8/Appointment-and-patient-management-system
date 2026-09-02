package dental.dao;

import dental.model.Appointment;
import java.time.LocalDate;
import java.util.List;

/**
 * DAO Pattern interface (Task A / Lecture 9). Realised by
 * FileAppointmentDao and DatabaseAppointmentDao.
 */
public interface IAppointmentDao {
    Appointment findByNumber(String appointmentNumber);
    /** Returns the generated appointment_number, matching Assumption A4. */
    String save(Appointment appointment);
    List<Appointment> findAll();
    List<Appointment> findByDate(LocalDate date);
}
