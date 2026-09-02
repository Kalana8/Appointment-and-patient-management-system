package dental.servlet;

import dental.model.Patient;
import dental.model.TreatmentType;
import dental.service.AppointmentService;
import dental.service.PatientService;
import dental.service.TreatmentTypeService;
import dental.service.ValidationException;
import dental.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/** Controller for "Register New Appointment" (Task A, Figures 1 and 4). */
public class RegisterAppointmentServlet extends HttpServlet {

    private final AppointmentService appointmentService = new AppointmentService();
    private final TreatmentTypeService treatmentTypeService = new TreatmentTypeService();
    private final PatientService patientService = new PatientService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        request.setAttribute("treatmentTypes", treatmentTypeService.findAll());

        // Returning-patient shortcut: coming from the Patients page with
        // ?contactNumber=... pre-fills the patient fields from their
        // existing record (looked up by contact number, the same key
        // AppointmentService/DatabaseAppointmentDao already dedupes new
        // registrations against) so the receptionist doesn't have to
        // re-type a client's details on every visit.
        String prefillContact = request.getParameter("contactNumber");
        if (prefillContact != null && !prefillContact.isBlank()) {
            Patient existing = patientService.findByContactNumber(prefillContact.trim());
            if (existing != null) {
                request.setAttribute("patientName", existing.getName());
                request.setAttribute("address", existing.getAddress());
                request.setAttribute("contactNumber", existing.getContactNumber());
                request.setAttribute("returningPatient", Boolean.TRUE);
            }
        }

        request.getRequestDispatcher("/registerAppointment.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Patient patient = new Patient();
        patient.setName(request.getParameter("patientName"));
        patient.setAddress(request.getParameter("address"));
        patient.setContactNumber(request.getParameter("contactNumber"));

        String dentistName = request.getParameter("dentistName");

        TreatmentType treatmentType = null;
        LocalDate appointmentDate = null;
        LocalTime appointmentTime = null;
        try {
            int treatmentTypeId = Integer.parseInt(request.getParameter("treatmentTypeId"));
            treatmentType = treatmentTypeService.findById(treatmentTypeId);
        } catch (NumberFormatException ignored) {
            // left null; AppointmentService.validateAppointmentDetails() will report the error
        }
        try {
            appointmentDate = LocalDate.parse(request.getParameter("appointmentDate"));
        } catch (DateTimeParseException | NullPointerException ignored) {
        }
        try {
            appointmentTime = LocalTime.parse(request.getParameter("appointmentTime"));
        } catch (DateTimeParseException | NullPointerException ignored) {
        }

        try {
            String appointmentNumber = appointmentService.registerAppointment(
                    patient, dentistName, treatmentType, appointmentDate, appointmentTime,
                    SessionUtil.currentUser(request).getStaffId());

            request.setAttribute("appointmentNumber", appointmentNumber);
            request.getRequestDispatcher("/registrationConfirmation.jsp").forward(request, response);
        } catch (ValidationException e) {
            request.setAttribute("errors", e.getErrors());
            request.setAttribute("treatmentTypes", treatmentTypeService.findAll());
            request.setAttribute("patientName", patient.getName());
            request.setAttribute("address", patient.getAddress());
            request.setAttribute("contactNumber", patient.getContactNumber());
            request.setAttribute("dentistName", dentistName);
            request.getRequestDispatcher("/registerAppointment.jsp").forward(request, response);
        }
    }
}
