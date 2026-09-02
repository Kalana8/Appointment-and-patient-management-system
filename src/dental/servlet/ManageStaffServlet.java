package dental.servlet;

import dental.service.StaffManagementService;
import dental.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/** Controller for "Manage Staff Accounts" (Task A, Figure 1) -- Administrator only. */
public class ManageStaffServlet extends HttpServlet {

    private final StaffManagementService staffManagementService = new StaffManagementService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.isAdministrator(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only administrators can manage staff accounts.");
            return;
        }
        request.setAttribute("staffList", staffManagementService.listStaff());
        request.getRequestDispatcher("/manageStaff.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.isAdministrator(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only administrators can manage staff accounts.");
            return;
        }

        List<String> errors = staffManagementService.createStaff(
                request.getParameter("username"),
                request.getParameter("password"),
                request.getParameter("fullName"),
                request.getParameter("role"));

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
        } else {
            request.setAttribute("successMessage", "Staff account created successfully.");
        }
        request.setAttribute("staffList", staffManagementService.listStaff());
        request.getRequestDispatcher("/manageStaff.jsp").forward(request, response);
    }
}
