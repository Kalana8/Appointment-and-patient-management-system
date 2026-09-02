package dental.servlet;

import dental.service.ReportService;
import dental.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller for the reports feature ("Come up with a suitable set of
 * reports, which you think add more value to your system", Task B brief).
 */
public class ReportServlet extends HttpServlet {

    private final ReportService reportService = new ReportService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        request.setAttribute("dailyReport", reportService.dailyAppointmentReport());
        request.setAttribute("revenueReport", reportService.revenueReport());
        request.setAttribute("treatmentReport", reportService.treatmentPopularityReport());
        request.getRequestDispatcher("/reports.jsp").forward(request, response);
    }
}
