<%-- Shared page footer, included the same way as header.jsp (Lecture 8 include
     directive). Closes whichever structure header.jsp opened for this page:
     the sidebar/main-wrap/app-shell (signed-in pages) or just the plain
     .container (login.jsp, which has no sidebar to close). "loggedInUser" is
     the same scriptlet variable header.jsp declared - see the note at the
     top of header.jsp for why that is safe across a translation-time
     <%@ include %>. --%>
    </div><%-- .container --%>
<% if (loggedInUser != null) { %>
    </div><%-- .main-wrap --%>
</div><%-- .app-shell --%>
<% } %>
<div class="footer-note">Sunrise Dental Clinic Management System &middot; CIS6003 Advanced Programming Coursework &middot; Task B</div>
</body>
</html>
