package dental.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Concrete subclass of StaffUser (Task A, Assumption A2). An Administrator
 * is a Receptionist who can additionally manage staff accounts, so its
 * permission list is the Receptionist list plus MANAGE_STAFF.
 */
public class Administrator extends StaffUser {

    public Administrator() {
        super();
    }

    public Administrator(int staffId, String username, String password, String fullName, boolean active) {
        super(staffId, username, password, fullName, active);
    }

    @Override
    public List<String> getPermissions() {
        List<String> permissions = new ArrayList<>(Arrays.asList(
                "REGISTER_APPOINTMENT",
                "VIEW_APPOINTMENT",
                "GENERATE_BILL",
                "PRINT_RECEIPT",
                "VIEW_HELP"
        ));
        permissions.add("MANAGE_STAFF_ACCOUNTS");
        return permissions;
    }

    @Override
    public String getRole() {
        return "ADMINISTRATOR";
    }
}
