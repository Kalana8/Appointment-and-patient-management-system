package dental.servlet;

import dental.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Controller for the "Logout" use case (Task A, Figure 1). */
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionUtil.logout(request);
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}
