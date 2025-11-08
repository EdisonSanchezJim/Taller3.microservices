**Taller 3**

**1. Análisis del problema y modelado del dominio**

**1.1 Descripción general del problema**

El propósito del proyecto es diseñar e implementar una plataforma de transporte tipo ride-sharing, en la que los usuarios puedan solicitar viajes, los conductores aceptarlos y completarlos, y finalmente se realice el pago de manera automatizada.

Para lograrlo, se plantea una arquitectura basada en microservicios, donde cada componente del sistema es independiente, escalable y puede desarrollarse o desplegarse de forma separada.
Dentro del dominio del problema se identifican cuatro entidades principales:

+Usuarios (Users)

+Conductores (Drivers)

+Viajes (Rides)

+Pagos (Payments)

Cada una de estas entidades se gestiona a través de un microservicio propio, lo que permite mantener una estructura modular y facilita la evolución del sistema en el tiempo.

**1.2 Recursos principales identificados**

| **Recurso**            | **Descripción**                                                                                                                                                   |
| ---------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Usuario (User)**     | Representa a la persona que solicita un viaje dentro de la plataforma. Contiene información básica y su estado actual dentro del sistema.                         |
| **Conductor (Driver)** | Representa a la persona que ofrece el servicio de transporte. Incluye sus datos personales, el vehículo que utiliza y su ubicación.                               |
| **Viaje (Ride)**       | Representa la interacción entre un usuario y un conductor. Contiene la información del recorrido, su estado, la tarifa y los identificadores de los involucrados. |
| **Pago (Payment)**     | Representa la transacción económica asociada a un viaje, vinculando el usuario, el conductor y el monto total a pagar.                                            |


1.3 Relaciones entre los recursos

-Un usuario puede tener varios viajes asociados.
-Un conductor puede realizar múltiples viajes (aunque solo uno activo a la vez).
-Cada viaje genera un pago una vez que ha sido completado.
-Un pago pertenece a un único viaje y está asociado a un usuario específico.

1.4 Modelo de dominio (UML)

classDiagram
    class User {
        +String id
        +String name
        +String email
        +String status
    }

    class Driver {
        +String id
        +String name
        +String licenseNumber
        +String status
        +Vehicle vehicle
        +Location location
    }

    class Vehicle {
        +String model
        +String plate
    }

    class Location {
        +double lat
        +double lng
    }

    class Ride {
        +String id
        +String riderId
        +String driverId
        +double fare
        +String status
    }

    class Payment {
        +String id
        +String rideId
        +String userId
        +double amount
        +String status
    }

    User "1" --> "many" Ride
    Driver "1" --> "many" Ride
    Ride "1" --> "1" Payment
    Payment "1" --> "1" User

1.5 Representación del modelo en código

Cada microservicio define las clases principales de su dominio. A continuación se muestran algunos ejemplos:

public class Ride {
    private String id;
    private String riderId;
    private String driverId;
    private double fare;
    private RideStatus status;
}

public class Driver {
    private String id;
    private String name;
    private String licenseNumber;
    private DriverStatus status;
    private Vehicle vehicle;
    private Location location;
}

public class Payment {
    private String id;
    private String rideId;
    private String userId;
    private double amount;
    private PaymentStatus status;
}

