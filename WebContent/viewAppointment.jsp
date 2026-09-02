<%@ page import="dental.model.Appointment" %>
<%@ page import="java.util.List" %>
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
    @SuppressWarnings("unchecked")
    List<Appointment> allAppointments = (List<Appointment>) request.getAttribute("allAppointments");
%>
<%@ include file="/common/header.jsp" %>

<div class="card">
    <h1>View Appointment Details</h1>
    <p class="subtitle">Search Appointment by Number (Task A, Figure 5), or browse every appointment on record.</p>

    <form method="get" action="<%= request.getContextPath() %>/viewAppointment" style="display:flex;gap:0.6rem;align-items:flex-end;flex-wrap:wrap;">
        <div class="field" style="flex:1;min-width:220px;margin-bottom:0;">
            <label for="appointmentNumber">Appointment Number</label>
            <input type="text" id="appointmentNumber" name="appointmentNumber" placeholder="e.g. APT-00001"
                   value="<%= searchedNumber != null ? searchedNumber : "" %>">
        </div>
        <button type="submit" class="btn">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="7"/><path d="m21 21-4.3-4.3"/></svg>
            Search
        </button>
        <a class="btn btn-secondary" href="<%= request.getContextPath() %>/viewAppointment?showAll=1">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="4" rx="1"/><rect x="3" y="10" width="18" height="4" rx="1"/><rect x="3" y="16" width="18" height="4" rx="1"/></svg>
            View All Appointments
        </a>
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

    <div class="form-actions" style="border-top:none;padding-top:0;margin-top:1.25rem;flex-wrap:wrap;">
        <form method="post" action="<%= request.getContextPath() %>/generateBill">
            <input type="hidden" name="appointmentNumber" value="<%= appointment.getAppointmentNumber() %>">
            <button type="submit" class="btn btn-accent">Generate Bill for this Appointment</button>
        </form>

        <% if (!"COMPLETED".equals(appointment.getStatus())) { %>
        <form method="post" action="<%= request.getContextPath() %>/viewAppointment">
            <input type="hidden" name="appointmentNumber" value="<%= appointment.getAppointmentNumber() %>">
            <input type="hidden" name="newStatus" value="COMPLETED">
            <button type="submit" class="btn btn-secondary">Mark Completed</button>
        </form>
        <% } %>
        <% if (!"CANCELLED".equals(appointment.getStatus())) { %>
        <form method="post" action="<%= request.getContextPath() %>/viewAppointment"
              onsubmit="return confirm('Cancel this appointment?');">
            <input type="hidden" name="appointmentNumber" value="<%= appointment.getAppointmentNumber() %>">
            <input type="hidden" name="newStatus" value="CANCELLED">
            <button type="submit" class="btn btn-danger">Cancel Appointment</button>
        </form>
        <% } %>

        <a class="form-hint" style="margin-left:auto;"
           href="<%= request.getContextPath() %>/patients?contact=<%= java.net.URLEncoder.encode(appointment.getPatient().getContactNumber(), "UTF-8") %>">
            View this patient's full history &rarr;
        </a>
    </div>
</div>
<% } %>

<% if (allAppointments != null) { %>
<div class="card">
    <h2>All Appointments <span class="subtitle" style="display:inline;margin:0;font-weight:400;">(<%= allAppointments.size() %>)</span></h2>
    <% if (allAppointments.isEmpty()) { %>
    <p class="subtitle" style="margin-bottom:0;">No appointments on record yet.</p>
    <% } else { %>
    <table>
        <thead>
            <tr>
                <th>Appointment #</th>
                <th>Patient</th>
                <th>Contact</th>
                <th>Dentist</th>
                <th>Treatment</th>
                <th>Date</th>
                <th>Time</th>
                <th>Status</th>
                <th></th>
            </tr>
        </thead>
        <tbody>
            <% for (Appointment a : allAppointments) { %>
            <tr>
                <td><%= a.getAppointmentNumber() %></td>
                <td><%= a.getPatient().getName() %></td>
                <td><%= a.getPatient().getContactNumber() %></td>
                <td><%= a.getDentistName() %></td>
                <td><%= a.getTreatmentType().getName() %></td>
                <td><%= a.getAppointmentDate() %></td>
                <td><%= a.getAppointmentTime() %></td>
                <td><span class="badge badge-<%= a.getStatus().toLowerCase() %>"><%= a.getStatus() %></span></td>
                <td><a href="<%= request.getContextPath() %>/viewAppointment?appointmentNumber=<%= a.getAppointmentNumber() %>">View</a></td>
            </tr>
            <% } %>
        </tbody>
    </table>
    <% } %>
</div>
<% } %>

<%@ include file="/common/footer.jsp" %>
