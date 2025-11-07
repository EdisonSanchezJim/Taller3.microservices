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
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<Payment> listPayments() {
        return service.listPayments();
    }

    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable String id) {
        return service.getPayment(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Payment processPayment(@RequestBody Payment payment) {
        return service.processPayment(payment);
    }

    @PutMapping("/{id}/status")
    public Payment updateStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        PaymentStatus status = PaymentStatus.valueOf(body.get("status").toUpperCase());
        return service.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePayment(@PathVariable String id) {
        service.deletePayment(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllPayments() {
        service.deleteAllPayments();
    }
}
