package edu.escuelaing.rideservice.service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import edu.escuelaing.rideservice.model.Ride;
import edu.escuelaing.rideservice.model.RideStatus;

@Service
public class RideService {

    private final Map<String, Ride> rides = new ConcurrentHashMap<>();

    @Autowired
    private RabbitTemplate amqp;

    public Ride createRide(Ride ride) {
        String id = UUID.randomUUID().toString();
        ride.setRideId(id);
        rides.put(id, ride);


        amqp.convertAndSend(MessagingConfig.RIDE_REQUESTED_QUEUE, ride);

        return ride;
    }


    public Ride getRide(String id) {
        return Optional.ofNullable(rides.get(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found"));
    }


    public Collection<Ride> listRides() {
        return rides.values();
    }

    public Ride updateStatus(String id, RideStatus status) {
        Ride ride = getRide(id);
        ride.setStatus(status);
        return ride;
    }
}
