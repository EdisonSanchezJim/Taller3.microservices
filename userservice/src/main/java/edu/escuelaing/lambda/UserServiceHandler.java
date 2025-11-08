package edu.escuelaing.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import edu.escuelaing.userservice.model.User;
import edu.escuelaing.userservice.model.UserStatus;
import edu.escuelaing.userservice.service.UserService;

import java.util.Collection;
import java.util.Map;

public class UserServiceHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final UserService service = new UserService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        try {
            // Método HTTP
            String httpMethod = (String) input.get("httpMethod");

            // Query parameters
            Map<String, String> queryParams = (Map<String, String>) input.get("queryStringParameters");

            // Body
            String body = (String) input.get("body");
            Map<String, Object> bodyMap = body != null ? mapper.readValue(body, Map.class) : null;

            String responseBody;

            switch (httpMethod != null ? httpMethod.toUpperCase() : "") {

                case "GET":
                    if (queryParams == null || queryParams.get("action") == null) {
                        responseBody = "{\"error\":\"action no especificada en GET\"}";
                        break;
                    }
                    String getAction = queryParams.get("action").toLowerCase();
                    if ("list".equals(getAction)) {
                        Collection<User> users = service.listUsers();
                        responseBody = mapper.writeValueAsString(users);
                    } else if ("get".equals(getAction)) {
                        String id = queryParams.get("id");
                        if (id == null) {
                            responseBody = "{\"error\":\"id no proporcionada para get\"}";
                        } else {
                            User user = service.getUser(id);
                            responseBody = user != null ? mapper.writeValueAsString(user)
                                    : "{\"error\":\"Usuario no encontrado con ID: " + id + "\"}";
                        }
                    } else {
                        responseBody = "{\"error\":\"acción GET no soportada: " + getAction + "\"}";
                    }
                    break;

                case "POST":
                    if (bodyMap == null || bodyMap.get("user") == null) {
                        responseBody = "{\"error\":\"user no proporcionado en POST\"}";
                    } else {
                        User newUser = mapper.convertValue(bodyMap.get("user"), User.class);
                        User createdUser = service.registerUser(newUser);
                        responseBody = mapper.writeValueAsString(createdUser);
                    }
                    break;

                case "PUT":
                    if (bodyMap == null || bodyMap.get("id") == null || bodyMap.get("status") == null) {
                        responseBody = "{\"error\":\"id o status faltante en PUT\"}";
                    } else {
                        String updateId = (String) bodyMap.get("id");
                        UserStatus status = UserStatus.valueOf(((String) bodyMap.get("status")).toUpperCase());
                        User updatedUser = service.updateStatus(updateId, status);
                        responseBody = updatedUser != null ? mapper.writeValueAsString(updatedUser)
                                : "{\"error\":\"Usuario no encontrado con ID: " + updateId + "\"}";
                    }
                    break;

                case "DELETE":
                    if (bodyMap == null || bodyMap.get("id") == null) {
                        service.deleteAllUsers();
                        responseBody = "{\"message\":\"Todos los usuarios eliminados\"}";
                    } else {
                        String deleteId = (String) bodyMap.get("id");
                        service.deleteUser(deleteId);
                        responseBody = "{\"message\":\"Usuario eliminado con ID: " + deleteId + "\"}";
                    }
                    break;

                default:
                    responseBody = "{\"error\":\"Método HTTP no soportado: " + httpMethod + "\"}";
            }

            // Devuelve el formato que espera API Gateway en proxy integration
            return Map.of(
                    "statusCode", 200,
                    "headers", Map.of("Content-Type", "application/json"),
                    "body", responseBody
            );

        } catch (JsonProcessingException e) {
            return Map.of(
                    "statusCode", 400,
                    "headers", Map.of("Content-Type", "application/json"),
                    "body", "{\"error\":\"JSON inválido: " + e.getMessage() + "\"}"
            );
        } catch (Exception e) {
            return Map.of(
                    "statusCode", 500,
                    "headers", Map.of("Content-Type", "application/json"),
                    "body", "{\"error\":\"" + e.getMessage() + "\"}"
            );
        }
    }
}
