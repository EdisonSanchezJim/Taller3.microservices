package edu.escuelaing.userservice.controller;

import java.util.Collection;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import edu.escuelaing.userservice.model.User;
import edu.escuelaing.userservice.model.UserStatus;
import edu.escuelaing.userservice.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestBody User user) {
        return service.registerUser(user);
    }

    @GetMapping
    public Collection<User> list() {
        return service.listUsers();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable String id) {
        return service.getUser(id);
    }

    @PutMapping("/{id}/status")
    public User updateStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        return service.updateStatus(id, UserStatus.valueOf(body.get("status")));
    }
}
