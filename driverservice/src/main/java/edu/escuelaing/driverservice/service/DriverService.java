package edu.escuelaing.driverservice.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;
import java.util.Map;

import edu.escuelaing.driverservice.model.Driver;
import edu.escuelaing.driverservice.model.DriverStatus;

@Service
public class DriverService {

    private final Map<String, Driver> drivers = new ConcurrentHashMap<>();

    public Driver registerDriver(Driver driver) {
        // Si no tiene ID, le asignamos uno simple
        if (driver.getId() == null || driver.getId().isEmpty()) {
            driver.setId("driver-" + (drivers.size() + 1));
        }

        // Si no tiene estado, lo ponemos como AVAILABLE por defecto
        if (driver.getStatus() == null) {
            driver.setStatus(DriverStatus.AVAILABLE);
        }

        drivers.put(driver.getId(), driver);
        return driver;
    }

    public Collection<Driver> listDrivers() {
        return drivers.values();
    }

    public Driver getDriver(String id) {
        return drivers.get(id);
    }

    public Driver updateStatus(String id, DriverStatus status) {
        Driver driver = drivers.get(id);
        if (driver != null) {
            driver.setStatus(status);
        }
        return driver;
    }
}
