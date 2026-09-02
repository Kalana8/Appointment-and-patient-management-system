<%
    if (session.getAttribute("staffUser") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    request.setAttribute("pageTitle", "Help");
    request.setAttribute("activeNav", "help");
%>
<%@ include file="/common/header.jsp" %>

<div class="card">
    <h1>Help &amp; Quick Guide</h1>
    <p class="subtitle">Realises the "Access Help" use case available to every staff member (Task A, Figure 1).</p>

    <h3>Register New Appointment</h3>
    <p>Enter the patient's details, choose a dentist and treatment type, and pick a date/time. The
       system checks the treatment cost live using the Treatment Cost web service and validates every
       field before saving. On success you are given an appointment number (e.g. <code>APT-00001</code>)
       generated automatically by the database.</p>

    <h3>View Appointment Details</h3>
    <p>Search using the appointment number given at registration to see the patient, dentist and
       treatment details, and to jump straight into Generate Bill for that appointment.</p>

    <h3>Generate Bill</h3>
    <p>Enter an appointment number to calculate the bill: a fixed LKR 500.00 consultation fee plus the
       treatment's base cost. This calls the <code>sp_generate_bill</code> stored procedure, which also
       prevents a duplicate bill being created for the same appointment.</p>

    <h3>Print Receipt</h3>
    <p>From the bill summary, click "Print Receipt" to record the payment and open a printable receipt.
       Use your browser's print dialog (or Ctrl/Cmd+P) to print or save it as a PDF.</p>

    <h3>Reports</h3>
    <p>View the daily appointment report, revenue report and treatment popularity report -- generated
       from live database views.</p>

    <h3>Manage Staff Accounts (Administrators only)</h3>
    <p>View existing staff accounts and create new receptionist or administrator accounts.</p>
</div>

<%@ include file="/common/footer.jsp" %>
