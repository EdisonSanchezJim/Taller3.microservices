package edu.escuelaing.driverservice.controller;

import java.util.Collection;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // ✅ Registrar nuevo driver
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Driver register(@RequestBody Driver driver) {
        return service.registerDriver(driver);
    }

    // ✅ Listar todos los drivers
    @GetMapping
    public Collection<Driver> list() {
        return service.listDrivers();
    }

    // ✅ Obtener driver por ID
    @GetMapping("/{id}")
    public Driver get(@PathVariable String id) {
        return service.getDriver(id);
    }

    // ✅ Actualizar estado (AVAILABLE / BUSY)
    @PutMapping("/{driverId}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable String driverId, @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        service.updateStatus(driverId, DriverStatus.valueOf(newStatus));
        return ResponseEntity.ok().build();
    }

    // ✅ Eliminar un driver por ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDriver(@PathVariable String id) {
        service.deleteDriver(id);
    }

    // ✅ Eliminar todos los drivers
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllDrivers() {
        service.deleteAllDrivers();
    }
}
