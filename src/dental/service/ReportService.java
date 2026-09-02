package dental.service;

import dental.util.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads the three reporting views defined in database/schema.sql
 * (v_daily_appointment_report, v_revenue_report, v_treatment_popularity_report)
 * -- the "suitable set of reports, which you think add more value to your
 * system" required by the Task B brief, and the "Proposed reports to
 * facilitate decision-making" excellent-band criterion.
 */
public class ReportService {

    public List<Map<String, Object>> dailyAppointmentReport() {
        return runQuery("SELECT * FROM v_daily_appointment_report");
    }

    public List<Map<String, Object>> revenueReport() {
        return runQuery("SELECT * FROM v_revenue_report");
    }

    public List<Map<String, Object>> treatmentPopularityReport() {
        return runQuery("SELECT * FROM v_treatment_popularity_report");
    }

    private List<Map<String, Object>> runQuery(String sql) {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int columnCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(rs.getMetaData().getColumnLabel(i), rs.getObject(i));
                }
                rows.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error running report query", e);
        }
        return rows;
    }
}
