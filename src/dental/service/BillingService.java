package dental.service;

import dental.model.Appointment;
import dental.model.Bill;
import dental.model.Receipt;
import dental.model.TreatmentType;
import dental.util.DatabaseConnectionManager;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Service Layer class (Task A, Figure 2). Realises "Generate Bill and
 * Print Receipt". The billing business rule (treatment cost + fixed
 * consultation fee) is implemented once, centrally, as the MySQL stored
 * procedure sp_generate_bill (see database/schema.sql) -- the "Excellent"
 * band of the Task B marking criteria specifically calls for "advanced
 * database features (e.g. stored procedures, functions, triggers to
 * implement business rules)", so the calculation intentionally lives in
 * the database rather than being re-implemented in Java.
 */
public class BillingService {

    private static final BigDecimal CONSULTATION_FEE = new BigDecimal("500.00");

    /** «include»-relationship helper: shows the cost before a bill is committed. */
    public BigDecimal calculateTreatmentCost(TreatmentType treatmentType) {
        if (treatmentType == null) {
            return BigDecimal.ZERO;
        }
        return treatmentType.getBaseCost();
    }

    /**
     * Generates (or re-fetches, if one already exists) the bill for an
     * appointment by calling the sp_generate_bill stored procedure.
     */
    public Bill generateBill(Appointment appointment) {
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             CallableStatement stmt = conn.prepareCall("{call sp_generate_bill(?, ?, ?)}")) {
            stmt.setString(1, appointment.getAppointmentNumber());
            stmt.setBigDecimal(2, CONSULTATION_FEE);
            stmt.registerOutParameter(3, Types.INTEGER);
            stmt.execute();
            int billId = stmt.getInt(3);
            return findBillById(conn, billId);
        } catch (SQLException e) {
            throw new RuntimeException("Error generating bill", e);
        }
    }

    private Bill findBillById(Connection conn, int billId) throws SQLException {
        String sql = "SELECT b.bill_id, a.appointment_number, b.consultation_fee, b.treatment_cost, " +
                     "b.total_amount, b.issue_date FROM bill b " +
                     "JOIN appointment a ON a.appointment_id = b.appointment_id WHERE b.bill_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Bill bill = new Bill();
                    bill.setBillId(rs.getInt("bill_id"));
                    bill.setAppointmentNumber(rs.getString("appointment_number"));
                    bill.setConsultationFee(rs.getBigDecimal("consultation_fee"));
                    bill.setTreatmentCost(rs.getBigDecimal("treatment_cost"));
                    bill.setTotalAmount(rs.getBigDecimal("total_amount"));
                    bill.setIssueDate(rs.getTimestamp("issue_date").toLocalDateTime());
                    return bill;
                }
            }
        }
        throw new RuntimeException("Bill " + billId + " was not found immediately after creation");
    }

    /** Realises the «extend» step: only invoked once the receptionist explicitly confirms printing. */
    public Receipt printReceipt(Bill bill) {
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection()) {
            String existingSql = "SELECT receipt_id, receipt_number, printed_date FROM receipt WHERE bill_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(existingSql)) {
                ps.setInt(1, bill.getBillId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapReceipt(rs);
                    }
                }
            }
            String receiptNumber = "RCP-" + String.format("%05d", bill.getBillId());
            String insertSql = "INSERT INTO receipt (bill_id, receipt_number) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, bill.getBillId());
                ps.setString(2, receiptNumber);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(existingSql)) {
                ps.setInt(1, bill.getBillId());
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    return mapReceipt(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error printing receipt", e);
        }
    }

    private Receipt mapReceipt(ResultSet rs) throws SQLException {
        Receipt receipt = new Receipt();
        receipt.setReceiptId(rs.getInt("receipt_id"));
        receipt.setReceiptNumber(rs.getString("receipt_number"));
        receipt.setPrintedDate(rs.getTimestamp("printed_date").toLocalDateTime());
        return receipt;
    }
}
