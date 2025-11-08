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

    // Getters y Setters
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

    // Inicializar valores por defecto (si no se especifica driver o status)
    public void initializeDefaults() {
        if (this.status == null) {
            this.status = RideStatus.REQUESTED;
        }
        if (this.driverId == null) {
            this.driverId = null; // opcional, puede quedar null hasta asignación
        }
    }
}
