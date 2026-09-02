package dental.service;

import dental.dao.DaoFactory;
import dental.dao.IUserDao;
import dental.model.StaffUser;

/**
 * Service Layer class (Task A, Figure 2). Realises the "Login" sequence
 * diagram: delegates the actual lookup to IUserDao (obtained through the
 * Factory Method DaoFactory), never talking to a concrete DAO directly.
 */
public class AuthenticationService {

    private final IUserDao userDao;

    public AuthenticationService() {
        this.userDao = DaoFactory.createUserDao();
    }

    /** Returns the authenticated StaffUser, or null if the credentials are invalid. */
    public StaffUser authenticate(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return null;
        }
        StaffUser user = userDao.findByUsername(username.trim());
        if (user != null && validateCredentials(user, password)) {
            return user;
        }
        return null;
    }

    private boolean validateCredentials(StaffUser user, String suppliedPassword) {
        return user.isActive() && user.getPassword().equals(suppliedPassword);
    }
}
