package edu.escuelaing.userservice.service;

import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import edu.escuelaing.userservice.model.User;
import edu.escuelaing.userservice.model.UserStatus;

@Service
public class UserService {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    public User registerUser(User user) {
        String id = UUID.randomUUID().toString();
        user.setId(id);
        if (user.getStatus() == null) {
            user.setStatus(UserStatus.ACTIVE);
        }
        users.put(id, user);
        return user;
    }

    public Collection<User> listUsers() {
        return users.values();
    }

    public User getUser(String id) {
        return users.get(id);
    }

    public User updateStatus(String id, UserStatus status) {
        User user = users.get(id);
        if (user != null) {
            user.setStatus(status);
        }
        return user;
    }
}
