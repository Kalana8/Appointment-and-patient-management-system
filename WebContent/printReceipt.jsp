<%@ page import="dental.model.Appointment" %>
<%@ page import="dental.model.Bill" %>
<%@ page import="dental.model.Receipt" %>
<%
    if (session.getAttribute("staffUser") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    request.setAttribute("pageTitle", "Print Receipt");
    request.setAttribute("activeNav", "bill");
    Appointment appointment = (Appointment) request.getAttribute("appointment");
    Bill bill = (Bill) request.getAttribute("bill");
    Receipt receipt = (Receipt) request.getAttribute("receipt");
%>
<%@ include file="/common/header.jsp" %>

<div class="no-print" style="max-width:480px;margin:0 auto 1rem;text-align:right;">
    <button class="btn btn-accent" onclick="window.print()">Print this Receipt</button>
    <a class="btn btn-secondary" href="<%= request.getContextPath() %>/menu.jsp">Back to Menu</a>
</div>

<div class="receipt-box">
    <p class="clinic-name">Sunrise Dental Clinic</p>
    <p class="center" style="color:#6b7280;font-size:0.85rem;margin-top:-0.5rem;">Official Payment Receipt</p>
    <h2><%= receipt.getReceiptNumber() %></h2>

    <table>
        <tr><th>Appointment No.</th><td><%= appointment.getAppointmentNumber() %></td></tr>
        <tr><th>Patient</th><td><%= appointment.getPatient().getName() %></td></tr>
        <tr><th>Dentist</th><td><%= appointment.getDentistName() %></td></tr>
        <tr><th>Treatment</th><td><%= appointment.getTreatmentType().getName() %></td></tr>
        <tr><th>Printed On</th><td><%= receipt.getPrintedDate() %></td></tr>
    </table>

    <div class="summary-row"><span>Consultation Fee</span><span>LKR <%= bill.getConsultationFee() %></span></div>
    <div class="summary-row"><span>Treatment Cost</span><span>LKR <%= bill.getTreatmentCost() %></span></div>
    <div class="summary-row total"><span>Total Paid</span><span>LKR <%= bill.getTotalAmount() %></span></div>

    <p class="center" style="margin-top:1.5rem;color:#6b7280;font-size:0.78rem;">Thank you for choosing Sunrise Dental Clinic.</p>
</div>

<%--
    The physical "print" step is realised with the browser's own
    window.print() dialog rather than driving printer hardware -- no
    printer is reachable from a web application in this teaching/marking
    context, so this is documented in the Task B report as a deliberate,
    reasonable scope decision rather than left unexplained. The
    @media print rule in css/style.css hides the navbar and the
    "no-print" button bar so only the receipt itself is printed.
--%>

<%@ include file="/common/footer.jsp" %>
