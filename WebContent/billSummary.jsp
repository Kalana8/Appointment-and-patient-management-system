<%@ page import="dental.model.Appointment" %>
<%@ page import="dental.model.Bill" %>
<%
    if (session.getAttribute("staffUser") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    request.setAttribute("pageTitle", "Bill Summary");
    request.setAttribute("activeNav", "bill");
    Appointment appointment = (Appointment) request.getAttribute("appointment");
    Bill bill = (Bill) request.getAttribute("bill");
%>
<%@ include file="/common/header.jsp" %>

<div class="card" style="max-width:520px;margin:2rem auto 0;">
    <div class="alert alert-success">Bill generated successfully for appointment <%= bill.getAppointmentNumber() %>.</div>
    <h1>Bill Summary</h1>
    <table style="margin-bottom:1rem;">
        <tr><th>Patient</th><td><%= appointment.getPatient().getName() %></td></tr>
        <tr><th>Dentist</th><td><%= appointment.getDentistName() %></td></tr>
        <tr><th>Treatment</th><td><%= appointment.getTreatmentType().getName() %></td></tr>
    </table>

    <div class="summary-row"><span>Consultation Fee</span><span>LKR <%= bill.getConsultationFee() %></span></div>
    <div class="summary-row"><span>Treatment Cost</span><span>LKR <%= bill.getTreatmentCost() %></span></div>
    <div class="summary-row total"><span>Total Amount</span><span>LKR <%= bill.getTotalAmount() %></span></div>

    <form method="post" action="<%= request.getContextPath() %>/printReceipt" style="margin-top:1.5rem;">
        <input type="hidden" name="appointmentNumber" value="<%= bill.getAppointmentNumber() %>">
        <input type="hidden" name="billId" value="<%= bill.getBillId() %>">
        <input type="hidden" name="consultationFee" value="<%= bill.getConsultationFee() %>">
        <input type="hidden" name="treatmentCost" value="<%= bill.getTreatmentCost() %>">
        <input type="hidden" name="totalAmount" value="<%= bill.getTotalAmount() %>">
        <button type="submit" class="btn btn-accent">Print Receipt</button>
        <a class="btn btn-secondary" href="<%= request.getContextPath() %>/menu.jsp">Back to Menu</a>
    </form>
</div>

<%@ include file="/common/footer.jsp" %>
