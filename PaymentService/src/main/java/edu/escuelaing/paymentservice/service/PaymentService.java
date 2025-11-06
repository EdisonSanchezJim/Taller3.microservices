package edu.escuelaing.paymentservice.service;

import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import edu.escuelaing.paymentservice.model.Payment;
import edu.escuelaing.paymentservice.model.PaymentStatus;

@Service
public class PaymentService {

    private final Map<String, Payment> payments = new ConcurrentHashMap<>();

    public Payment processPayment(Payment payment) {
        if (payment.getId() == null || payment.getId().isEmpty()) {
            payment.setId(UUID.randomUUID().toString());
        }
        payment.setStatus(PaymentStatus.COMPLETED);
        payments.put(payment.getId(), payment);
        return payment;
    }

    public Collection<Payment> listPayments() {
        return payments.values();
    }

    public Payment getPayment(String id) {
        return payments.get(id);
    }

    public Payment updateStatus(String id, PaymentStatus status) {
        Payment payment = payments.get(id);
        if (payment != null) {
            payment.setStatus(status);
        }
        return payment;
    }
}
