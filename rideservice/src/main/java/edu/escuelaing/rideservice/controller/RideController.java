package edu.escuelaing.rideservice.controller;

import java.util.Collection;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import edu.escuelaing.rideservice.model.Ride;
import edu.escuelaing.rideservice.model.RideStatus;
import edu.escuelaing.rideservice.service.RideService;

@RestController
@RequestMapping("/api/v1/rides")
@CrossOrigin
public class RideController {

    private final RideService service;

    public RideController(RideService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<Ride> list() {
        return service.listRides();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ride create(@RequestBody Ride ride) {
        return service.createRide(ride);
    }

    @GetMapping("/{id}")
    public Ride get(@PathVariable String id) {
        return service.getRide(id);
    }

    @PutMapping("/{id}/status")
    public Ride updateStatus(@PathVariable String id, @RequestBody Map<String,String> body) {
        return service.updateStatus(id, RideStatus.valueOf(body.get("status")));
    }
}
