package dental.dao;

/**
 * Factory Method Pattern (Task A / Lecture 4: Factory Method Pattern).
 * Each creator method returns a product INTERFACE type (IUserDao /
 * IAppointmentDao) while deciding, internally, which concrete product
 * class to instantiate based on the "mode" parameter -- exactly the
 * "Creator interface (Factory interface) / Concrete creators" structure
 * from Lecture 4's own Factory Method implementation steps.
 *
 * Callers (the Service layer) never call `new DatabaseAppointmentDao()`
 * directly; they always go through DaoFactory, so switching every DAO in
 * the application from file storage to the database (or back) is a
 * one-line change in web.xml / a config value, not a code change in any
 * class that depends on the interfaces.
 */
public final class DaoFactory {

    public static final String MODE_DATABASE = "database";
    public static final String MODE_FILE = "file";

    private static final String DEFAULT_MODE = MODE_DATABASE;
    // Relative to the working directory Tomcat is started from (e.g. the
    // Eclipse "Servers" launch configuration's base directory), so a fresh
    // "data" folder is created next to wherever the server process runs.
    private static final String USER_DATA_FILE = "data/staff.txt";
    private static final String APPOINTMENT_DATA_FILE = "data/appointments.txt";

    private DaoFactory() {
        // static factory: no instances
    }

    public static IUserDao createUserDao() {
        return createUserDao(DEFAULT_MODE);
    }

    public static IUserDao createUserDao(String mode) {
        if (MODE_FILE.equalsIgnoreCase(mode)) {
            return new FileUserDao(USER_DATA_FILE);
        }
        return new DatabaseUserDao();
    }

    public static IAppointmentDao createAppointmentDao() {
        return createAppointmentDao(DEFAULT_MODE);
    }

    public static IAppointmentDao createAppointmentDao(String mode) {
        if (MODE_FILE.equalsIgnoreCase(mode)) {
            return new FileAppointmentDao(APPOINTMENT_DATA_FILE);
        }
        return new DatabaseAppointmentDao();
    }
}
