package edu.escuelaing.driverservice.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import edu.escuelaing.driverservice.model.Driver;
import edu.escuelaing.driverservice.model.DriverStatus;
import edu.escuelaing.driverservice.model.Location;

@Service
public class DriverService {

    private final Map<String, Driver> drivers = new HashMap<>();
    private final RestTemplate restTemplate = new RestTemplate();

    // URL base del PaymentService
    private static final String PAYMENT_SERVICE_URL = "http://localhost:8083/api/v1/payments";

    // --------------------------------------------------------------------
    // REGISTRO Y LISTADO
    // --------------------------------------------------------------------
    public Driver registerDriver(Driver driver) {
        String id = "driver-" + System.currentTimeMillis();
        driver.setId(id);
        if (driver.getStatus() == null) {
            driver.setStatus(DriverStatus.AVAILABLE);
        }
        drivers.put(id, driver);
        System.out.println("✅ Nuevo driver registrado: " + driver.getName() + " (" + id + ")");
        return driver;
    }

    public Collection<Driver> listDrivers() {
        return drivers.values();
    }

    public Driver getDriver(String id) {
        return drivers.get(id);
    }

    // --------------------------------------------------------------------
    // ACTUALIZAR ESTADO
    // --------------------------------------------------------------------
    public void updateStatus(String driverId, DriverStatus newStatus) {
        Driver driver = drivers.get(driverId);
        if (driver != null) {
            driver.setStatus(newStatus);
            drivers.put(driverId, driver);
            System.out.println("🚦 Estado del driver " + driverId + " actualizado a " + newStatus);
        } else {
            System.out.println("⚠️ No se encontró driver con ID: " + driverId);
        }
    }

    // --------------------------------------------------------------------
    // ACTUALIZAR UBICACIÓN
    // --------------------------------------------------------------------
    public Driver updateLocation(String id, Location location) {
        Driver driver = drivers.get(id);
        if (driver != null) {
            driver.setLocation(location);
            System.out.println("📍 Ubicación del driver " + id + " actualizada a " + location);
        } else {
            System.out.println("⚠️ No se encontró driver con ID: " + id);
        }
        return driver;
    }

    // --------------------------------------------------------------------
    // COMPLETAR RIDE Y GENERAR PAGO
    // --------------------------------------------------------------------
    public void completeRide(String rideId, double amount, String userId, String driverId) {
        Driver driver = drivers.get(driverId);
        if (driver == null) {
            System.out.println("❌ No se encontró driver con ID: " + driverId);
            return;
        }

        // Marcar al conductor como disponible nuevamente
        driver.setStatus(DriverStatus.AVAILABLE);
        drivers.put(driverId, driver);
        System.out.println("✅ Driver " + driverId + " completó ride " + rideId + ". Estado: AVAILABLE");

        // Crear el pago
        Map<String, Object> paymentPayload = Map.of(
            "rideId", rideId,
            "userId", userId,
            "driverId", driverId,
            "amount", amount,
            "method", "CARD",
            "status", "COMPLETED"
        );

        try {
            ResponseEntity<Object> response = restTemplate.postForEntity(PAYMENT_SERVICE_URL, paymentPayload, Object.class);
            System.out.println("💰 Pago registrado exitosamente en PaymentService. Estado HTTP: " + response.getStatusCode());
        } catch (Exception e) {
            System.out.println("⚠️ Error al registrar el pago en PaymentService: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------
    // ELIMINAR
    // --------------------------------------------------------------------
    public void deleteDriver(String id) {
        drivers.remove(id);
        System.out.println("🗑️ Driver eliminado: " + id);
    }

    public void deleteAllDrivers() {
        drivers.clear();
        System.out.println("🧹 Todos los drivers eliminados");
    }
}
