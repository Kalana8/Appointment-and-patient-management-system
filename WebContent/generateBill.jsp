<%
    if (session.getAttribute("staffUser") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    request.setAttribute("pageTitle", "Generate Bill");
    request.setAttribute("activeNav", "bill");
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<%@ include file="/common/header.jsp" %>

<div class="card" style="max-width:480px;margin:2rem auto 0;">
    <h1>Generate Bill</h1>
    <p class="subtitle">«includes» Calculate Treatment Cost (Task A, Figure 6) -- consultation fee (LKR 500.00) plus the treatment's base cost, computed by the stored procedure <code>sp_generate_bill</code>.</p>

    <% if (errorMessage != null) { %>
    <div class="alert alert-error"><%= errorMessage %></div>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %>/generateBill">
        <div class="field">
            <label for="appointmentNumber">Appointment Number</label>
            <input type="text" id="appointmentNumber" name="appointmentNumber" placeholder="e.g. APT-00001" required>
        </div>
        <button type="submit" class="btn">Generate Bill</button>
    </form>
</div>

<%@ include file="/common/footer.jsp" %>
