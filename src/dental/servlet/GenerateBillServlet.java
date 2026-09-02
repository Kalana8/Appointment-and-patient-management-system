package dental.servlet;

import dental.model.Appointment;
import dental.model.Bill;
import dental.service.AppointmentService;
import dental.service.BillingService;
import dental.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Controller for "Generate Bill" (Task A, Figures 1 and 6), «includes» Calculate Treatment Cost. */
public class GenerateBillServlet extends HttpServlet {

    private final AppointmentService appointmentService = new AppointmentService();
    private final BillingService billingService = new BillingService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        request.getRequestDispatcher("/generateBill.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String appointmentNumber = request.getParameter("appointmentNumber");
        Appointment appointment = appointmentService.searchAppointment(appointmentNumber);

        if (appointment == null) {
            request.setAttribute("errorMessage", "No appointment found for number \"" + appointmentNumber + "\".");
            request.getRequestDispatcher("/generateBill.jsp").forward(request, response);
            return;
        }

        Bill bill = billingService.generateBill(appointment);
        request.setAttribute("appointment", appointment);
        request.setAttribute("bill", bill);
        request.getRequestDispatcher("/billSummary.jsp").forward(request, response);
    }
}
