package dental.dao;

import dental.model.Administrator;
import dental.model.Receptionist;
import dental.model.StaffUser;
import dental.util.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC-backed implementation of IUserDao (DAO Pattern, Lecture 9), using
 * PreparedStatement as taught: import java.sql.*, load the driver via the
 * Singleton DatabaseConnectionManager, prepare + execute + process the
 * ResultSet, and close the connection (try-with-resources).
 */
public class DatabaseUserDao implements IUserDao {

    @Override
    public StaffUser findByUsername(String username) {
        String sql = "SELECT staff_id, username, password, full_name, role, active " +
                     "FROM staff WHERE username = ? AND active = 1";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error looking up staff by username", e);
        }
        return null;
    }

    @Override
    public void save(StaffUser user) {
        String sql = "INSERT INTO staff (username, password, full_name, role, active) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getRole());
            ps.setBoolean(5, user.isActive());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving staff account", e);
        }
    }

    @Override
    public List<StaffUser> findAll() {
        List<StaffUser> users = new ArrayList<>();
        String sql = "SELECT staff_id, username, password, full_name, role, active FROM staff ORDER BY staff_id";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listing staff accounts", e);
        }
        return users;
    }

    /** Maps one staff row to a Receptionist or Administrator (Polymorphism, Lecture 2). */
    private StaffUser mapRow(ResultSet rs) throws SQLException {
        int staffId = rs.getInt("staff_id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String fullName = rs.getString("full_name");
        String role = rs.getString("role");
        boolean active = rs.getBoolean("active");

        if ("ADMINISTRATOR".equals(role)) {
            return new Administrator(staffId, username, password, fullName, active);
        }
        return new Receptionist(staffId, username, password, fullName, active);
    }
}
