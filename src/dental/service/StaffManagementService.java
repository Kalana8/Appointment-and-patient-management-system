package dental.service;

import dental.dao.DaoFactory;
import dental.dao.IUserDao;
import dental.model.Administrator;
import dental.model.Receptionist;
import dental.model.StaffUser;

import java.util.ArrayList;
import java.util.List;

/** Service Layer class realising "Manage Staff Accounts" (Administrator-only use case). */
public class StaffManagementService {

    private final IUserDao userDao;

    public StaffManagementService() {
        this.userDao = DaoFactory.createUserDao();
    }

    public List<StaffUser> listStaff() {
        return userDao.findAll();
    }

    public List<String> createStaff(String username, String password, String fullName, String role) {
        List<String> errors = new ArrayList<>();
        if (isBlank(username)) {
            errors.add("Username is required.");
        } else if (userDao.findByUsername(username.trim()) != null) {
            errors.add("Username '" + username.trim() + "' is already taken.");
        }
        if (isBlank(password) || password.length() < 6) {
            errors.add("Password must be at least 6 characters.");
        }
        if (isBlank(fullName)) {
            errors.add("Full name is required.");
        }
        if (!"RECEPTIONIST".equals(role) && !"ADMINISTRATOR".equals(role)) {
            errors.add("Role must be RECEPTIONIST or ADMINISTRATOR.");
        }
        if (!errors.isEmpty()) {
            return errors;
        }

        StaffUser newUser = "ADMINISTRATOR".equals(role)
                ? new Administrator(0, username.trim(), password, fullName.trim(), true)
                : new Receptionist(0, username.trim(), password, fullName.trim(), true);
        userDao.save(newUser);
        return errors; // empty = success
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
