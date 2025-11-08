package edu.escuelaing.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.escuelaing.driverservice.model.Driver;
import edu.escuelaing.driverservice.model.DriverStatus;
import edu.escuelaing.driverservice.service.DriverService;

import java.util.Collection;
import java.util.Map;

public class DriverServiceHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final DriverService service = new DriverService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        String httpMethod = (String) input.get("httpMethod");
        Map<String, String> queryParams = (Map<String, String>) input.get("queryStringParameters");
        String body = (String) input.get("body");
        Map<String,Object> bodyMap = null;

        try {
            if (body != null) {
                bodyMap = mapper.readValue(body, Map.class);
            }

            switch (httpMethod.toUpperCase()) {

                case "GET":
                    if (queryParams == null || queryParams.get("action") == null) {
                        return response(400, Map.of("error", "action no especificada en GET"));
                    }
                    String getAction = queryParams.get("action").toLowerCase();
                    if ("list".equals(getAction)) {
                        Collection<Driver> drivers = service.listDrivers();
                        return response(200, drivers);
                    } else if ("get".equals(getAction)) {
                        String id = queryParams.get("id");
                        if (id == null) return response(400, Map.of("error","id no proporcionada para get"));
                        Driver driver = service.getDriver(id);
                        return driver != null ? response(200, driver)
                                : response(404, Map.of("error","Driver no encontrado con ID: "+id));
                    } else {
                        return response(400, Map.of("error","acción GET no soportada: "+getAction));
                    }

                case "POST":
                    if (bodyMap == null || bodyMap.get("driver") == null) {
                        return response(400, Map.of("error","driver no proporcionado en POST"));
                    }
                    Driver driverToCreate = mapper.convertValue(bodyMap.get("driver"), Driver.class);
                    Driver createdDriver = service.registerDriver(driverToCreate);
                    return response(201, createdDriver);

                case "PUT":
                    if (bodyMap == null || bodyMap.get("id") == null || bodyMap.get("status") == null) {
                        return response(400, Map.of("error","id o status faltante en PUT"));
                    }
                    String updateId = (String) bodyMap.get("id");
                    DriverStatus status = DriverStatus.valueOf(((String) bodyMap.get("status")).toUpperCase());
                    Driver updatedDriver = service.updateStatus(updateId, status);
                    return updatedDriver != null ? response(200, updatedDriver)
                            : response(404, Map.of("error","Driver no encontrado con ID: "+updateId));

                case "DELETE":
                    if (bodyMap == null || bodyMap.get("id") == null) {
                        service.deleteAllDrivers();
                        return response(200, Map.of("message","Todos los drivers eliminados"));
                    } else {
                        String deleteId = (String) bodyMap.get("id");
                        service.deleteDriver(deleteId);
                        return response(200, Map.of("message","Driver eliminado con ID: "+deleteId));
                    }

                default:
                    return response(405, Map.of("error","Método HTTP no soportado: "+httpMethod));
            }

        } catch (Exception e) {
            return response(500, Map.of("error", e.getMessage()));
        }
    }

    private Map<String,Object> response(int statusCode, Object body) {
        String bodyJson;
        try {
            bodyJson = mapper.writeValueAsString(body);
        } catch (Exception ex) {
            bodyJson = "{\"error\":\""+ex.getMessage()+"\"}";
        }
        return Map.of(
                "statusCode", statusCode,
                "headers", Map.of("Content-Type", "application/json"),
                "body", bodyJson
        );
    }
}
