package edu.escuelaing.driverservice.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.escuelaing.driverservice.model.Driver;
import edu.escuelaing.driverservice.model.DriverStatus;
import edu.escuelaing.driverservice.model.Location;

public class DriverService {

    private final Map<String, Driver> drivers = new HashMap<>();

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
    public Driver updateStatus(String driverId, DriverStatus newStatus) {
        Driver driver = drivers.get(driverId);
        if (driver != null) {
            driver.setStatus(newStatus);
            drivers.put(driverId, driver);
            System.out.println("🚦 Estado del driver " + driverId + " actualizado a " + newStatus);
        } else {
            System.out.println("⚠️ No se encontró driver con ID: " + driverId);
        }
        return driver;
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
    // COMPLETAR RIDE Y SIMULAR PAGO
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

        // Aquí se puede llamar a PaymentServiceHandler si se quiere integrar
        System.out.println("💰 Pago simulado para ride " + rideId + " | Usuario: " + userId + " | Monto: " + amount);
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
