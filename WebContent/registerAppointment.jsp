<%@ page import="java.util.List" %>
<%@ page import="dental.model.TreatmentType" %>
<%
    if (session.getAttribute("staffUser") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    request.setAttribute("pageTitle", "Register Appointment");
    request.setAttribute("activeNav", "register");

    @SuppressWarnings("unchecked")
    List<TreatmentType> treatmentTypes = (List<TreatmentType>) request.getAttribute("treatmentTypes");
    List<String> validationErrors = (List<String>) request.getAttribute("errors");

    String fPatientName = request.getAttribute("patientName") != null ? (String) request.getAttribute("patientName") : "";
    String fAddress = request.getAttribute("address") != null ? (String) request.getAttribute("address") : "";
    String fContactNumber = request.getAttribute("contactNumber") != null ? (String) request.getAttribute("contactNumber") : "";
    String fDentistName = request.getAttribute("dentistName") != null ? (String) request.getAttribute("dentistName") : "";
    boolean returningPatient = Boolean.TRUE.equals(request.getAttribute("returningPatient"));
%>
<%@ include file="/common/header.jsp" %>

<div class="card">
    <h1>Register New Appointment</h1>
    <p class="subtitle">Realises the "Register New Appointment" use case, which includes Calculate Treatment Cost (Task A, Figure 4).</p>

    <% if (returningPatient) { %>
    <div class="alert alert-success">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6 9 17l-5-5"/></svg>
        <div>Returning patient -- details pre-filled from their record. Just add the new appointment's dentist, treatment, date and time.</div>
    </div>
    <% } %>

    <% if (validationErrors != null && !validationErrors.isEmpty()) { %>
    <div class="alert alert-error">
        <strong>Please fix the following:</strong>
        <ul>
            <% for (String err : validationErrors) { %>
            <li><%= err %></li>
            <% } %>
        </ul>
    </div>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %>/registerAppointment" id="appointmentForm">
        <div class="form-section-title"><span class="step-num">1</span> Patient Details</div>
        <div class="form-grid">
            <div class="field span-2">
                <label for="patientName">Patient Full Name</label>
                <input type="text" id="patientName" name="patientName" value="<%= fPatientName %>" autofocus required>
            </div>
            <div class="field">
                <label for="address">Address</label>
                <input type="text" id="address" name="address" value="<%= fAddress %>" required>
            </div>
            <div class="field">
                <label for="contactNumber">Contact Number (07XXXXXXXX)</label>
                <input type="tel" id="contactNumber" name="contactNumber" value="<%= fContactNumber %>" placeholder="07XXXXXXXX" required>
            </div>
        </div>

        <div class="form-section-title"><span class="step-num">2</span> Appointment Details</div>
        <div class="form-grid">
            <div class="field">
                <label for="dentistName">Dentist Name</label>
                <input type="text" id="dentistName" name="dentistName" value="<%= fDentistName %>" required>
            </div>
            <div class="field">
                <label for="treatmentTypeId">Treatment Type</label>
                <select id="treatmentTypeId" name="treatmentTypeId" required onchange="updateCostPreview()">
                    <option value="">-- Select treatment --</option>
                    <% if (treatmentTypes != null) {
                        for (TreatmentType t : treatmentTypes) { %>
                    <option value="<%= t.getTreatmentTypeId() %>"><%= t.getName() %> (base cost: <%= t.getBaseCost() %>)</option>
                    <% } } %>
                </select>
            </div>
            <div class="field span-2" id="costPreviewBox" style="display:none;">
                <div class="alert alert-success" id="costPreviewText" style="margin-bottom:0;"></div>
            </div>
            <div class="field">
                <label for="appointmentDate">Appointment Date</label>
                <input type="date" id="appointmentDate" name="appointmentDate" required>
            </div>
            <div class="field">
                <label for="appointmentTime">Appointment Time</label>
                <input type="time" id="appointmentTime" name="appointmentTime" required>
            </div>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6 9 17l-5-5"/></svg>
                Register Appointment
            </button>
            <a class="btn btn-secondary" href="<%= request.getContextPath() %>/menu.jsp">Cancel</a>
            <span class="form-hint">All fields required</span>
        </div>
    </form>
</div>

<script>
    /*
     * Calls the TreatmentCostServlet JSON web-service endpoint
     * asynchronously so the receptionist sees the estimated cost
     * update live as they choose a treatment type -- demonstrates
     * the "distributed application with web services" requirement
     * from the Task B brief in the running UI, not only on the
     * server side.
     */
    function updateCostPreview() {
        var select = document.getElementById('treatmentTypeId');
        var box = document.getElementById('costPreviewBox');
        var text = document.getElementById('costPreviewText');
        if (!select.value) {
            box.style.display = 'none';
            return;
        }
        fetch('<%= request.getContextPath() %>/treatmentCost?treatmentTypeId=' + encodeURIComponent(select.value))
            .then(function (resp) { return resp.json(); })
            .then(function (data) {
                if (data.error) {
                    box.style.display = 'none';
                    return;
                }
                text.textContent = data.name + ': LKR ' + data.baseCost +
                    ' (plus the standard LKR 500.00 consultation fee, added when the bill is generated)';
                box.style.display = 'block';
            })
            .catch(function () { box.style.display = 'none'; });
    }
</script>

<%@ include file="/common/footer.jsp" %>
