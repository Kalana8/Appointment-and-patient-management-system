<%@ page import="java.util.List" %>
<%
    request.setAttribute("pageTitle", "Login");
    request.setAttribute("activeNav", "");
%>
<%@ include file="/common/header.jsp" %>

<div class="card" style="max-width:420px;margin:2rem auto 0;">
    <h1>Staff Login</h1>
    <p class="subtitle">Sign in with your receptionist or administrator account.</p>

    <%
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (errorMessage != null) {
    %>
        <div class="alert alert-error"><%= errorMessage %></div>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %>/login">
        <div class="field">
            <label for="username">Username</label>
            <input type="text" id="username" name="username" required
                   value="<%= request.getAttribute("rememberedUsername") != null ? request.getAttribute("rememberedUsername") : "" %>">
        </div>
        <div class="field">
            <label for="password">Password</label>
            <input type="password" id="password" name="password" required>
        </div>
        <div class="field checkbox-row">
            <input type="checkbox" id="rememberMe" name="rememberMe"
                   <%= request.getAttribute("rememberedUsername") != null ? "checked" : "" %>>
            <label for="rememberMe">Remember my username on this computer</label>
        </div>
        <button type="submit" class="btn" style="width:100%;">Login</button>
    </form>

    <p style="font-size:0.8rem;color:#6b7280;margin-top:1.25rem;">
        Demo accounts (sample_data.sql): <code>receptionist1 / recep123</code> and <code>admin1 / admin123</code>.
    </p>
</div>

<%@ include file="/common/footer.jsp" %>
