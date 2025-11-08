package edu.escuelaing.paymentservice.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.escuelaing.paymentservice.model.Payment;
import edu.escuelaing.paymentservice.model.PaymentStatus;

public class PaymentService {

    private final Map<String, Payment> payments = new HashMap<>();

    // --------------------------------------------------------------------
    // CREAR / PROCESAR PAGO
    // --------------------------------------------------------------------
    public Payment processPayment(Payment payment) {
        String id = "pay-" + System.currentTimeMillis();
        payment.setId(id);

        if (payment.getStatus() == null) {
            payment.setStatus(PaymentStatus.PENDING);
        }

        payments.put(id, payment);
        System.out.println("💰 Pago procesado: " + payment.getId() + " | Estado: " + payment.getStatus());
        return payment;
    }

    // --------------------------------------------------------------------
    // LISTAR PAGOS
    // --------------------------------------------------------------------
    public Collection<Payment> listPayments() {
        System.out.println("📋 Consultando todos los pagos registrados...");
        return payments.values();
    }

    // --------------------------------------------------------------------
    // OBTENER PAGO POR ID
    // --------------------------------------------------------------------
    public Payment getPayment(String id) {
        Payment payment = payments.get(id);
        if (payment == null) {
            System.out.println("⚠️ No se encontró el pago con ID: " + id);
        } else {
            System.out.println("🔍 Pago encontrado: " + id + " | Estado: " + payment.getStatus());
        }
        return payment;
    }

    // --------------------------------------------------------------------
    // ACTUALIZAR ESTADO DEL PAGO
    // --------------------------------------------------------------------
    public Payment updateStatus(String id, PaymentStatus status) {
        Payment payment = payments.get(id);
        if (payment != null) {
            payment.setStatus(status);
            System.out.println("✅ Estado del pago " + id + " actualizado a: " + status);
        } else {
            System.out.println("⚠️ No se encontró el pago para actualizar: " + id);
        }
        return payment;
    }

    // --------------------------------------------------------------------
    // ELIMINAR PAGO POR ID
    // --------------------------------------------------------------------
    public void deletePayment(String id) {
        if (payments.remove(id) != null) {
            System.out.println("🗑️ Pago eliminado: " + id);
        } else {
            System.out.println("⚠️ No se encontró el pago con ID: " + id);
        }
    }

    // --------------------------------------------------------------------
    // ELIMINAR TODOS LOS PAGOS
    // --------------------------------------------------------------------
    public void deleteAllPayments() {
        payments.clear();
        System.out.println("🧹 Todos los pagos eliminados");
    }
}
