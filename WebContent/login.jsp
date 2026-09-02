<%@ page import="java.util.List" %>
<%
    request.setAttribute("pageTitle", "Login");
    request.setAttribute("activeNav", "");
%>
<%@ include file="/common/header.jsp" %>

<div class="auth-shell">
<div class="card auth-card">
    <div class="auth-logo">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3c-2.5 0-4 1.4-5.4 1.4C5.2 4.4 4 3.6 3 4.3c-1.2.8-1.2 2.9-.7 4.6.5 1.8 1.6 2.8 1.9 4.9.3 2 .2 4.6 1.4 6.6.7 1.2 1.6 1.6 2.3 1.6 1.4 0 1.6-2.7 1.9-4.4.2-1.3.6-2.2 1.2-2.2s1 .9 1.2 2.2c.3 1.7.5 4.4 1.9 4.4.7 0 1.6-.4 2.3-1.6 1.2-2 1.1-4.6 1.4-6.6.3-2.1 1.4-3.1 1.9-4.9.5-1.7.5-3.8-.7-4.6-1-.7-2.2.1-3.6.1C16 4.4 14.5 3 12 3Z"/></svg>
    </div>
    <h1>Welcome back</h1>
    <p class="subtitle">Sign in with your receptionist or administrator account.</p>

    <%
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (errorMessage != null) {
    %>
        <div class="alert alert-error">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><line x1="12" y1="8" x2="12" y2="13"/><circle cx="12" cy="16.2" r="0.6" fill="currentColor" stroke="none"/></svg>
            <div><%= errorMessage %></div>
        </div>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %>/login">
        <div class="field">
            <label for="username">Username</label>
            <input type="text" id="username" name="username" required placeholder="e.g. receptionist1"
                   value="<%= request.getAttribute("rememberedUsername") != null ? request.getAttribute("rememberedUsername") : "" %>">
        </div>
        <div class="field">
            <label for="password">Password</label>
            <input type="password" id="password" name="password" required placeholder="Enter your password">
        </div>
        <div class="field checkbox-row">
            <input type="checkbox" id="rememberMe" name="rememberMe"
                   <%= request.getAttribute("rememberedUsername") != null ? "checked" : "" %>>
            <label for="rememberMe">Remember my username on this computer</label>
        </div>
        <button type="submit" class="btn" style="width:100%;">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"/><path d="M10 17l5-5-5-5M15 12H3"/></svg>
            Login
        </button>
    </form>

    <div class="demo-note">
        Demo accounts (sample_data.sql): <code>receptionist1 / recep123</code> and <code>admin1 / admin123</code>.
    </div>
</div>
</div>

<%@ include file="/common/footer.jsp" %>
