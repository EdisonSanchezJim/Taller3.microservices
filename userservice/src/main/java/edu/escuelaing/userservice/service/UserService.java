package edu.escuelaing.userservice.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.escuelaing.userservice.model.User;
import edu.escuelaing.userservice.model.UserStatus;


public class UserService {

    private final Map<String, User> users = new HashMap<>();

    // Registrar usuario
    public User registerUser(User user) {
        String id = "user-" + System.currentTimeMillis();
        user.setId(id);
        if (user.getStatus() == null) {
            user.setStatus(UserStatus.ACTIVE);
        }
        users.put(id, user);
        return user;
    }

    // Listar usuarios
    public Collection<User> listUsers() {
        return users.values();
    }

    // Obtener usuario por ID
    public User getUser(String id) {
        return users.get(id);
    }

    // Actualizar estado
    public User updateStatus(String id, UserStatus status) {
        User user = users.get(id);
        if (user != null) {
            user.setStatus(status);
        }
        return user;
    }

    public void deleteUser(String id) {
        users.remove(id);
    }
    
    public void deleteAllUsers() {
        users.clear();
    }

    
    

}
