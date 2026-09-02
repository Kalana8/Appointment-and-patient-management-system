package dental.model;

import java.time.LocalDateTime;

/** Domain Model class (Task A, Figure 2). Owned by exactly one Bill (composition). */
public class Receipt {

    private int receiptId;
    private int billId;
    private String receiptNumber;
    private LocalDateTime printedDate;

    public Receipt() {
    }

    public int getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(int receiptId) {
        this.receiptId = receiptId;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public LocalDateTime getPrintedDate() {
        return printedDate;
    }

    public void setPrintedDate(LocalDateTime printedDate) {
        this.printedDate = printedDate;
    }
}
