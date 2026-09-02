<%
    if (session.getAttribute("staffUser") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    request.setAttribute("pageTitle", "Appointment Registered");
    request.setAttribute("activeNav", "register");
    String appointmentNumber = (String) request.getAttribute("appointmentNumber");
%>
<%@ include file="/common/header.jsp" %>

<div class="card" style="max-width:520px;margin:2rem auto 0;text-align:center;">
    <div class="alert alert-success">Appointment registered successfully.</div>
    <h1>Appointment Number</h1>
    <p style="font-size:2rem;font-weight:800;color:#0f766e;letter-spacing:0.05em;"><%= appointmentNumber %></p>
    <p class="subtitle">Generated automatically by the database trigger <code>trg_generate_appointment_number</code>. Please give this number to the patient -- it is needed to view the appointment or generate a bill later.</p>

    <div style="margin-top:1.5rem;">
        <a class="btn" href="<%= request.getContextPath() %>/viewAppointment?appointmentNumber=<%= appointmentNumber %>">View Appointment</a>
        <a class="btn btn-secondary" href="<%= request.getContextPath() %>/registerAppointment">Register Another</a>
        <a class="btn btn-secondary" href="<%= request.getContextPath() %>/menu.jsp">Back to Menu</a>
    </div>
</div>

<%@ include file="/common/footer.jsp" %>
