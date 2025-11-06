package edu.escuelaing.driverservice.controller;

import java.util.Collection;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import edu.escuelaing.driverservice.model.Driver;
import edu.escuelaing.driverservice.model.DriverStatus;
import edu.escuelaing.driverservice.service.DriverService;

@RestController
@RequestMapping("/api/v1/drivers")
@CrossOrigin
public class DriverController {

    private final DriverService service;

    public DriverController(DriverService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Driver register(@RequestBody Driver driver) {
        return service.registerDriver(driver);
    }

    @GetMapping
    public Collection<Driver> list() {
        return service.listDrivers();
    }

    @GetMapping("/{id}")
    public Driver get(@PathVariable String id) {
        return service.getDriver(id);
    }

    @PutMapping("/{id}/status")
    public Driver updateStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        return service.updateStatus(id, DriverStatus.valueOf(body.get("status")));
    }
}
