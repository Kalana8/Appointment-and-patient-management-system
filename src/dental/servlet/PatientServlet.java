package dental.servlet;

import dental.model.Appointment;
import dental.model.Patient;
import dental.service.AppointmentService;
import dental.service.PatientService;
import dental.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Controller for the Patient directory / client-history feature: lets
 * front-desk staff pull up a returning client's record -- by exact
 * contact number, or a name/contact search -- and see every appointment
 * they have ever had, instead of only being able to look appointments up
 * one appointment-number at a time.
 */
public class PatientServlet extends HttpServlet {

    private final PatientService patientService = new PatientService();
    private final AppointmentService appointmentService = new AppointmentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String contact = request.getParameter("contact");
        String query = request.getParameter("q");

        if (contact != null && !contact.isBlank()) {
            Patient patient = patientService.findByContactNumber(contact.trim());
            if (patient != null) {
                List<Appointment> history = appointmentService.findByPatientId(patient.getPatientId());
                request.setAttribute("patient", patient);
                request.setAttribute("history", history);
            } else {
                request.setAttribute("notFoundMessage",
                        "No patient record found for contact number \"" + contact + "\".");
            }
        } else if (query != null && !query.isBlank()) {
            request.setAttribute("results", patientService.search(query.trim()));
            request.setAttribute("searchedQuery", query.trim());
        } else {
            request.setAttribute("results", patientService.findAll());
        }

        request.getRequestDispatcher("/patients.jsp").forward(request, response);
    }
}
