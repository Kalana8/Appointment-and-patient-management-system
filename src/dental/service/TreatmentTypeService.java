package dental.service;

import dental.model.TreatmentType;
import dental.util.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Small lookup service backing the treatment_type table -- used to
 * populate the treatment dropdown on the appointment form and by
 * TreatmentCostServlet (the AJAX "web service" endpoint, see 2.6 in the
 * Task B report) to answer cost-lookup requests from the browser.
 */
public class TreatmentTypeService {

    public List<TreatmentType> findAll() {
        List<TreatmentType> types = new ArrayList<>();
        String sql = "SELECT treatment_type_id, name, base_cost FROM treatment_type ORDER BY name";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                types.add(new TreatmentType(rs.getInt("treatment_type_id"), rs.getString("name"), rs.getBigDecimal("base_cost")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listing treatment types", e);
        }
        return types;
    }

    public TreatmentType findById(int treatmentTypeId) {
        String sql = "SELECT treatment_type_id, name, base_cost FROM treatment_type WHERE treatment_type_id = ?";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, treatmentTypeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TreatmentType(rs.getInt("treatment_type_id"), rs.getString("name"), rs.getBigDecimal("base_cost"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error looking up treatment type", e);
        }
        return null;
    }
}
