package dental.model;

import java.math.BigDecimal;

/** Lookup domain class backing the treatment_type table (Calculate Treatment Cost use case). */
public class TreatmentType {

    private int treatmentTypeId;
    private String name;
    private BigDecimal baseCost;

    public TreatmentType() {
    }

    public TreatmentType(int treatmentTypeId, String name, BigDecimal baseCost) {
        this.treatmentTypeId = treatmentTypeId;
        this.name = name;
        this.baseCost = baseCost;
    }

    public int getTreatmentTypeId() {
        return treatmentTypeId;
    }

    public void setTreatmentTypeId(int treatmentTypeId) {
        this.treatmentTypeId = treatmentTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(BigDecimal baseCost) {
        this.baseCost = baseCost;
    }
}
