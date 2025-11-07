package edu.escuelaing.rideservice.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import edu.escuelaing.rideservice.model.Ride;
import edu.escuelaing.rideservice.model.RideStatus;

@Service
public class RideService {

    private final Map<String, Ride> rides = new ConcurrentHashMap<>();
    private final RestTemplate restTemplate = new RestTemplate();

    // URLs de otros microservicios
    private final String DRIVER_SERVICE_URL = "http://localhost:8081/api/v1/drivers";
    private final String PAYMENT_SERVICE_URL = "http://localhost:8083/api/v1/payments";

    // --------------------------------------------------------------------
    // CRUD BÁSICO
    // --------------------------------------------------------------------
    public Collection<Ride> listRides() {
        return rides.values();
    }

    public Ride getRide(String id) {
        return rides.get(id);
    }

    public Ride createRide(Ride ride) {
        String id = "ride-" + System.currentTimeMillis();
        ride.setId(id);
        ride.setStatus(RideStatus.REQUESTED);
        rides.put(id, ride);
        System.out.println("✅ Nuevo ride creado: " + id);
        return ride;
    }

    public Ride updateStatus(String id, RideStatus status) {
        Ride ride = rides.get(id);
        if (ride != null) {
            ride.setStatus(status);
            rides.put(id, ride);
            System.out.println("🔄 Ride " + id + " actualizado a estado " + status);
        } else {
            System.out.println("⚠️ No se encontró ride con ID: " + id);
        }
        return ride;
    }

    // --------------------------------------------------------------------
    // ASIGNAR DRIVER
    // --------------------------------------------------------------------
    public Ride acceptRide(String rideId, String driverId) {
        Ride ride = rides.get(rideId);
        if (ride == null) {
            System.out.println("❌ No se encontró el ride con ID: " + rideId);
            return null;
        }

        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ACCEPTED);
        rides.put(rideId, ride);
        System.out.println("🚗 Driver " + driverId + " asignado al ride " + rideId);

        // Marcar driver como BUSY
        try {
            Map<String, String> payload = Map.of("status", "BUSY");
            restTemplate.put(DRIVER_SERVICE_URL + "/" + driverId + "/status", payload);
            System.out.println("✅ Driver " + driverId + " actualizado a BUSY");
        } catch (Exception e) {
            System.out.println("⚠️ Error al actualizar estado del driver: " + e.getMessage());
        }

        return ride;
    }

    // --------------------------------------------------------------------
    // COMPLETAR VIAJE
    // --------------------------------------------------------------------
    public Ride completeRide(String rideId) {
        Ride ride = updateStatus(rideId, RideStatus.COMPLETED);
        if (ride == null) return null;

        System.out.println("✅ Ride " + rideId + " completado. Generando pago...");

        // Crear pago
        Map<String, Object> paymentPayload = Map.of(
            "rideId", ride.getId(),
            "userId", ride.getRiderId(),
            "amount", ride.getFare(),
            "status", "COMPLETED",
            "method", "CARD"
        );

        try {
            ResponseEntity<Object> response =
                restTemplate.postForEntity(PAYMENT_SERVICE_URL, paymentPayload, Object.class);
            System.out.println("💰 Pago generado correctamente: " + response.getStatusCode());
        } catch (Exception e) {
            System.out.println("⚠️ Error al crear el pago: " + e.getMessage());
        }

        // Marcar driver como AVAILABLE
        if (ride.getDriverId() != null) {
            try {
                restTemplate.put(DRIVER_SERVICE_URL + "/" + ride.getDriverId() + "/status",
                        Map.of("status", "AVAILABLE"));
                System.out.println("🚙 Driver " + ride.getDriverId() + " actualizado a AVAILABLE");
            } catch (Exception e) {
                System.out.println("⚠️ Error al actualizar driver a AVAILABLE: " + e.getMessage());
            }
        }

        return ride;
    }

    // --------------------------------------------------------------------
    // ELIMINAR
    // --------------------------------------------------------------------
    public void deleteRide(String id) {
        rides.remove(id);
        System.out.println("🗑️ Ride eliminado: " + id);
    }

    public void deleteAllRides() {
        rides.clear();
        System.out.println("🧹 Todos los rides eliminados");
    }
}
