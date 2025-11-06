package edu.escuelaing.rideservice.model;

import java.io.Serializable;

public class Location implements Serializable {
    private double latitude;
    private double longitude;

    // Constructor vacío (necesario para Jackson)
    public Location() {}

    // Constructor con parámetros (opcional)
    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters y Setters (Jackson los usa para serializar)
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
