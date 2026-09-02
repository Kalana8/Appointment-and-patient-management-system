<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
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

    Layout: a fixed left sidebar (brand + primary navigation) next to a
    main content column (a slim top bar showing the page title and the
    signed-in user, then the page's own .container content). The
    scriptlet variable "loggedInUser" declared below is also read by
    common/footer.jsp, which is safe because <%@ include %> is a
    translation-time merge into one generated servlet (same reasoning as
    above) and no other page in this project re-declares that name.
    Logged-out pages (login.jsp) get no sidebar/topbar at all - just the
    plain centered .container - since there is nothing to navigate to yet.
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
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css?v=8">
</head>
<body>
<%
    StaffUser loggedInUser = (StaffUser) session.getAttribute("staffUser");
    String initials = "";
    if (loggedInUser != null && loggedInUser.getFullName() != null) {
        for (String part : loggedInUser.getFullName().trim().split("\\s+")) {
            if (!part.isEmpty() && initials.length() < 2) { initials += part.substring(0, 1).toUpperCase(); }
        }
    }
%>
<% if (loggedInUser != null) { %>
<div class="app-shell">
    <aside class="sidebar" id="sidebar">
        <div class="sidebar-brand">
            <span class="brand-mark">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3c-2.5 0-4 1.4-5.4 1.4C5.2 4.4 4 3.6 3 4.3c-1.2.8-1.2 2.9-.7 4.6.5 1.8 1.6 2.8 1.9 4.9.3 2 .2 4.6 1.4 6.6.7 1.2 1.6 1.6 2.3 1.6 1.4 0 1.6-2.7 1.9-4.4.2-1.3.6-2.2 1.2-2.2s1 .9 1.2 2.2c.3 1.7.5 4.4 1.9 4.4.7 0 1.6-.4 2.3-1.6 1.2-2 1.1-4.6 1.4-6.6.3-2.1 1.4-3.1 1.9-4.9.5-1.7.5-3.8-.7-4.6-1-.7-2.2.1-3.6.1C16 4.4 14.5 3 12 3Z"/></svg>
            </span>
            <span class="brand-text">
                <strong>Sunrise Dental</strong>
                <small>Clinic Management</small>
            </span>
        </div>
        <nav class="side-nav" id="sideNav">
            <div class="side-nav-label">Menu</div>
            <a href="<%= request.getContextPath() %>/menu.jsp" class="side-link <%= "menu".equals(activeNav) ? "active" : "" %>">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7" rx="1.5"/><rect x="14" y="3" width="7" height="7" rx="1.5"/><rect x="3" y="14" width="7" height="7" rx="1.5"/><rect x="14" y="14" width="7" height="7" rx="1.5"/></svg>
                Dashboard
            </a>
            <a href="<%= request.getContextPath() %>/registerAppointment" class="side-link <%= "register".equals(activeNav) ? "active" : "" %>">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="17" rx="2"/><path d="M3 9h18M8 2v4M16 2v4M12 13v4M10 15h4"/></svg>
                Register Appointment
            </a>
            <a href="<%= request.getContextPath() %>/viewAppointment" class="side-link <%= "view".equals(activeNav) ? "active" : "" %>">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="7"/><path d="m21 21-4.3-4.3"/></svg>
                View Appointment
            </a>
            <a href="<%= request.getContextPath() %>/patients" class="side-link <%= "patients".equals(activeNav) ? "active" : "" %>">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="4"/><path d="M4 21v-1a8 8 0 0 1 16 0v1"/></svg>
                Patients
            </a>
            <a href="<%= request.getContextPath() %>/generateBill" class="side-link <%= "bill".equals(activeNav) ? "active" : "" %>">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 3h16v18l-3-2-2 2-2-2-2 2-2-2-2 2-3-2Z"/><path d="M8 8h8M8 12h8M8 16h5"/></svg>
                Generate Bill
            </a>
            <a href="<%= request.getContextPath() %>/reports" class="side-link <%= "reports".equals(activeNav) ? "active" : "" %>">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 3v18h18"/><rect x="7" y="12" width="3" height="6"/><rect x="12.5" y="8" width="3" height="10"/><rect x="18" y="5" width="3" height="13"/></svg>
                Reports
            </a>
            <% if ("ADMINISTRATOR".equals(loggedInUser.getRole())) { %>
            <a href="<%= request.getContextPath() %>/manageStaff" class="side-link <%= "staff".equals(activeNav) ? "active" : "" %>">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                Manage Staff
            </a>
            <% } %>

            <div class="side-nav-general">
                <div class="side-nav-label">General</div>
                <a href="<%= request.getContextPath() %>/help.jsp" class="side-link <%= "help".equals(activeNav) ? "active" : "" %>">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M9.5 9a2.5 2.5 0 0 1 4.9.8c0 1.7-2.4 1.7-2.4 3.5"/><circle cx="12" cy="17" r="0.6" fill="currentColor" stroke="none"/></svg>
                    Help
                </a>
                <a href="<%= request.getContextPath() %>/logout" class="side-link side-link-logout"
                   onclick="return confirm('Are you sure you want to logout?');">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><path d="M16 17l5-5-5-5M21 12H9"/></svg>
                    Logout
                </a>
            </div>
        </nav>
    </aside>
    <div class="sidebar-overlay" id="sidebarOverlay"
         onclick="document.getElementById('sidebar').classList.remove('open'); this.classList.remove('show');"></div>
    <div class="main-wrap">
        <header class="main-topbar">
            <div class="main-topbar-left">
                <button type="button" class="sidebar-toggle" aria-label="Toggle menu"
                        onclick="document.getElementById('sidebar').classList.toggle('open'); document.getElementById('sidebarOverlay').classList.toggle('show');">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="4" y1="6" x2="20" y2="6"/><line x1="4" y1="12" x2="20" y2="12"/><line x1="4" y1="18" x2="20" y2="18"/></svg>
                </button>
                <span class="main-topbar-title"><%= pageTitle %></span>
            </div>
            <div class="session-chip">
                <span class="avatar"><%= initials %></span>
                <span class="session-text">
                    <strong><%= loggedInUser.getFullName() %></strong>
                    <span class="session-role"><%= loggedInUser.getRole().toLowerCase() %></span>
                </span>
            </div>
        </header>
        <div class="container">
<% } else { %>
        <div class="container">
<% } %>
