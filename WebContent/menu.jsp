<%@ page import="dental.model.StaffUser" %>
<%
    if (session.getAttribute("staffUser") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    StaffUser currentStaff = (StaffUser) session.getAttribute("staffUser");
    boolean isAdmin = "ADMINISTRATOR".equals(currentStaff.getRole());
    request.setAttribute("pageTitle", "Menu");
    request.setAttribute("activeNav", "menu");
%>
<%@ include file="/common/header.jsp" %>

<div class="card">
    <h1>Welcome, <%= currentStaff.getFullName() %></h1>
    <p class="subtitle">What would you like to do today?</p>

    <div class="grid-menu">
        <a class="menu-tile" href="<%= request.getContextPath() %>/registerAppointment">
            <span class="tile-title">Register New Appointment</span>
            <span class="tile-desc">Record a new patient appointment and get an appointment number.</span>
        </a>
        <a class="menu-tile" href="<%= request.getContextPath() %>/viewAppointment">
            <span class="tile-title">View Appointment Details</span>
            <span class="tile-desc">Search for an appointment by its appointment number.</span>
        </a>
        <a class="menu-tile" href="<%= request.getContextPath() %>/generateBill">
            <span class="tile-title">Generate Bill</span>
            <span class="tile-desc">Calculate treatment cost and generate a bill for an appointment.</span>
        </a>
        <a class="menu-tile" href="<%= request.getContextPath() %>/reports">
            <span class="tile-title">Reports</span>
            <span class="tile-desc">Daily appointments, revenue and treatment popularity reports.</span>
        </a>
        <% if (isAdmin) { %>
        <a class="menu-tile" href="<%= request.getContextPath() %>/manageStaff">
            <span class="tile-title">Manage Staff Accounts</span>
            <span class="tile-desc">Administrator only: view and create receptionist/administrator accounts.</span>
        </a>
        <% } %>
        <a class="menu-tile" href="<%= request.getContextPath() %>/help.jsp">
            <span class="tile-title">Help</span>
            <span class="tile-desc">Quick guide to using the system.</span>
        </a>
    </div>
</div>

<%@ include file="/common/footer.jsp" %>
