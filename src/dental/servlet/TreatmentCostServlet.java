package dental.servlet;

import dental.model.TreatmentType;
import dental.service.TreatmentTypeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Lightweight JSON web-service endpoint, called asynchronously (AJAX,
 * fetch()) from registerAppointment.jsp and generateBill.jsp so the
 * receptionist sees the treatment cost update live as they pick a
 * treatment type, without a full page reload.
 *
 * This is the concrete "web service" referred to in the Task B report's
 * distributed-application discussion: the assignment brief requires the
 * program to "be a distributed application with web services", and
 * Lecture 6 (Java Web Development) describes Java EE itself as providing
 * "enterprise features such as distributed computing and web services" --
 * every servlet in this application is already a distributed, client
 * (browser) / server (Tomcat) endpoint in that sense, and this servlet
 * additionally exposes a narrow, callable JSON contract (GET
 * ?treatmentTypeId=n -> {"name":...,"baseCost":...}) consumed
 * asynchronously by client-side JavaScript, rather than only ever
 * returning full HTML pages like the other controllers.
 *
 * No external JSON library was introduced in the module's lectures, so
 * the (very small, fixed-shape) JSON body is written by hand rather than
 * pulling in a dependency the syllabus never covers.
 */
public class TreatmentCostServlet extends HttpServlet {

    private final TreatmentTypeService treatmentTypeService = new TreatmentTypeService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            int treatmentTypeId = Integer.parseInt(request.getParameter("treatmentTypeId"));
            TreatmentType treatmentType = treatmentTypeService.findById(treatmentTypeId);
            if (treatmentType == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Treatment type not found\"}");
            } else {
                out.printf("{\"treatmentTypeId\":%d,\"name\":\"%s\",\"baseCost\":%s}",
                        treatmentType.getTreatmentTypeId(),
                        escapeJson(treatmentType.getName()),
                        treatmentType.getBaseCost().toPlainString());
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"treatmentTypeId must be a number\"}");
        }
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
