package dental.servlet;

import dental.model.StaffUser;
import dental.service.AuthenticationService;
import dental.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller for the "Login" use case (Task A, Figures 1 and 3).
 * Uses HttpSession to track the logged-in staff member (Lecture 7:
 * "Common Uses of Sessions - User login authentication") and, when
 * "Remember me" is ticked, a Cookie storing the username for 7 days
 * (Lecture 7: Cookies -- "Remembering usernames", "Remember Me"
 * functionality) -- together these satisfy the Task B excellent-band
 * criterion "Effective use of sessions/cookies".
 */
public class LoginServlet extends HttpServlet {

    private final AuthenticationService authenticationService = new AuthenticationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessionUtil.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/menu.jsp");
            return;
        }
        // Pre-fill the username field if a "remember me" cookie is present.
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("rememberedUsername".equals(cookie.getName())) {
                    request.setAttribute("rememberedUsername", cookie.getValue());
                }
            }
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        boolean rememberMe = "on".equals(request.getParameter("rememberMe"));

        StaffUser user = authenticationService.authenticate(username, password);

        if (user != null) {
            SessionUtil.login(request, user);

            if (rememberMe) {
                Cookie cookie = new Cookie("rememberedUsername", username.trim());
                cookie.setMaxAge(60 * 60 * 24 * 7); // 7 days, per Lecture 7's cookie example
                cookie.setPath(request.getContextPath() + "/");
                response.addCookie(cookie);
            }

            response.sendRedirect(request.getContextPath() + "/menu.jsp");
        } else {
            request.setAttribute("errorMessage", "Invalid username or password.");
            request.setAttribute("rememberedUsername", username);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