public class User {
    private String id;
    private String name;
    private String email;
    private String status;


2. Identificación de recursos relacionados y métodos de soporte

Una vez definidos los recursos principales (usuarios, conductores, viajes y pagos), es necesario identificar los recursos relacionados y 
los métodos de soporte que permiten la interacción entre ellos. Estos recursos adicionales facilitan la gestión de solicitudes de viaje, 
la administración de conductores y el seguimiento en tiempo real de los trayectos.


**2.1 Solicitudes de viaje (Ride Requests)**
Este recurso permite que los usuarios puedan solicitar un viaje, indicando su ubicación actual y destino.
El sistema debe asignar un conductor disponible y crear un registro de viaje con el estado inicial “pendiente” o “en curso”.


**Atributos principales:**

id → Identificador único de la solicitud.

userId → Usuario que solicita el viaje.

pickupLocation → Punto de recogida.

destination → Destino final.

status → Estado de la solicitud (pendiente, asignado, completado, cancelado).



**Métodos de soporte:**

POST /rides/request → Crea una nueva solicitud de viaje.

GET /rides/{id} → Consulta los detalles de una solicitud específica.

PUT /rides/{id}/cancel → Cancela una solicitud antes de ser asignada.

Estos métodos permiten que el servicio de viajes gestione la creación, consulta y cancelación de solicitudes en tiempo real.



**2.2 Gestión de conductores (Driver Management)**

El recurso de conductores permite administrar toda la información relacionada con los choferes registrados en el sistema, incluyendo su disponibilidad, ubicación y estado del vehículo.
Además, es esencial para que el sistema pueda asignar un conductor disponible a un viaje.



**Atributos principales:**


id → Identificador del conductor.

name → Nombre del conductor.

licenseNumber → Número de licencia.

status → Estado actual (disponible, en viaje, inactivo).

location → Ubicación actual.



**Métodos de soporte:**

GET /drivers → Obtiene la lista de todos los conductores registrados.

GET /drivers/{id} → Devuelve la información de un conductor específico.

PUT /drivers/{id}/status → Actualiza el estado de disponibilidad.

PUT /drivers/{id}/location → Actualiza la ubicación actual del conductor (por ejemplo, cada cierto intervalo de tiempo).

Estos métodos garantizan que el sistema pueda conocer en todo momento qué conductores están activos y disponibles para recibir solicitudes de viaje.




### **3. Definición de la representación de los recursos para estandarizar las interacciones entre servicios**

Para garantizar una comunicación consistente entre los diferentes microservicios (usuarios, conductores, viajes y pagos), es fundamental definir un formato estándar de representación de los recursos. Esto permite que todos los servicios puedan intercambiar información de forma clara, predecible y sin depender de la implementación interna de cada componente.

La representación elegida para este proyecto es JSON (JavaScript Object Notation), debido a su compatibilidad con la mayoría de las tecnologías web, su ligereza y su facilidad de lectura tanto por humanos como por máquinas.



**3.1. Estructura general de los recursos**
Cada microservicio expone sus recursos a través de endpoints RESTful, donde la respuesta se devuelve en formato JSON con una estructura clara, consistente y acompañada de códigos HTTP que reflejan el resultado de la operación.


### **USERS: Define la información básica de cada usuario registrado en la plataforma.**

{
  "id": "U001",
  "name": "Laura Gómez",
  "email": "laura.gomez@example.com",
  "phone": "+573001234567",
  "role": "rider",
  "registeredAt": "2025-11-07T12:00:00Z"
}

### **DRIVERS: Contiene la información del conductor, su estado y ubicación actual.**

{
  "id": "D123",
  "name": "Carlos López",
  "licenseNumber": "ABC-4567",
  "status": "available",
  "location": {
    "latitude": 4.6097,
    "longitude": -74.0817
  },
  "vehicle": {
    "brand": "Toyota",
    "model": "Corolla",
    "plate": "XYZ-987"
  }
}

### **RIDE: Describe la solicitud y ejecución de un viaje, incluyendo los participantes y su estado.**

{
  "id": "R789",
  "userId": "U001",
  "driverId": "D123",
  "pickupLocation": "Calle 45 #12-30",
  "destination": "Cra 15 #93-60",
  "status": "in_progress",
  "fare": 18500,
  "requestedAt": "2025-11-07T12:10:00Z",
  "completedAt": null
}

**Refleja la información relacionada con el pago de un viaje.**

{
  "id": "P456",
  "rideId": "R789",
  "amount": 18500,
  "currency": "COP",
  "method": "credit_card",
  "status": "paid",
  "timestamp": "2025-11-07T12:40:00Z"
}



## **4. Creación de URIs modelo para cada recurso**

Para mantener una estructura clara, predecible y fácil de mantener dentro de la arquitectura de microservicios, es fundamental definir **URIs bien organizadas** para cada recurso.
El objetivo es que cualquier desarrollador o cliente (por ejemplo, una aplicación web o móvil) pueda **entender de inmediato cómo interactuar con cada servicio** solo observando la estructura de los endpoints.

Cada microservicio cuenta con su propio conjunto de rutas bajo el dominio base de la API Gateway de AWS:

```
https://uql9xumqp5.execute-api.us-east-1.amazonaws.com/p1/
```

A partir de esta base, se organizan los recursos principales: `users`, `drivers`, `rides` y `payments`.
Cada uno tiene URIs RESTful bien definidas que representan acciones o datos específicos.



### **4.1. URIs del microservicio de Usuarios (`/users`)**

| Método     | URI           | Descripción                                                     |
| ---------- | ------------- | --------------------------------------------------------------- |
| **GET**    | `/users`      | Obtiene la lista completa de usuarios registrados.              |
| **GET**    | `/users/{id}` | Devuelve la información detallada de un usuario específico.     |
| **POST**   | `/users`      | Crea un nuevo usuario en la plataforma.                         |
| **PUT**    | `/users/{id}` | Actualiza la información de un usuario existente.               |
| **DELETE** | `/users/{id}` | Elimina un usuario de la base de datos.                         |
| **DELETE** | `/users`      | Elimina todos los usuarios (solo para pruebas o mantenimiento). |

