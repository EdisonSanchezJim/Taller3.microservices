package edu.escuelaing.driverservice.model;

public class Driver {
    private String id;
    private String name;
    private DriverStatus status;

    public Driver() {}

    public Driver(String id, String name, DriverStatus status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public DriverStatus getStatus() { return status; }
    public void setStatus(DriverStatus status) { this.status = status; }
}
