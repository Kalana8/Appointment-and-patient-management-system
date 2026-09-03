package dental.service;

import dental.model.TreatmentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pure unit tests (no database) for BillingService.calculateTreatmentCost(),
 * the «include»-relationship helper behind "Calculate Treatment Cost"
 * (Task A, Figure 1) and the AJAX cost-preview web service (Task B, 2.8).
 * Requirement traced: FR4 "Calculate and Print Bill" (assignment brief).
 */
public class BillingServiceTest {

    private final BillingService billingService = new BillingService();

    @Test
    @DisplayName("calculateTreatmentCost returns the treatment's own base cost, unmodified")
    void returnsBaseCostForAGivenTreatmentType() {
        TreatmentType scaling = new TreatmentType(5, "Teeth Cleaning (Scaling)", new BigDecimal("3500.00"));
        assertEquals(new BigDecimal("3500.00"), billingService.calculateTreatmentCost(scaling));
    }

    @Test
    @DisplayName("calculateTreatmentCost does not add the consultation fee itself")
    void doesNotIncludeConsultationFee() {
        // The LKR 500.00 consultation fee is added later, by
        // sp_generate_bill (Task B, 2.5) -- calculateTreatmentCost is only
        // the «include»d step that previews the treatment's own cost, so
        // this test pins that boundary down explicitly.
        TreatmentType checkup = new TreatmentType(1, "Dental Checkup", new BigDecimal("1500.00"));
        BigDecimal result = billingService.calculateTreatmentCost(checkup);
        assertEquals(new BigDecimal("1500.00"), result);
        assertEquals(0, result.compareTo(new BigDecimal("2000.00").subtract(new BigDecimal("500.00"))));
    }

    @Test
    @DisplayName("calculateTreatmentCost(null) returns zero rather than throwing")
    void nullTreatmentTypeReturnsZeroNotException() {
        assertEquals(0, BigDecimal.ZERO.compareTo(billingService.calculateTreatmentCost(null)));
    }
}
