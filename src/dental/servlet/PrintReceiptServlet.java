package dental.servlet;

import dental.model.Appointment;
import dental.model.Bill;
import dental.model.Receipt;
import dental.service.AppointmentService;
import dental.service.BillingService;
import dental.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Controller for "Print Receipt" (Task A, Figure 6) -- the «extend» step
 * on "Generate Bill", only ever invoked once the receptionist explicitly
 * confirms. Forwards to a print-styled JSP; the browser's own print
 * dialog (window.print()) stands in for a physical printer, since no
 * printer hardware is reachable from a web application in this teaching
 * context -- documented as a deliberate scope decision in the Task B
 * report rather than left unexplained.
 */
public class PrintReceiptServlet extends HttpServlet {

    private final AppointmentService appointmentService = new AppointmentService();
    private final BillingService billingService = new BillingService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String appointmentNumber = request.getParameter("appointmentNumber");
        int billId = Integer.parseInt(request.getParameter("billId"));
        BigDecimal consultationFee = new BigDecimal(request.getParameter("consultationFee"));
        BigDecimal treatmentCost = new BigDecimal(request.getParameter("treatmentCost"));
        BigDecimal totalAmount = new BigDecimal(request.getParameter("totalAmount"));

        Appointment appointment = appointmentService.searchAppointment(appointmentNumber);

        Bill bill = new Bill();
        bill.setBillId(billId);
        bill.setAppointmentNumber(appointmentNumber);
        bill.setConsultationFee(consultationFee);
        bill.setTreatmentCost(treatmentCost);
        bill.setTotalAmount(totalAmount);

        Receipt receipt = billingService.printReceipt(bill);

        request.setAttribute("appointment", appointment);
        request.setAttribute("bill", bill);
        request.setAttribute("receipt", receipt);
        request.getRequestDispatcher("/printReceipt.jsp").forward(request, response);
    }
}
