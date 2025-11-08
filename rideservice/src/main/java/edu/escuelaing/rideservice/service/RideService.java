package edu.escuelaing.rideservice.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.escuelaing.rideservice.model.Ride;
import edu.escuelaing.rideservice.model.RideStatus;

public class RideService {

    private final Map<String, Ride> rides = new ConcurrentHashMap<>();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // URLs de otros microservicios
    private final String DRIVER_SERVICE_URL = "http://localhost:8081/api/v1/drivers";
    private final String PAYMENT_SERVICE_URL = "http://localhost:8083/api/v1/payments";

    // ---------------- CRUD ----------------
    public Collection<Ride> listRides() { return rides.values(); }

    public Ride getRide(String id) { return rides.get(id); }

    public Ride createRide(Ride ride) {
        String id = "ride-" + System.currentTimeMillis();
        ride.setId(id);
        ride.setStatus(RideStatus.REQUESTED);
        rides.put(id, ride);
        return ride;
    }

    public Ride updateStatus(String id, RideStatus status) {
        Ride ride = rides.get(id);
        if (ride != null) ride.setStatus(status);
        return ride;
    }

    public void deleteRide(String id) { rides.remove(id); }

    public void deleteAllRides() { rides.clear(); }

    // ---------------- ASIGNAR DRIVER ----------------
    public Ride acceptRide(String rideId, String driverId) {
        Ride ride = rides.get(rideId);
        if (ride == null) return null;

        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ACCEPTED);
        rides.put(rideId, ride);

        // Marcar driver como BUSY
        try {
            sendPutRequest(DRIVER_SERVICE_URL + "/" + driverId + "/status",
                    Map.of("status", "BUSY"));
        } catch (Exception e) {
            System.out.println("⚠️ Error al marcar driver BUSY: " + e.getMessage());
        }

        return ride;
    }

    // ---------------- INICIAR VIAJE ----------------
    public Ride startRide(String rideId) {
        Ride ride = rides.get(rideId);
        if (ride == null) return null;

        ride.setStatus(RideStatus.IN_PROGRESS);

        if (ride.getDriverId() != null) {
            try {
                sendPutRequest(DRIVER_SERVICE_URL + "/" + ride.getDriverId() + "/status",
                        Map.of("status", "BUSY"));
            } catch (Exception e) {
                System.out.println("⚠️ Error al marcar driver BUSY: " + e.getMessage());
            }
        }

        return ride;
    }

    // ---------------- COMPLETAR VIAJE ----------------
    public Ride completeRide(String rideId) {
        Ride ride = rides.get(rideId);
        if (ride == null) return null;

        ride.setStatus(RideStatus.COMPLETED);

        // Crear pago
        try {
            Map<String, Object> paymentPayload = Map.of(
                    "rideId", ride.getId(),
                    "userId", ride.getRiderId(),
                    "amount", ride.getFare(),
                    "status", "COMPLETED",
                    "method", "CARD"
            );
            sendPostRequest(PAYMENT_SERVICE_URL, paymentPayload);
        } catch (Exception e) {
            System.out.println("⚠️ Error al generar pago: " + e.getMessage());
        }

        // Marcar driver como AVAILABLE
        if (ride.getDriverId() != null) {
            try {
                sendPutRequest(DRIVER_SERVICE_URL + "/" + ride.getDriverId() + "/status",
                        Map.of("status", "AVAILABLE"));
            } catch (Exception e) {
                System.out.println("⚠️ Error al marcar driver AVAILABLE: " + e.getMessage());
            }
        }

        return ride;
    }

    // ---------------- MÉTODOS HTTP ----------------
    private void sendPutRequest(String url, Map<String, ?> payload) throws Exception {
        String json = mapper.writeValueAsString(payload);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            System.out.println("⚠️ PUT request failed: " + response.body());
        }
    }

    private void sendPostRequest(String url, Map<String, ?> payload) throws Exception {
        String json = mapper.writeValueAsString(payload);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            System.out.println("⚠️ POST request failed: " + response.body());
        }
    }

    public Ride updateRide(String id, Ride rideUpdates) {
        Ride ride = rides.get(id);
        if (ride != null) {
            // Actualizar solo los campos que tengan valor
            if (rideUpdates.getDriverId() != null) ride.setDriverId(rideUpdates.getDriverId());
            if (rideUpdates.getFare() != 0) ride.setFare(rideUpdates.getFare());
            if (rideUpdates.getStatus() != null) ride.setStatus(rideUpdates.getStatus());
            System.out.println("🔄 Ride " + id + " actualizado");
        } else {
            System.out.println("⚠️ No se encontró ride con ID: " + id);
        }
        return ride;
    }
}
