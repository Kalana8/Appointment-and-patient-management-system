<%@ page import="dental.model.StaffUser" %>
<%@ page import="dental.model.Appointment" %>
<%@ page import="dental.model.Patient" %>
<%@ page import="dental.service.AppointmentService" %>
<%@ page import="dental.service.PatientService" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.LocalTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%
    if (session.getAttribute("staffUser") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    StaffUser currentStaff = (StaffUser) session.getAttribute("staffUser");
    boolean isAdmin = "ADMINISTRATOR".equals(currentStaff.getRole());
    request.setAttribute("pageTitle", "Menu");
    request.setAttribute("activeNav", "menu");

    // menu.jsp has always done its own session check inline instead of
    // going through a Controller servlet (it's the page LoginServlet and
    // every "Cancel"/logo link land on directly). Everything below calls
    // the Service layer directly for the same reason - see the longer
    // note this comment used to carry, kept short here since the pattern
    // is now used in several places on this page.
    AppointmentService appointmentService = new AppointmentService();
    PatientService patientService = new PatientService();

    List<Appointment> todaysAppointments = appointmentService.findByDate(LocalDate.now());
    int scheduledToday = 0;
    int completedToday = 0;
    int cancelledToday = 0;
    Appointment nextAppointment = null;
    LocalTime now = LocalTime.now();
    for (Appointment a : todaysAppointments) {
        if ("SCHEDULED".equals(a.getStatus())) {
            scheduledToday++;
            if (nextAppointment == null && !a.getAppointmentTime().isBefore(now)) {
                nextAppointment = a;
            }
        } else if ("COMPLETED".equals(a.getStatus())) {
            completedToday++;
        } else if ("CANCELLED".equals(a.getStatus())) {
            cancelledToday++;
        }
    }

    List<Patient> recentPatients = patientService.findRecent(5);
    int totalPatients = patientService.findAll().size();

    String todayFormatted = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"));
    DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("h:mm a");
%>
<%@ include file="/common/header.jsp" %>

<div class="dash-header">
    <div>
        <h1>Welcome back, <%= currentStaff.getFullName() %> &#x1F44B;</h1>
        <p class="subtitle" style="margin:0;">
            <% if (todaysAppointments.isEmpty()) { %>
                No appointments scheduled for today.
            <% } else { %>
                <%= todaysAppointments.size() %> appointment<%= todaysAppointments.size() == 1 ? "" : "s" %> scheduled today.
            <% } %>
        </p>
    </div>
    <span class="dash-date">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="17" rx="2"/><path d="M3 9h18M8 2v4M16 2v4"/></svg>
        <%= todayFormatted %>
    </span>
</div>

<div class="section-heading">Today at a Glance</div>
<div class="stat-grid">
    <div class="stat-card">
        <span class="stat-icon blue">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="17" rx="2"/><path d="M3 9h18M8 2v4M16 2v4M12 13v4M10 15h4"/></svg>
        </span>
        <span>
            <span class="stat-value"><%= todaysAppointments.size() %></span>
            <span class="stat-label">Appointments Today</span>
        </span>
    </div>
    <div class="stat-card">
        <span class="stat-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 3"/></svg>
        </span>
        <span>
            <span class="stat-value"><%= scheduledToday %></span>
            <span class="stat-label">Scheduled</span>
        </span>
    </div>
    <div class="stat-card">
        <span class="stat-icon amber">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6 9 17l-5-5"/></svg>
        </span>
        <span>
            <span class="stat-value"><%= completedToday %></span>
            <span class="stat-label">Completed Today</span>
        </span>
    </div>
    <div class="stat-card">
        <span class="stat-icon rose">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="m15 9-6 6M9 9l6 6"/></svg>
        </span>
        <span>
            <span class="stat-value"><%= cancelledToday %></span>
            <span class="stat-label">Cancelled Today</span>
        </span>
    </div>
</div>

<div class="dash-grid">
    <div class="dash-col-main">

        <div class="card">
            <div class="card-header-row">
                <div class="section-heading" style="margin:0;">Today's Appointments</div>
                <a class="link-more" href="<%= request.getContextPath() %>/viewAppointment?showAll=1">View all &rarr;</a>
            </div>
            <% if (todaysAppointments.isEmpty()) { %>
                <p class="empty-state">Nothing on the books for today yet -- <a href="<%= request.getContextPath() %>/registerAppointment">register an appointment</a> to get started.</p>
            <% } else { %>
                <div class="appt-list">
                    <% for (Appointment a : todaysAppointments) { %>
                    <a class="appt-row" href="<%= request.getContextPath() %>/viewAppointment?appointmentNumber=<%= a.getAppointmentNumber() %>">
                        <span class="appt-time"><%= a.getAppointmentTime().format(timeFmt) %></span>
                        <span class="appt-who">
                            <strong><%= a.getPatient().getName() %></strong>
                            <small><%= a.getTreatmentType().getName() %> &middot; <%= a.getDentistName() %></small>
                        </span>
                        <span class="badge badge-<%= a.getStatus().toLowerCase() %>"><%= a.getStatus() %></span>
                    </a>
                    <% } %>
                </div>
            <% } %>
        </div>

        <div class="card">
            <div class="card-header-row">
                <div class="section-heading" style="margin:0;">Recent Patients</div>
                <a class="link-more" href="<%= request.getContextPath() %>/patients">View all &rarr;</a>
            </div>
            <% if (recentPatients.isEmpty()) { %>
                <p class="empty-state">No patients registered yet.</p>
            <% } else { %>
                <div class="patient-list">
                    <% for (Patient p : recentPatients) {
                        String pInitials = "";
                        for (String part : p.getName().trim().split("\\s+")) {
                            if (!part.isEmpty() && pInitials.length() < 2) { pInitials += part.substring(0, 1).toUpperCase(); }
                        }
                    %>
                    <a class="patient-row" href="<%= request.getContextPath() %>/patients?contact=<%= java.net.URLEncoder.encode(p.getContactNumber(), "UTF-8") %>">
                        <span class="patient-avatar"><%= pInitials %></span>
                        <span class="patient-who">
                            <strong><%= p.getName() %></strong>
                            <small><%= p.getContactNumber() %></small>
                        </span>
                        <span class="link-more">History &rarr;</span>
                    </a>
                    <% } %>
                </div>
            <% } %>
        </div>

    </div>

    <div class="dash-col-side">

        <div class="card next-appt-card">
            <div class="section-heading" style="margin:0 0 0.9rem;">Next Appointment</div>
            <% if (nextAppointment != null) { %>
                <div class="next-appt-time"><%= nextAppointment.getAppointmentTime().format(timeFmt) %></div>
                <div class="next-appt-patient"><%= nextAppointment.getPatient().getName() %></div>
                <div class="next-appt-detail"><%= nextAppointment.getTreatmentType().getName() %> &middot; <%= nextAppointment.getDentistName() %></div>
                <a class="btn btn-block" href="<%= request.getContextPath() %>/viewAppointment?appointmentNumber=<%= nextAppointment.getAppointmentNumber() %>">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="7"/><path d="m21 21-4.3-4.3"/></svg>
                    View Appointment
                </a>
            <% } else { %>
                <p class="empty-state" style="margin:0;">No more scheduled appointments for today.</p>
            <% } %>
        </div>

        <div class="card">
            <div class="section-heading" style="margin:0 0 0.9rem;">Quick Actions</div>
            <div class="quick-actions">
                <a class="quick-action" href="<%= request.getContextPath() %>/registerAppointment">
                    <span class="tile-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="17" rx="2"/><path d="M3 9h18M8 2v4M16 2v4M12 13v4M10 15h4"/></svg>
                    </span>
                    Register Appointment
                </a>
                <a class="quick-action" href="<%= request.getContextPath() %>/patients">
                    <span class="tile-icon green">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="4"/><path d="M4 21v-1a8 8 0 0 1 16 0v1"/></svg>
                    </span>
                    Patients <span class="quick-action-count"><%= totalPatients %></span>
                </a>
                <a class="quick-action" href="<%= request.getContextPath() %>/generateBill">
                    <span class="tile-icon amber">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 3h16v18l-3-2-2 2-2-2-2 2-2-2-2 2-3-2Z"/><path d="M8 8h8M8 12h8M8 16h5"/></svg>
                    </span>
                    Generate Bill
                </a>
                <% if (isAdmin) { %>
                <a class="quick-action" href="<%= request.getContextPath() %>/manageStaff">
                    <span class="tile-icon rose">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                    </span>
                    Manage Staff
                </a>
                <% } %>
            </div>
        </div>

    </div>
</div>

<%@ include file="/common/footer.jsp" %>
