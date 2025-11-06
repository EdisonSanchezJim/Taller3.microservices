package edu.escuelaing.paymentservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;
import java.util.Map;

import edu.escuelaing.paymentservice.model.Payment;
import edu.escuelaing.paymentservice.model.PaymentStatus;
import edu.escuelaing.paymentservice.service.PaymentService;

@RestController
@RequestMapping("/api/v1/payments")
@CrossOrigin
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Payment process(@RequestBody Payment payment) {
        return service.processPayment(payment);
    }

    @GetMapping
    public Collection<Payment> list() {
        return service.listPayments();
    }

    @GetMapping("/{id}")
    public Payment get(@PathVariable String id) {
        return service.getPayment(id);
    }

    @PutMapping("/{id}/status")
    public Payment updateStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        return service.updateStatus(id, PaymentStatus.valueOf(body.get("status")));
    }
}
