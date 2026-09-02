package dental.util;

import dental.model.StaffUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Small helper wrapping the HttpSession calls taught in Lecture 7
 * (session.setAttribute / getAttribute / invalidate) so every protected
 * servlet checks login state the same way instead of repeating the same
 * three lines. This does not introduce any new concept beyond what the
 * lecture teaches -- it is the same HttpSession API, just centralised.
 */
public final class SessionUtil {

    public static final String SESSION_ATTR_STAFF_USER = "staffUser";

    private SessionUtil() {
    }

    public static void login(HttpServletRequest request, StaffUser user) {
        HttpSession session = request.getSession();
        session.setAttribute(SESSION_ATTR_STAFF_USER, user);
    }

    public static StaffUser currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (StaffUser) session.getAttribute(SESSION_ATTR_STAFF_USER);
    }

    public static boolean isLoggedIn(HttpServletRequest request) {
        return currentUser(request) != null;
    }

    public static boolean isAdministrator(HttpServletRequest request) {
        StaffUser user = currentUser(request);
        return user != null && "ADMINISTRATOR".equals(user.getRole());
    }

    public static void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // Lecture 7: "Usually done during logout"
        }
    }
}
