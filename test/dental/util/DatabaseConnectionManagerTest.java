package dental.util;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the Singleton pattern claimed for DatabaseConnectionManager
 * (Task B, 2.4.1) actually holds at runtime, and that the pooled
 * configuration it holds can open a real connection against the test
 * database configured by run-tests.sh (-Ddental.db.name=
 * sunrise_dental_clinic_test).
 */
public class DatabaseConnectionManagerTest {

    @Test
    void getInstanceAlwaysReturnsTheSameObject() {
        DatabaseConnectionManager first = DatabaseConnectionManager.getInstance();
        DatabaseConnectionManager second = DatabaseConnectionManager.getInstance();
        assertSame(first, second, "Singleton pattern requires getInstance() to return the same object every time");
    }

    @Test
    void getConnectionOpensAWorkingJdbcConnection() throws SQLException {
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection()) {
            assertNotNull(conn);
            assertTrue(conn.isValid(2), "Connection should be valid/open immediately after creation");
        }
    }
}
