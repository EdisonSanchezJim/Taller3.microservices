package edu.escuelaing.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.escuelaing.paymentservice.model.Payment;
import edu.escuelaing.paymentservice.model.PaymentStatus;
import edu.escuelaing.paymentservice.service.PaymentService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PaymentServiceHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final PaymentService service = new PaymentService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Método HTTP
            String httpMethod = (String) input.get("httpMethod");

            // Parámetros query
            Map<String, String> queryParams = (Map<String, String>) input.get("queryStringParameters");
            if (queryParams == null) queryParams = new HashMap<>();

            // Body
            String body = (String) input.get("body");
            Map<String, Object> bodyMap = body != null ? mapper.readValue(body, Map.class) : new HashMap<>();

            switch (httpMethod.toUpperCase()) {

                case "GET":
                    String getAction = queryParams.getOrDefault("action", "").toLowerCase();
                    if ("list".equals(getAction)) {
                        Collection<Payment> payments = service.listPayments();
                        response.put("statusCode", 200);
                        response.put("body", mapper.writeValueAsString(payments));
                    } else if ("get".equals(getAction)) {
                        String id = queryParams.get("id");
                        if (id == null) {
                            response.put("statusCode", 400);
                            response.put("body", "{\"error\":\"id no proporcionada\"}");
                        } else {
                            Payment payment = service.getPayment(id);
                            if (payment != null) {
                                response.put("statusCode", 200);
                                response.put("body", mapper.writeValueAsString(payment));
                            } else {
                                response.put("statusCode", 404);
                                response.put("body", "{\"error\":\"Payment no encontrado\"}");
                            }
                        }
                    } else {
                        response.put("statusCode", 400);
                        response.put("body", "{\"error\":\"acción GET no soportada\"}");
                    }
                    break;

                case "POST":
                    if (!bodyMap.containsKey("payment")) {
                        response.put("statusCode", 400);
                        response.put("body", "{\"error\":\"payment no proporcionado\"}");
                    } else {
                        Payment newPayment = mapper.convertValue(bodyMap.get("payment"), Payment.class);
                        if (newPayment.getRideId() == null || newPayment.getAmount() <= 0) {
                            response.put("statusCode", 400);
                            response.put("body", "{\"error\":\"rideId inválido o amount <= 0\"}");
                        } else {
                            Payment createdPayment = service.processPayment(newPayment);
                            response.put("statusCode", 201);
                            response.put("body", mapper.writeValueAsString(createdPayment));
                        }
                    }
                    break;

                case "PUT":
                    if (!bodyMap.containsKey("id") || !bodyMap.containsKey("status")) {
                        response.put("statusCode", 400);
                        response.put("body", "{\"error\":\"id o status faltante\"}");
                    } else {
                        String updateId = (String) bodyMap.get("id");
                        PaymentStatus status = PaymentStatus.valueOf(((String) bodyMap.get("status")).toUpperCase());
                        Payment updatedPayment = service.updateStatus(updateId, status);
                        if (updatedPayment != null) {
                            response.put("statusCode", 200);
                            response.put("body", mapper.writeValueAsString(updatedPayment));
                        } else {
                            response.put("statusCode", 404);
                            response.put("body", "{\"error\":\"Payment no encontrado\"}");
                        }
                    }
                    break;

                case "DELETE":
                    if (!bodyMap.containsKey("id")) {
                        service.deleteAllPayments();
                        response.put("statusCode", 200);
                        response.put("body", "{\"message\":\"Todos los payments eliminados\"}");
                    } else {
                        String deleteId = (String) bodyMap.get("id");
                        service.deletePayment(deleteId);
                        response.put("statusCode", 200);
                        response.put("body", "{\"message\":\"Payment eliminado con ID: " + deleteId + "\"}");
                    }
                    break;

                default:
                    response.put("statusCode", 405);
                    response.put("body", "{\"error\":\"Método HTTP no soportado\"}");
            }

        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
            response.put("statusCode", 500);
            response.put("body", "{\"error\":\"" + e.getMessage() + "\"}");
        }

        return response;
    }
}
