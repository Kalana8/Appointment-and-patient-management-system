<%@ page import="dental.model.Patient" %>
<%@ page import="dental.model.Appointment" %>
<%@ page import="java.util.List" %>
<%
    if (session.getAttribute("staffUser") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    request.setAttribute("pageTitle", "Patients");
    request.setAttribute("activeNav", "patients");

    Patient patient = (Patient) request.getAttribute("patient");
    @SuppressWarnings("unchecked")
    List<Appointment> history = (List<Appointment>) request.getAttribute("history");
    @SuppressWarnings("unchecked")
    List<Patient> results = (List<Patient>) request.getAttribute("results");
    String notFoundMessage = (String) request.getAttribute("notFoundMessage");
    String searchedQuery = (String) request.getAttribute("searchedQuery");
%>
<%@ include file="/common/header.jsp" %>

<div class="card">
    <h1>Patients</h1>
    <p class="subtitle">Every client is matched by contact number, so a returning patient has one record with a full visit history instead of a fresh one each time they come in.</p>

    <form method="get" action="<%= request.getContextPath() %>/patients" style="display:flex;gap:0.6rem;align-items:flex-end;flex-wrap:wrap;">
        <div class="field" style="flex:1;min-width:220px;margin-bottom:0;">
            <label for="q">Search by name or contact number</label>
            <input type="text" id="q" name="q" placeholder="e.g. Amal or 0771234567"
                   value="<%= searchedQuery != null ? searchedQuery : "" %>">
        </div>
        <button type="submit" class="btn">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="7"/><path d="m21 21-4.3-4.3"/></svg>
            Search
        </button>
        <% if (patient != null || searchedQuery != null) { %>
        <a class="btn btn-secondary" href="<%= request.getContextPath() %>/patients">All Patients</a>
        <% } %>
    </form>
</div>

<% if (notFoundMessage != null) { %>
<div class="alert alert-error"><%= notFoundMessage %></div>
<% } %>

<% if (patient != null) { %>
<div class="card">
    <h2><%= patient.getName() %></h2>
    <table>
        <tr><th>Contact Number</th><td><%= patient.getContactNumber() %></td></tr>
        <tr><th>Address</th><td><%= patient.getAddress() %></td></tr>
        <tr><th>Date of Birth</th><td><%= patient.getDateOfBirth() != null ? patient.getDateOfBirth() : "-" %></td></tr>
        <tr><th>Total Visits</th><td><%= history != null ? history.size() : 0 %></td></tr>
    </table>
    <a class="btn" style="margin-top:1.25rem;"
       href="<%= request.getContextPath() %>/registerAppointment?contactNumber=<%= java.net.URLEncoder.encode(patient.getContactNumber(), "UTF-8") %>">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 5v14M5 12h14"/></svg>
        Register New Appointment for this Patient
    </a>
</div>

<div class="card">
    <h2>Visit History</h2>
    <% if (history == null || history.isEmpty()) { %>
    <p class="subtitle" style="margin-bottom:0;">No appointments on record yet.</p>
    <% } else { %>
    <table>
        <thead>
            <tr>
                <th>Appointment #</th>
                <th>Date</th>
                <th>Time</th>
                <th>Dentist</th>
                <th>Treatment</th>
                <th>Status</th>
                <th></th>
            </tr>
        </thead>
        <tbody>
            <% for (Appointment visit : history) { %>
            <tr>
                <td><%= visit.getAppointmentNumber() %></td>
                <td><%= visit.getAppointmentDate() %></td>
                <td><%= visit.getAppointmentTime() %></td>
                <td><%= visit.getDentistName() %></td>
                <td><%= visit.getTreatmentType().getName() %></td>
                <td><span class="badge badge-<%= visit.getStatus().toLowerCase() %>"><%= visit.getStatus() %></span></td>
                <td><a href="<%= request.getContextPath() %>/viewAppointment?appointmentNumber=<%= visit.getAppointmentNumber() %>">View</a></td>
            </tr>
            <% } %>
        </tbody>
    </table>
    <% } %>
</div>
<% } else if (results != null) { %>
<div class="card">
    <h2><%= searchedQuery != null ? "Search Results" : "All Patients" %> <span class="subtitle" style="display:inline;margin:0;font-weight:400;">(<%= results.size() %>)</span></h2>
    <% if (results.isEmpty()) { %>
    <p class="subtitle" style="margin-bottom:0;">No matching patients found.</p>
    <% } else { %>
    <table>
        <thead>
            <tr>
                <th>Name</th>
                <th>Contact Number</th>
                <th>Address</th>
                <th></th>
            </tr>
        </thead>
        <tbody>
            <% for (Patient p : results) { %>
            <tr>
                <td><%= p.getName() %></td>
                <td><%= p.getContactNumber() %></td>
                <td><%= p.getAddress() %></td>
                <td><a href="<%= request.getContextPath() %>/patients?contact=<%= java.net.URLEncoder.encode(p.getContactNumber(), "UTF-8") %>">View History</a></td>
            </tr>
            <% } %>
        </tbody>
    </table>
    <% } %>
</div>
<% } %>

<%@ include file="/common/footer.jsp" %>
