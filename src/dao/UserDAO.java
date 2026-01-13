package dao;

import model.User;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Zaenkrat simulacija baze z list
    private List<User> users = new ArrayList<>();

    public UserDAO() {
        // dummy user
        users.add(new User("admin", "1234"));
    }

    public User getUserByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
}
