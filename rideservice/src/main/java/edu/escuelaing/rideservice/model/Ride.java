package edu.escuelaing.rideservice.model;

public class Ride {

    private String rideId;
    private String passengerId;
    private String driverId;
    private Location origin;
    private Location destination;
    private RideStatus status;

    public Ride() {}

    // ✅ Getters y Setters correctos
    public String getRideId() { return rideId; }
    public void setRideId(String rideId) { this.rideId = rideId; }

    public String getPassengerId() { return passengerId; }
    public void setPassengerId(String passengerId) { this.passengerId = passengerId; }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public Location getOrigin() { return origin; }
    public void setOrigin(Location origin) { this.origin = origin; }

    public Location getDestination() { return destination; }
    public void setDestination(Location destination) { this.destination = destination; }

    public RideStatus getStatus() { return status; }
    public void setStatus(RideStatus status) { this.status = status; }
}
