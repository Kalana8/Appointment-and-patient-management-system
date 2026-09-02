<%@ page import="dental.model.Appointment" %>
<%
    if (session.getAttribute("staffUser") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    request.setAttribute("pageTitle", "View Appointment");
    request.setAttribute("activeNav", "view");

    Appointment appointment = (Appointment) request.getAttribute("appointment");
    String notFoundMessage = (String) request.getAttribute("notFoundMessage");
    String searchedNumber = (String) request.getAttribute("searchedNumber");
%>
<%@ include file="/common/header.jsp" %>

<div class="card">
    <h1>View Appointment Details</h1>
    <p class="subtitle">Search Appointment by Number (Task A, Figure 5).</p>

    <form method="get" action="<%= request.getContextPath() %>/viewAppointment" style="display:flex;gap:0.6rem;align-items:flex-end;flex-wrap:wrap;">
        <div class="field" style="flex:1;min-width:220px;margin-bottom:0;">
            <label for="appointmentNumber">Appointment Number</label>
            <input type="text" id="appointmentNumber" name="appointmentNumber" placeholder="e.g. APT-00001"
                   value="<%= searchedNumber != null ? searchedNumber : "" %>">
        </div>
        <button type="submit" class="btn">Search</button>
    </form>
</div>

<% if (notFoundMessage != null) { %>
<div class="alert alert-error"><%= notFoundMessage %></div>
<% } %>

<% if (appointment != null) { %>
<div class="card">
    <h2>Appointment <%= appointment.getAppointmentNumber() %>
        <span class="badge badge-<%= appointment.getStatus().toLowerCase() %>"><%= appointment.getStatus() %></span>
    </h2>
    <table>
        <tr><th>Patient Name</th><td><%= appointment.getPatient().getName() %></td></tr>
        <tr><th>Address</th><td><%= appointment.getPatient().getAddress() %></td></tr>
        <tr><th>Contact Number</th><td><%= appointment.getPatient().getContactNumber() %></td></tr>
        <tr><th>Dentist</th><td><%= appointment.getDentistName() %></td></tr>
        <tr><th>Treatment Type</th><td><%= appointment.getTreatmentType().getName() %></td></tr>
        <tr><th>Base Treatment Cost</th><td><%= appointment.getTreatmentType().getBaseCost() %></td></tr>
        <tr><th>Appointment Date</th><td><%= appointment.getAppointmentDate() %></td></tr>
        <tr><th>Appointment Time</th><td><%= appointment.getAppointmentTime() %></td></tr>
    </table>

    <form method="post" action="<%= request.getContextPath() %>/generateBill" style="margin-top:1.25rem;">
        <input type="hidden" name="appointmentNumber" value="<%= appointment.getAppointmentNumber() %>">
        <button type="submit" class="btn btn-accent">Generate Bill for this Appointment</button>
    </form>
</div>
<% } %>

<%@ include file="/common/footer.jsp" %>
