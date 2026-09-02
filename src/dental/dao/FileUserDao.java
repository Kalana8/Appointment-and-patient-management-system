package dental.dao;

import dental.model.Administrator;
import dental.model.Receptionist;
import dental.model.StaffUser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Text-file-backed implementation of IUserDao (DAO Pattern). Demonstrates
 * the "appropriate data structures and text files to store information"
 * option allowed by the assignment brief, and shows that DaoFactory can
 * hand back a completely different storage mechanism without any change
 * to AuthenticationService, which depends only on the IUserDao interface.
 * One pipe-delimited line per staff member:
 * staffId|username|password|fullName|role|active
 */
public class FileUserDao implements IUserDao {

    private final String filePath;

    public FileUserDao(String filePath) {
        this.filePath = filePath;
        ensureFileExists();
    }

    private void ensureFileExists() {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Could not create staff data file: " + filePath, e);
            }
        }
    }

    @Override
    public StaffUser findByUsername(String username) {
        for (StaffUser user : findAll()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void save(StaffUser user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(String.join("|",
                    String.valueOf(user.getStaffId()),
                    user.getUsername(),
                    user.getPassword(),
                    user.getFullName(),
                    user.getRole(),
                    String.valueOf(user.isActive())));
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error writing staff record to file", e);
        }
    }

    @Override
    public List<StaffUser> findAll() {
        List<StaffUser> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split("\\|");
                int staffId = Integer.parseInt(parts[0]);
                String username = parts[1];
                String password = parts[2];
                String fullName = parts[3];
                String role = parts[4];
                boolean active = Boolean.parseBoolean(parts[5]);
                if ("ADMINISTRATOR".equals(role)) {
                    users.add(new Administrator(staffId, username, password, fullName, active));
                } else {
                    users.add(new Receptionist(staffId, username, password, fullName, active));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading staff data file", e);
        }
        return users;
    }
}
