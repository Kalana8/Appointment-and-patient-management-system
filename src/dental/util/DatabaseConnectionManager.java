package dental.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton Pattern (Task A / Lecture 4: Singleton Pattern). Ensures the
 * whole web application shares one JDBC configuration object with a single,
 * global access point (getInstance()), exactly matching the "Key Features
 * of the Singleton pattern" slide: Single Instance, Global Access Point,
 * Lazy Initialization.
 *
 * A private constructor prevents any other class from calling `new
 * DatabaseConnectionManager()`, and getInstance() creates the instance only
 * the first time it is needed (lazy initialization), then reuses it.
 *
 * Note on scope: because HTTP requests in this application are short-lived
 * and each DAO method opens/closes its own Connection in a try-with-resources
 * block (see FileUserDao/DatabaseUserDao... wait: DatabaseUserDao etc.),
 * the Singleton here holds the immutable JDBC URL/driver configuration
 * rather than one shared live Connection object -- sharing a single mutable
 * Connection across concurrent servlet requests would not be thread-safe.
 * This keeps the Singleton pattern's structural intent (one shared,
 * globally-accessible configuration/factory point) while remaining correct
 * under concurrent web requests, and is exactly the same trade-off a real
 * connection *pool* singleton (e.g. HikariCP) makes internally.
 */
public final class DatabaseConnectionManager {

    private static DatabaseConnectionManager instance;

    private final String jdbcUrl;
    private final String username;
    private final String password;

    // MariaDB Connector/J (org.mariadb.jdbc.Driver) is used as the JDBC
    // driver. It speaks the same MySQL wire protocol taught in Lecture 9
    // and is a drop-in, MySQL-compatible replacement for mysql-connector-j
    // in any environment where the official MySQL Connector/J jar cannot be
    // downloaded; swapping the driver class name and the jar in WEB-INF/lib
    // is the only change needed to use mysql-connector-j instead.
    private static final String DRIVER_CLASS_NAME = "org.mariadb.jdbc.Driver";

    private DatabaseConnectionManager() {
        // allowPublicKeyRetrieval=true is required because MySQL 8's default
        // authentication plugin (caching_sha2_password) needs to exchange an
        // RSA public key with the client; MariaDB Connector/J does not
        // request it automatically, which otherwise fails every connection
        // with "RSA public key is not available client side".
        this.jdbcUrl = "jdbc:mysql://localhost:3306/sunrise_dental_clinic?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        this.username = "dental_app";
        this.password = "DentalApp#2026";
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("JDBC driver not found on classpath: " + DRIVER_CLASS_NAME, e);
        }
    }

    /** Global access point (Singleton pattern) -- lazily creates the single instance. */
    public static synchronized DatabaseConnectionManager getInstance() {
        if (instance == null) {
            instance = new DatabaseConnectionManager();
        }
        return instance;
    }

    /** Opens a new JDBC connection using the shared configuration held by this Singleton. */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }
}
