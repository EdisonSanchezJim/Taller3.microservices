package edu.escuelaing.driverservice.model;

public class Driver {

    private String id;
    private String name;
    private String licenseNumber;
    private DriverStatus status;
    private Vehicle vehicle;
    private Location location;

    public Driver() {}

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public DriverStatus getStatus() { return status; }
    public void setStatus(DriverStatus status) { this.status = status; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
}
