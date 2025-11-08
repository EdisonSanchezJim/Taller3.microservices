package edu.escuelaing.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.escuelaing.rideservice.model.Ride;
import edu.escuelaing.rideservice.model.RideStatus;
import edu.escuelaing.rideservice.service.RideService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RideServiceHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final RideService service = new RideService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
        Map<String, Object> response = new HashMap<>();
        response.put("headers", Map.of("Content-Type", "application/json"));

        try {
            String httpMethod = (String) event.get("httpMethod");
            Map<String, String> queryParams = (Map<String, String>) event.get("queryStringParameters");
            String body = (String) event.get("body");
            Map<String, Object> bodyMap = body != null ? mapper.readValue(body, Map.class) : null;

            switch (httpMethod.toUpperCase()) {

                case "GET":
                    if (queryParams == null || queryParams.get("action") == null) {
                        response.put("statusCode", 400);
                        response.put("body", "{\"error\":\"action no especificada en GET\"}");
                        break;
                    }

                    String getAction = queryParams.get("action").toLowerCase();
                    if ("list".equals(getAction)) {
                        Collection<Ride> rides = service.listRides();
                        response.put("statusCode", 200);
                        response.put("body", mapper.writeValueAsString(rides));
                    } else if ("get".equals(getAction)) {
                        String id = queryParams.get("id");
                        if (id == null) {
                            response.put("statusCode", 400);
                            response.put("body", "{\"error\":\"id no proporcionada para get\"}");
                        } else {
                            Ride ride = service.getRide(id);
                            response.put("statusCode", ride != null ? 200 : 404);
                            response.put("body", ride != null
                                    ? mapper.writeValueAsString(ride)
                                    : "{\"error\":\"Ride no encontrado con ID: " + id + "\"}");
                        }
                    } else {
                        response.put("statusCode", 400);
                        response.put("body", "{\"error\":\"acción GET no soportada: " + getAction + "\"}");
                    }
                    break;

                case "POST":
                    if (bodyMap == null || bodyMap.get("ride") == null) {
                        response.put("statusCode", 400);
                        response.put("body", "{\"error\":\"ride no proporcionado en POST\"}");
                        break;
                    }
                    Ride rideToCreate = mapper.convertValue(bodyMap.get("ride"), Ride.class);
                    rideToCreate.initializeDefaults();
                    Ride createdRide = service.createRide(rideToCreate);
                    response.put("statusCode", 201);
                    response.put("body", mapper.writeValueAsString(createdRide));
                    break;

                case "PUT":
                    if (bodyMap == null || bodyMap.get("id") == null || bodyMap.get("status") == null) {
                        response.put("statusCode", 400);
                        response.put("body", "{\"error\":\"id o status faltante en PUT\"}");
                        break;
                    }
                    String updateId = (String) bodyMap.get("id");
                    RideStatus status = RideStatus.valueOf(((String) bodyMap.get("status")).toUpperCase());
                    Ride updatedRide = service.updateStatus(updateId, status);
                    response.put("statusCode", updatedRide != null ? 200 : 404);
                    response.put("body", updatedRide != null
                            ? mapper.writeValueAsString(updatedRide)
                            : "{\"error\":\"Ride no encontrado con ID: " + updateId + "\"}");
                    break;

                case "DELETE":
                    if (bodyMap != null && bodyMap.get("id") != null) {
                        String deleteId = (String) bodyMap.get("id");
                        service.deleteRide(deleteId);
                        response.put("statusCode", 200);
                        response.put("body", "{\"message\":\"Ride eliminado con ID: " + deleteId + "\"}");
                    } else {
                        service.deleteAllRides();
                        response.put("statusCode", 200);
                        response.put("body", "{\"message\":\"Todos los rides eliminados\"}");
                    }
                    break;

                default:
                    response.put("statusCode", 405);
                    response.put("body", "{\"error\":\"Método HTTP no soportado: " + httpMethod + "\"}");
            }

        } catch (Exception e) {
            response.put("statusCode", 500);
            response.put("body", "{\"error\":\"" + e.getMessage() + "\"}");
        }

        return response;
    }
}
