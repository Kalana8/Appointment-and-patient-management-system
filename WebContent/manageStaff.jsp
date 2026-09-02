<%@ page import="java.util.List" %>
<%@ page import="dental.model.StaffUser" %>
<%@ page import="jakarta.servlet.http.HttpServletResponse" %>
<%
    if (session.getAttribute("staffUser") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    StaffUser currentStaff = (StaffUser) session.getAttribute("staffUser");
    if (!"ADMINISTRATOR".equals(currentStaff.getRole())) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only administrators can manage staff accounts.");
        return;
    }
    request.setAttribute("pageTitle", "Manage Staff Accounts");
    request.setAttribute("activeNav", "staff");

    @SuppressWarnings("unchecked")
    List<StaffUser> staffList = (List<StaffUser>) request.getAttribute("staffList");
    @SuppressWarnings("unchecked")
    List<String> validationErrors = (List<String>) request.getAttribute("errors");
    String successMessage = (String) request.getAttribute("successMessage");
%>
<%@ include file="/common/header.jsp" %>

<div class="card">
    <h1>Manage Staff Accounts</h1>
    <p class="subtitle">Administrator-only use case (Task A, Figure 1) -- polymorphic Receptionist / Administrator accounts (Lecture 2, Polymorphism &amp; Abstraction).</p>

    <table>
        <thead>
        <tr><th>Staff ID</th><th>Username</th><th>Full Name</th><th>Role</th><th>Active</th></tr>
        </thead>
        <tbody>
        <% if (staffList != null) {
            for (StaffUser s : staffList) { %>
        <tr>
            <td><%= s.getStaffId() %></td>
            <td><%= s.getUsername() %></td>
            <td><%= s.getFullName() %></td>
            <td><span class="badge badge-<%= s.getRole().toLowerCase() %>"><%= s.getRole() %></span></td>
            <td><%= s.isActive() ? "Yes" : "No" %></td>
        </tr>
        <% } } %>
        </tbody>
    </table>
</div>

<div class="card" style="max-width:480px;">
    <h2>Create New Staff Account</h2>

    <% if (successMessage != null) { %>
    <div class="alert alert-success"><%= successMessage %></div>
    <% } %>
    <% if (validationErrors != null && !validationErrors.isEmpty()) { %>
    <div class="alert alert-error">
        <ul>
            <% for (String err : validationErrors) { %>
            <li><%= err %></li>
            <% } %>
        </ul>
    </div>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %>/manageStaff">
        <div class="field">
            <label for="username">Username</label>
            <input type="text" id="username" name="username" required>
        </div>
        <div class="field">
            <label for="password">Password (min. 6 characters)</label>
            <input type="password" id="password" name="password" required>
        </div>
        <div class="field">
            <label for="fullName">Full Name</label>
            <input type="text" id="fullName" name="fullName" required>
        </div>
        <div class="field">
            <label for="role">Role</label>
            <select id="role" name="role" required>
                <option value="RECEPTIONIST">Receptionist</option>
                <option value="ADMINISTRATOR">Administrator</option>
            </select>
        </div>
        <button type="submit" class="btn">Create Account</button>
    </form>
</div>

<%@ include file="/common/footer.jsp" %>
