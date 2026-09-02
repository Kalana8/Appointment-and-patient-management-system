<%@ page isErrorPage="true" %>
<%
    request.setAttribute("pageTitle", "Error");
    request.setAttribute("activeNav", "");
%>
<%@ include file="/common/header.jsp" %>

<div class="card" style="max-width:560px;margin:2rem auto 0;">
    <div class="alert alert-error">
        <strong>Something went wrong.</strong><br>
        <%= exception != null ? exception.getMessage() : "An unexpected error occurred." %>
    </div>
    <a class="btn" href="<%= request.getContextPath() %>/menu.jsp">Back to Menu</a>
</div>

<%@ include file="/common/footer.jsp" %>
