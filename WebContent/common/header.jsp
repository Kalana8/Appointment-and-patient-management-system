<%@ page import="dental.model.StaffUser" %>
<%--
    Shared page header, pulled in with the JSP include directive
    (Lecture 8, JSP Elements - Directive: "<%@ include file="header.jsp" %>").
    The including page sets the "pageTitle" and "activeNav" REQUEST
    ATTRIBUTES before the include line (request.setAttribute(...)), and
    this file reads them back with request.getAttribute(...). A bare
    scriptlet variable declared by the includer would also work at
    runtime (the include directive is a translation-time/static include,
    so the two files are merged into one generated servlet), but Eclipse's
    JSP editor validates header.jsp in isolation and cannot see variables
    declared only in whichever page happens to include it - using request
    attributes instead keeps the same one-file, no-JSTL-dependency design
    while resolving cleanly under Eclipse's validator too.
--%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <%
        String pageTitle = (String) request.getAttribute("pageTitle");
        String activeNav = (String) request.getAttribute("activeNav");
        if (activeNav == null) { activeNav = ""; }
    %>
    <title><%= pageTitle %> - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<%
    StaffUser loggedInUser = (StaffUser) session.getAttribute("staffUser");
%>
<div class="topbar">
    <div class="brand">Sunrise Dental Clinic <small>Appointment &amp; Patient Management System</small></div>
    <% if (loggedInUser != null) { %>
    <div class="session-info">
        Signed in as <strong><%= loggedInUser.getFullName() %></strong>
        (<%= loggedInUser.getRole() %>)
    </div>
    <% } %>
</div>
<% if (loggedInUser != null) { %>
<div class="navbar">
    <a href="<%= request.getContextPath() %>/menu.jsp" <%= "menu".equals(activeNav) ? "style=\"font-weight:700;\"" : "" %>>Menu</a>
    <a href="<%= request.getContextPath() %>/registerAppointment" <%= "register".equals(activeNav) ? "style=\"font-weight:700;\"" : "" %>>Register Appointment</a>
    <a href="<%= request.getContextPath() %>/viewAppointment" <%= "view".equals(activeNav) ? "style=\"font-weight:700;\"" : "" %>>View Appointment</a>
    <a href="<%= request.getContextPath() %>/generateBill" <%= "bill".equals(activeNav) ? "style=\"font-weight:700;\"" : "" %>>Generate Bill</a>
    <a href="<%= request.getContextPath() %>/reports" <%= "reports".equals(activeNav) ? "style=\"font-weight:700;\"" : "" %>>Reports</a>
    <% if ("ADMINISTRATOR".equals(loggedInUser.getRole())) { %>
    <a href="<%= request.getContextPath() %>/manageStaff" <%= "staff".equals(activeNav) ? "style=\"font-weight:700;\"" : "" %>>Manage Staff</a>
    <% } %>
    <a href="<%= request.getContextPath() %>/help.jsp" <%= "help".equals(activeNav) ? "style=\"font-weight:700;\"" : "" %>>Help</a>
    <a href="<%= request.getContextPath() %>/logout">Logout</a>
</div>
<% } %>
<div class="container">
