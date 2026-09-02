<%-- Welcome file (declared in web.xml); simply routes to the LoginServlet
     so the "remember me" cookie check in LoginServlet.doGet() always runs,
     rather than duplicating that logic here. --%>
<% response.sendRedirect(request.getContextPath() + "/login"); %>
