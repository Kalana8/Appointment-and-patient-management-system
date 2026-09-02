package dental.model;

import java.util.Arrays;
import java.util.List;

/**
 * Concrete subclass of StaffUser (Task A, Assumption A1). Handles the
 * day-to-day appointment, patient and billing operations.
 */
public class Receptionist extends StaffUser {

    public Receptionist() {
        super();
    }

    public Receptionist(int staffId, String username, String password, String fullName, boolean active) {
        super(staffId, username, password, fullName, active);
    }

    @Override
    public List<String> getPermissions() {
        return Arrays.asList(
                "REGISTER_APPOINTMENT",
                "VIEW_APPOINTMENT",
                "GENERATE_BILL",
                "PRINT_RECEIPT",
                "VIEW_HELP"
        );
    }

    @Override
    public String getRole() {
        return "RECEPTIONIST";
    }
}