 **Ejemplo de uso:**

```
GET https://uql9xumqp5.execute-api.us-east-1.amazonaws.com/p1/users
POST https://uql9xumqp5.execute-api.us-east-1.amazonaws.com/p1/users
```

---

###  **4.2. URIs del microservicio de Conductores (`/drivers`)**

| Método     | URI             | Descripción                                                                          |
| ---------- | --------------- | ------------------------------------------------------------------------------------ |
| **GET**    | `/drivers`      | Obtiene todos los conductores registrados.                                           |
| **GET**    | `/drivers/{id}` | Devuelve la información de un conductor específico.                                  |
| **POST**   | `/drivers`      | Crea un nuevo conductor con su información de vehículo y licencia.                   |
| **PUT**    | `/drivers/{id}` | Actualiza la información del conductor (por ejemplo, cambiar su estado o ubicación). |
| **DELETE** | `/drivers/{id}` | Elimina un conductor.                                                                |
| **DELETE** | `/drivers`      | Elimina todos los conductores (solo para mantenimiento o pruebas).                   |

**Ejemplo de uso:**

```
POST https://uql9xumqp5.execute-api.us-east-1.amazonaws.com/p1/drivers
PUT https://uql9xumqp5.execute-api.us-east-1.amazonaws.com/p1/drivers/D123
```

---

###  **4.3. URIs del microservicio de Viajes (`/rides`)**

| Método     | URI                    | Descripción                                               |
| ---------- | ---------------------- | --------------------------------------------------------- |
| **GET**    | `/rides`               | Lista todos los viajes registrados.                       |
| **GET**    | `/rides/{id}`          | Obtiene la información detallada de un viaje.             |
| **POST**   | `/rides`               | Crea una nueva solicitud de viaje.                        |
| **PUT**    | `/rides/{id}/assign`   | Asigna un conductor a un viaje pendiente.                 |
| **PUT**    | `/rides/{id}/start`    | Marca el inicio del viaje.                                |
| **PUT**    | `/rides/{id}/complete` | Finaliza el viaje y genera la solicitud de pago.          |
| **DELETE** | `/rides/{id}`          | Elimina un viaje específico.                              |
| **DELETE** | `/rides`               | Elimina todos los viajes registrados (solo para pruebas). |

 **Ejemplo de uso:**

```
POST https://uql9xumqp5.execute-api.us-east-1.amazonaws.com/p1/rides
PUT https://uql9xumqp5.execute-api.us-east-1.amazonaws.com/p1/rides/R001/start
```

---

### **4.4. URIs del microservicio de Pagos (`/payments`)**

| Método     | URI              | Descripción                                                              |
| ---------- | ---------------- | ------------------------------------------------------------------------ |
| **GET**    | `/payments`      | Lista todos los pagos realizados.                                        |
| **GET**    | `/payments/{id}` | Consulta el estado de un pago específico.                                |
| **POST**   | `/payments`      | Crea un nuevo registro de pago asociado a un viaje.                      |
| **PUT**    | `/payments/{id}` | Actualiza el estado de un pago (por ejemplo, de “pendiente” a “pagado”). |
| **DELETE** | `/payments/{id}` | Elimina un pago específico.                                              |
| **DELETE** | `/payments`      | Elimina todos los pagos (uso de pruebas).                                |

**Ejemplo de uso:**

```
GET https://uql9xumqp5.execute-api.us-east-1.amazonaws.com/p1/payments
POST https://uql9xumqp5.execute-api.us-east-1.amazonaws.com/p1/payments
```




