package dental.dao;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the Factory Method pattern claimed for DaoFactory (Task B,
 * 2.4.2): every creator method must return the INTERFACE type, while a
 * database-backed concrete class is what actually gets instantiated by
 * default (Task A, Assumption A6 / A7).
 */
public class DaoFactoryTest {

    @Test
    void createUserDaoReturnsADatabaseBackedImplementationByDefault() {
        IUserDao dao = DaoFactory.createUserDao();
        assertNotNull(dao);
        assertInstanceOf(DatabaseUserDao.class, dao);
    }

    @Test
    void createUserDaoWithFileModeReturnsTheFileImplementation() {
        IUserDao dao = DaoFactory.createUserDao(DaoFactory.MODE_FILE);
        assertInstanceOf(FileUserDao.class, dao);
    }

    @Test
    void createAppointmentDaoReturnsADatabaseBackedImplementationByDefault() {
        IAppointmentDao dao = DaoFactory.createAppointmentDao();
        assertInstanceOf(DatabaseAppointmentDao.class, dao);
    }

    @Test
    void createPatientDaoIsDatabaseOnly() {
        // Unlike the two factory methods above, createPatientDao() does not
        // branch on a mode argument at all -- the Patient directory feature
        // was added once Task B's database was already the system's sole
        // datastore (Task A, 1.5 / this revision). This test pins that
        // documented design decision down rather than leaving it as prose
        // only.
        IPatientDao dao = DaoFactory.createPatientDao();
        assertInstanceOf(DatabasePatientDao.class, dao);
    }

    @Test
    void repeatedCallsReturnFreshInstancesNotASharedOne() {
        // DaoFactory is a Factory Method, not a Singleton: each call must
        // return a new object, distinguishing this pattern from
        // DatabaseConnectionManager's Singleton (Task B, 2.4.1).
        IUserDao first = DaoFactory.createUserDao();
        IUserDao second = DaoFactory.createUserDao();
        assertTrue(first != second, "DaoFactory should not behave like a Singleton");
    }
}
