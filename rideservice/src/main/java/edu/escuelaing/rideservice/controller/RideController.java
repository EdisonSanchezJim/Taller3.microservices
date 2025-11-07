package edu.escuelaing.rideservice.controller;

import java.util.Collection;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import edu.escuelaing.rideservice.model.Ride;
import edu.escuelaing.rideservice.model.RideStatus;
import edu.escuelaing.rideservice.service.RideService;

@RestController
@RequestMapping("/api/v1/rides")
@CrossOrigin
public class RideController {

    private final RideService service;
    private final RestTemplate restTemplate = new RestTemplate();

    public RideController(RideService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<Ride> list() {
        return service.listRides();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ride create(@RequestBody Ride ride) {
        return service.createRide(ride);
    }

    @GetMapping("/{id}")
    public Ride get(@PathVariable String id) {
        return service.getRide(id);
    }

    @PutMapping("/{id}/status")
    public Ride updateStatus(@PathVariable String id, @RequestBody Map<String,String> body) {
        return service.updateStatus(id, RideStatus.valueOf(body.get("status")));
    }

    @PutMapping("/{id}/accept")
    public Ride acceptRide(@PathVariable String id, @RequestBody Map<String,String> body) {
        String driverId = body.get("driverId");
        return service.acceptRide(id, driverId);
    }

    // 🚦 Iniciar viaje → driver pasa a BUSY
    @PutMapping("/{id}/start")
    public ResponseEntity<Ride> startRide(@PathVariable String id) {
        Ride ride = service.getRide(id);
        if (ride == null) return ResponseEntity.notFound().build();

        ride.setStatus(RideStatus.IN_PROGRESS);
        service.updateStatus(id, RideStatus.IN_PROGRESS);

        // 🚗 Llamar al DriverService para marcarlo BUSY
        if (ride.getDriverId() != null) {
            try {
                restTemplate.put(
                    "http://localhost:8081/api/v1/drivers/" + ride.getDriverId() + "/status",
                    Map.of("status", "BUSY")
                );
                System.out.println("🚗 Driver " + ride.getDriverId() + " marcado como BUSY");
            } catch (Exception e) {
                System.out.println("⚠️ Error al marcar driver BUSY: " + e.getMessage());
            }
        }

        return ResponseEntity.ok(ride);
    }

    // ✅ Completar viaje → driver pasa a AVAILABLE + se crea el pago
    @PutMapping("/{id}/complete")
    public ResponseEntity<Ride> completeRide(@PathVariable String id) {
        Ride ride = service.getRide(id);
        if (ride == null) return ResponseEntity.notFound().build();

        ride.setStatus(RideStatus.COMPLETED);
        service.updateStatus(id, RideStatus.COMPLETED);

        // 💸 Llamada al PaymentService
        try {
            restTemplate.postForObject(
                "http://localhost:8083/api/v1/payments",
                Map.of(
                    "rideId", ride.getId(),
                    "userId", ride.getRiderId(),
                    "amount", ride.getFare(),
                    "status", "COMPLETED",
                    "method", "CARD"
                ),
                Void.class
            );
            System.out.println("💰 Pago registrado correctamente para ride " + id);
        } catch (Exception e) {
            System.out.println("⚠️ Error al registrar pago: " + e.getMessage());
        }

        // ✅ Marcar al driver como AVAILABLE
        if (ride.getDriverId() != null) {
            try {
                restTemplate.put(
                    "http://localhost:8081/api/v1/drivers/" + ride.getDriverId() + "/status",
                    Map.of("status", "AVAILABLE")
                );
                System.out.println("🚙 Driver " + ride.getDriverId() + " marcado como AVAILABLE");
            } catch (Exception e) {
                System.out.println("⚠️ Error al marcar driver AVAILABLE: " + e.getMessage());
            }
        }

        return ResponseEntity.ok(ride);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRide(@PathVariable String id) {
        service.deleteRide(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllRides() {
        service.deleteAllRides();
    }
}
