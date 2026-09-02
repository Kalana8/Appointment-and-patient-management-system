# Sunrise Dental Clinic - Task B (Interactive System Implementation)

CIS6003 Advanced Programming coursework, Task B. A Java web application built
with Servlets, JSP, JDBC and MySQL, following the exact toolchain and MVC
approach taught in the module lectures (Lecture 6: Java Web Development,
Lecture 7: Servlets, Lecture 8: JSP, Lecture 9: JDBC/DAO, and the MVC
lecture). **This project is designed to be imported into Eclipse and run
there, using Apache Tomcat as the web server, exactly as Lecture 6 sets up.**

## 1. Prerequisites (matches Lecture 6's own setup steps)

1. **Eclipse IDE for Enterprise Java Developers** (Lecture 6: "Download and
   Install Eclipse ... Select Eclipse IDE for Enterprise Java Developers").
   Download from https://www.eclipse.org/downloads/packages/.
2. **Apache Tomcat 10.x** (Lecture 6: "Download and Install Apache Tomcat ...
   Unzip the folder to Eclipse workbench"). Download the Tomcat 10 core zip
   from https://tomcat.apache.org/download-10.cgi and unzip it somewhere on
   your machine (e.g. `C:\tomcat10` or `~/tomcat10`).
   - Tomcat **10.x** is required, not 9 or earlier, because this project
     uses the `jakarta.servlet.*` package (Jakarta EE namespace). Tomcat 9
     and earlier use the older `javax.servlet.*` namespace and will not run
     this code.
3. **MySQL Server 8.0** (Lecture 9). Install MySQL Community Server and make
   sure it is running on `localhost:3306`.
4. **JDK 11 or later** installed and selected as Eclipse's workspace JRE.

## 2. Set up the Apache Tomcat server in Eclipse

(Lecture 7: "Select File > Other > Servers > Apache Tomcat version ... Select
the relevant folder as the location ... Select workbench JRE version and
click on Finish".)

1. In Eclipse: **Window > Preferences > Server > Runtime Environments > Add**.
2. Choose **Apache Tomcat v10.1**, click Next.
3. Browse to the folder where you unzipped Tomcat 10, select your JRE, click
   **Finish**.

## 3. Set up the database

1. Start MySQL and open a terminal / MySQL Workbench.
2. Run the schema script to create the database, tables, trigger, function,
   stored procedure and reporting views:
   ```
   mysql -u root -p < database/schema.sql
   ```
3. (Optional but recommended) Load demo data (2 staff accounts, 7 treatment
   types, 3 patients, 3 appointments, 1 bill/receipt):
   ```
   mysql -u root -p < database/sample_data.sql
   ```
4. The scripts create an application-level database user matching the
   credentials in `src/dental/util/DatabaseConnectionManager.java`:
   - user: `dental_app`
   - password: `DentalApp#2026`
   - database: `sunrise_dental_clinic`

   If you use different MySQL credentials, update the three fields in
   `DatabaseConnectionManager`'s constructor (`jdbcUrl`, `username`,
   `password`) to match your own setup. (See section 6 below for the note
   on why this project uses the MariaDB Connector/J JDBC driver instead of
   the official MySQL Connector/J.)

## 4. Import the project into Eclipse

1. **File > Import > General > Existing Projects into Workspace**.
2. Select this folder (`SunriseDentalClinic`) as the root directory.
3. Eclipse recognises it as a **Dynamic Web Project** automatically (the
   `.project` / `.classpath` / `.settings` files are included), targeting
   the Apache Tomcat v10.1 runtime you configured in step 2.
4. If Eclipse asks you to resolve the server runtime, point it at the same
   Apache Tomcat v10.1 runtime added in section 2.
5. Right-click the project > **Properties > Targeted Runtimes** and confirm
   Apache Tomcat v10.1 is checked.

The Java source lives under `src/` (package `dental`), and the web content
(JSPs, `web.xml`, CSS) lives under `WebContent/`, exactly as Lecture 7 and
Lecture 8 describe ("Right click on webapp folder and create new HTML file
as the View ... Inside the Java Resources create a new Java class as the
Controller").

## 5. Run the application

1. Right-click the project > **Run As > Run on Server**.
2. Choose the Apache Tomcat v10.1 server you configured, click **Finish**.
3. Eclipse starts Tomcat and opens
   `http://localhost:8080/SunriseDentalClinic/` in the built-in browser
   (or open it in a normal browser).
4. You should see the Login page. Demo accounts (from `sample_data.sql`):
   - Receptionist: `receptionist1` / `recep123`
   - Administrator: `admin1` / `admin123`

This project was fully built, deployed to a local Tomcat 10 instance and
smoke-tested end-to-end (login, appointment registration with the database
trigger generating the appointment number, the live AJAX treatment-cost web
service, bill generation via the stored procedure, receipt printing, all
three reports, and administrator-only staff management) before being
delivered -- see the Task B report for the full walk-through with
screenshots.

## 6. Design notes and documented substitutions

A few implementation choices were driven by practical constraints and are
documented here (and explained in more depth, with lecture citations, in the
Task B report) rather than silently made:

- **JDBC driver**: this project uses **MariaDB Connector/J**
  (`org.mariadb.jdbc.Driver`, `WebContent/WEB-INF/lib/mariadb-java-client.jar`)
  rather than the official MySQL Connector/J. MariaDB Connector/J speaks the
  same MySQL wire protocol and connects to a MySQL 8 server with the same
  `jdbc:mysql://...` URL, `DriverManager`, `PreparedStatement` and
  `CallableStatement` API taught in Lecture 9 -- only the driver class name
  and jar differ. If you prefer the official MySQL Connector/J, download it
  from https://dev.mysql.com/downloads/connector/j/, place it in
  `WebContent/WEB-INF/lib/`, remove the MariaDB jar, and change
  `DRIVER_CLASS_NAME` in `DatabaseConnectionManager.java` to
  `com.mysql.cj.jdbc.Driver`. No other code changes are required.
- **No JSTL**: all JSPs use plain JSP scriptlets, expressions and directives
  (`<% %>`, `<%= %>`, `<%@ %>`) and JSP Expression Language (`${}`), which
  Lecture 8 covers before introducing JSTL as an alternative. This avoids a
  dependency on an extra tag-library jar and keeps every JSP runnable with
  nothing beyond a stock Tomcat 10 install. JSTL (`<c:forEach>`, `<c:if>`,
  etc., Lecture 8) is a documented drop-in enhancement: add
  `jakarta.servlet.jsp.jstl-api.jar` and `jakarta.servlet.jsp.jstl.jar` to
  `WEB-INF/lib` and the `taglib` directive from Lecture 8's own slides.
- **Printing**: "Print Receipt" opens a print-styled page and uses the
  browser's own print dialog (`window.print()`) rather than driving physical
  printer hardware, which is not reachable from a web application in this
  context.

## 7. Project structure

```
SunriseDentalClinic/
  src/dental/
    model/      Domain classes (Task A class diagram): Patient, Appointment,
                TreatmentType, Bill, Receipt, StaffUser (+ Receptionist,
                Administrator)
    dao/        Data Access Objects (Lecture 9): IUserDao, IAppointmentDao,
                Database*Dao, File*Dao, DaoFactory (Factory Method pattern)
    service/    Service layer: AuthenticationService, AppointmentService,
                BillingService, ReportService, StaffManagementService,
                TreatmentTypeService, ValidationException
    servlet/    Controllers (Lecture 7): Login/Logout, RegisterAppointment,
                ViewAppointment, GenerateBill, PrintReceipt, ManageStaff,
                Report, TreatmentCost (JSON web-service endpoint)
    util/       DatabaseConnectionManager (Singleton pattern), SessionUtil
  WebContent/
    *.jsp       Views (Lecture 8)
    common/     header.jsp / footer.jsp, included via <%@ include %>
    css/        style.css
    WEB-INF/    web.xml, lib/ (MariaDB Connector/J)
  database/
    schema.sql       Tables, trigger, function, stored procedure, views
    sample_data.sql  Demo staff, treatment types, patients, appointments
```
