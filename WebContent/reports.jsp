<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    if (session.getAttribute("staffUser") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    request.setAttribute("pageTitle", "Reports");
    request.setAttribute("activeNav", "reports");

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> dailyReport = (List<Map<String, Object>>) request.getAttribute("dailyReport");
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> revenueReport = (List<Map<String, Object>>) request.getAttribute("revenueReport");
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> treatmentReport = (List<Map<String, Object>>) request.getAttribute("treatmentReport");
%>
<%@ include file="/common/header.jsp" %>

<div class="card">
    <h1>Reports</h1>
    <p class="subtitle">
        Reads directly from three MySQL reporting views (<code>v_daily_appointment_report</code>,
        <code>v_revenue_report</code>, <code>v_treatment_popularity_report</code>) defined in
        database/schema.sql -- an "appropriate use of advanced database features" and a "suitable
        set of reports... to facilitate decision-making" (Task B brief).
    </p>
</div>

<div class="card">
    <h2>Daily Appointment Report</h2>
    <table>
        <thead><tr><th>Date</th><th>Total</th><th>Completed</th><th>Cancelled</th><th>Scheduled</th></tr></thead>
        <tbody>
        <% if (dailyReport != null && !dailyReport.isEmpty()) {
            for (Map<String, Object> row : dailyReport) { %>
        <tr>
            <td><%= row.get("appointment_date") %></td>
            <td><%= row.get("total_appointments") %></td>
            <td><%= row.get("completed") %></td>
            <td><%= row.get("cancelled") %></td>
            <td><%= row.get("scheduled") %></td>
        </tr>
        <% } } else { %>
        <tr><td colspan="5" style="color:#6b7280;">No appointment data yet.</td></tr>
        <% } %>
        </tbody>
    </table>
</div>

<div class="card">
    <h2>Revenue Report</h2>
    <table>
        <thead><tr><th>Bill Date</th><th>Bills Issued</th><th>Total Revenue (LKR)</th></tr></thead>
        <tbody>
        <% if (revenueReport != null && !revenueReport.isEmpty()) {
            for (Map<String, Object> row : revenueReport) { %>
        <tr>
            <td><%= row.get("bill_date") %></td>
            <td><%= row.get("bills_issued") %></td>
            <td><%= row.get("total_revenue") %></td>
        </tr>
        <% } } else { %>
        <tr><td colspan="3" style="color:#6b7280;">No billing data yet.</td></tr>
        <% } %>
        </tbody>
    </table>
</div>

<div class="card">
    <h2>Treatment Popularity Report</h2>
    <table>
        <thead><tr><th>Treatment Type</th><th>Times Booked</th><th>Revenue Generated (LKR)</th></tr></thead>
        <tbody>
        <% if (treatmentReport != null && !treatmentReport.isEmpty()) {
            for (Map<String, Object> row : treatmentReport) { %>
        <tr>
            <td><%= row.get("treatment_type") %></td>
            <td><%= row.get("times_booked") %></td>
            <td><%= row.get("revenue_from_treatment") %></td>
        </tr>
        <% } } else { %>
        <tr><td colspan="3" style="color:#6b7280;">No treatment data yet.</td></tr>
        <% } %>
        </tbody>
    </table>
</div>

<%@ include file="/common/footer.jsp" %>
