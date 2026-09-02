package dental.dao;

import dental.model.StaffUser;
import java.util.List;

/**
 * DAO Pattern interface (Task A / Lecture 9: "What is DAO?"). Separates
 * the persistence logic for StaffUser from the business logic in
 * AuthenticationService. Realised by FileUserDao and DatabaseUserDao.
 */
public interface IUserDao {
    StaffUser findByUsername(String username);
    void save(StaffUser user);
    List<StaffUser> findAll();
}
