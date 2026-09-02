package dental.servlet;

import dental.model.Appointment;
import dental.service.AppointmentService;
import dental.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Controller for "View Appointment Details" / "Search Appointment by Number" (Task A, Figures 1 and 5). */
public class ViewAppointmentServlet extends HttpServlet {

    private final AppointmentService appointmentService = new AppointmentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String appointmentNumber = request.getParameter("appointmentNumber");
        if (appointmentNumber != null && !appointmentNumber.isBlank()) {
            Appointment appointment = appointmentService.searchAppointment(appointmentNumber);
            if (appointment != null) {
                request.setAttribute("appointment", appointment);
            } else {
                request.setAttribute("notFoundMessage", "No appointment found for number \"" + appointmentNumber + "\".");
            }
            request.setAttribute("searchedNumber", appointmentNumber);
        }
        request.getRequestDispatcher("/viewAppointment.jsp").forward(request, response);
    }
}
