package edu.escuelaing.rideservice.model;

public class Ride {
    private String id;
    private String riderId;
    private String driverId;
    private double fare;
    private RideStatus status;

    public Ride() {}

    public Ride(String id, String riderId, String driverId, double fare, RideStatus status) {
        this.id = id;
        this.riderId = riderId;
        this.driverId = driverId;
        this.fare = fare;
        this.status = status;
    }

    // ✅ Getters y setters necesarios
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRiderId() { return riderId; }
    public void setRiderId(String riderId) { this.riderId = riderId; }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public double getFare() { return fare; }
    public void setFare(double fare) { this.fare = fare; }

    public RideStatus getStatus() { return status; }
    public void setStatus(RideStatus status) { this.status = status; }
}
