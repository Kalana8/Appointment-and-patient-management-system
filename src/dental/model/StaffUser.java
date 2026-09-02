package dental.model;

import java.util.List;

/**
 * Abstract superclass of the Actor Hierarchy (see Task A, Figure 2).
 * Declared abstract with an abstract getPermissions() operation so that
 * only a concrete Receptionist or Administrator can ever be instantiated
 * (Abstraction, Lecture 2).
 */
public abstract class StaffUser {

    protected int staffId;
    protected String username;
    protected String password;
    protected String fullName;
    protected boolean active;

    public StaffUser() {
    }

    public StaffUser(int staffId, String username, String password, String fullName, boolean active) {
        this.staffId = staffId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.active = active;
    }

    /** Polymorphic operation: each subclass returns a different permission list (Lecture 2, Polymorphism). */
    public abstract List<String> getPermissions();

    /** Discriminator string persisted in the staff.role column ('RECEPTIONIST' / 'ADMINISTRATOR'). */
    public abstract String getRole();

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
