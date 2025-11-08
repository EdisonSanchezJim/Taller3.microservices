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

Cada microservicio cuenta con su propio conjunto de rutas bajo el dominio base de la API Gateway de AWS:


https://uql9xumqp5.execute-api.us-east-1.amazonaws.com/p1/


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


GET https://uql9xumqp5.execute-api.us-east-1.amazonaws.com/p1/users
POST https://uql9xumqp5.execute-api.us-east-1.amazonaws.com/p1/users



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


POST https://uql9xumqp5.execute-api.us-east-1.amazonaws.com/p1/drivers
PUT https://uql9xumqp5.execute-api.us-east-1.amazonaws.com/p1/drivers/D123




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

POST https://uql9xumqp5.execute-api.us-east-1.amazonaws.com/p1/rides
PUT https://uql9xumqp5.execute-api.us-east-1.amazonaws.com/p1/rides/R001/start




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


GET https://uql9xumqp5.execute-api.us-east-1.amazonaws.com/p1/payments
POST https://uql9xumqp5.execute-api.us-east-1.amazonaws.com/p1/payments



**5. Asignación de métodos HTTP según los principios RESTful**

###  **5.1. Microservicio de Usuarios (`/users`)**

| Método     | URI           | Descripción                                                                  | Ejemplo             |
| ---------- | ------------- | ---------------------------------------------------------------------------- | ------------------- |
| **GET**    | `/users`      | Recupera la lista de todos los usuarios registrados.                         | `GET /users`        |
| **GET**    | `/users/{id}` | Consulta la información de un usuario específico.                            | `GET /users/123`    |
| **POST**   | `/users`      | Crea un nuevo usuario con nombre, correo y estado.                           | `POST /users`       |
| **PUT**    | `/users/{id}` | Actualiza los datos de un usuario (por ejemplo, cambiar su estado o correo). | `PUT /users/123`    |
| **DELETE** | `/users/{id}` | Elimina un usuario específico.                                               | `DELETE /users/123` |
| **DELETE** | `/users`      | Elimina todos los usuarios (solo para pruebas).                              | `DELETE /users`     |

 **Ejemplo**
Un cliente envía un `POST /users` con un JSON como:

```json
{
  "name": "Carlos Pérez",
  "email": "carlos@example.com",
  "status": "ACTIVE"
}
```

y la API devuelve:

```json
{
  "id": "U001",
  "message": "Usuario creado exitosamente"
}
```

---

### **5.2. Microservicio de Conductores (`/drivers`)**

| Método     | URI             | Descripción                                           | Ejemplo                |
| ---------- | --------------- | ----------------------------------------------------- | ---------------------- |
| **GET**    | `/drivers`      | Lista todos los conductores registrados.              | `GET /drivers`         |
| **GET**    | `/drivers/{id}` | Obtiene la información completa de un conductor.      | `GET /drivers/D001`    |
| **POST**   | `/drivers`      | Registra un nuevo conductor con sus datos y vehículo. | `POST /drivers`        |
| **PUT**    | `/drivers/{id}` | Actualiza el estado o ubicación de un conductor.      | `PUT /drivers/D001`    |
| **DELETE** | `/drivers/{id}` | Elimina un conductor del sistema.                     | `DELETE /drivers/D001` |
| **DELETE** | `/drivers`      | Elimina todos los conductores (solo pruebas).         | `DELETE /drivers`      |

**EjemplO:**
El sistema podría enviar:

```json
{
  "name": "Ana López",
  "licenseNumber": "XYZ1234",
  "status": "AVAILABLE",
  "vehicle": { "model": "Toyota Corolla", "plate": "ABC123" }
}
```

al endpoint `POST /drivers`, y recibir:

```json
{ "message": "Conductor registrado correctamente", "id": "D001" }
```

---

### **5.3. Microservicio de Viajes (`/rides`)**

| Método     | URI                    | Descripción                                                | Ejemplo                    |
| ---------- | ---------------------- | ---------------------------------------------------------- | -------------------------- |
| **GET**    | `/rides`               | Lista todos los viajes registrados.                        | `GET /rides`               |
| **GET**    | `/rides/{id}`          | Devuelve los detalles de un viaje específico.              | `GET /rides/R123`          |
| **POST**   | `/rides`               | Crea una nueva solicitud de viaje con un usuario y tarifa. | `POST /rides`              |
| **PUT**    | `/rides/{id}/assign`   | Asigna un conductor a un viaje pendiente.                  | `PUT /rides/R123/assign`   |
| **PUT**    | `/rides/{id}/start`    | Cambia el estado del viaje a “en curso”.                   | `PUT /rides/R123/start`    |
| **PUT**    | `/rides/{id}/complete` | Marca el viaje como completado y genera el pago.           | `PUT /rides/R123/complete` |
| **DELETE** | `/rides/{id}`          | Elimina un viaje específico.                               | `DELETE /rides/R123`       |
| **DELETE** | `/rides`               | Elimina todos los viajes (solo para pruebas).              | `DELETE /rides`            |

**Ejemplo:**
Cuando un usuario solicita un viaje:

```json
{ "riderId": "U001", "fare": 35000 }
```

se envía a `POST /rides`, y la API responde:

```json
{ "id": "R123", "status": "PENDING" }
```

---

###  **5.4. Microservicio de Pagos (`/payments`)**

| Método     | URI              | Descripción                                                            | Ejemplo                 |
| ---------- | ---------------- | ---------------------------------------------------------------------- | ----------------------- |
| **GET**    | `/payments`      | Lista todos los pagos registrados.                                     | `GET /payments`         |
| **GET**    | `/payments/{id}` | Devuelve los detalles de un pago específico.                           | `GET /payments/P001`    |
| **POST**   | `/payments`      | Registra un nuevo pago asociado a un viaje.                            | `POST /payments`        |
| **PUT**    | `/payments/{id}` | Actualiza el estado del pago (por ejemplo, de “pendiente” a “pagado”). | `PUT /payments/P001`    |
| **DELETE** | `/payments/{id}` | Elimina un pago específico.                                            | `DELETE /payments/P001` |
| **DELETE** | `/payments`      | Elimina todos los pagos (solo para mantenimiento).                     | `DELETE /payments`      |

 **Ejemplo:**
Un pago puede crearse así:

```json
{
  "rideId": "R123",
  "userId": "U001",
  "amount": 35000,
  "status": "PENDING"
}
```

con `POST /payments`, y luego actualizar su estado con:

```
PUT /payments/P001
```

para marcarlo como:

```json
{ "status": "COMPLETED" }
```


---
**6. Diseño de arquitectura de microservicios — Plataforma de ridesharing** 

La arquitectura propuesta está pensada para un entorno cloud (ej. AWS) usando funciones serverless (Lambda) y API Gateway como puerta de entrada, más mensajería asíncrona para integración entre servicios (p. ej. RabbitMQ o SNS/SQS/Kafka). Cada microservicio es responsable de su propio dominio (Users, Drivers, Rides, Payments), su propia persistencia y API. Para la comunicación en tiempo real usamos WebSockets o un servicio de pub/sub (AWS API Gateway WebSocket + SNS/SQS o WebSocket server en ECS/Fargate).



### 7. Arquitectura general del prototipo

La arquitectura del prototipo sigue el patrón **serverless**, con un enfoque modular por microservicio:

1. **API Gateway** actúa como punto de entrada único para las peticiones HTTP.
2. **AWS Lambda** maneja la lógica de negocio de cada microservicio (Users, Drivers, Rides, Payments).
3. **CloudWatch** registra las ejecuciones, errores y métricas de cada función Lambda.

---

### Flujo básico de funcionamiento

El flujo de trabajo entre los servicios es el siguiente:

1. El cliente realiza una solicitud HTTP (por ejemplo, un `POST /payments` o `GET /users`) hacia **API Gateway**.
2. API Gateway enruta la solicitud hacia la **función Lambda** correspondiente, dependiendo del microservicio.
3. La función Lambda ejecuta la lógica del negocio:

   * Procesa los datos recibidos.
   * Realiza validaciones básicas.
   * Devuelve una respuesta JSON estandarizada.
4. En caso de operaciones más complejas (por ejemplo, procesar un pago), la Lambda puede comunicarse con otra función mediante eventos o acceder a una base de datos temporal.
5. Finalmente, API Gateway devuelve la respuesta al cliente en formato **JSON**, cumpliendo con los principios REST.


###  Descripción técnica de la implementación

**a) API Gateway**

* Se crearon endpoints específicos para cada servicio:

  * `/users` → gestionar usuarios.
  * `/drivers` → gestionar conductores.
  * `/rides` → gestionar viajes.
  * `/payments` → registrar y listar pagos.
* Cada endpoint acepta diferentes métodos HTTP (`GET`, `POST`, `PUT`, `DELETE`).
* Se habilitó **CORS** para permitir solicitudes desde distintas interfaces (por ejemplo, una app web o Postman).

**b) AWS Lambda**

* Cada microservicio tiene un **handler Java** independiente (por ejemplo: `UserServiceHandler`, `PaymentServiceHandler`).
* Las funciones fueron empaquetadas usando **Maven** con el **Shade Plugin**, generando un `.jar` con todas las dependencias necesarias.
* Se definieron los roles de ejecución mínimos en IAM para que las Lambdas pudieran escribir logs y comunicarse con API Gateway.
* Cada handler maneja las peticiones a través de eventos JSON, procesando los parámetros enviados en el cuerpo o los query strings.



###  Ejemplo de ejecución del prototipo

#### Ejemplo 1 – Crear un pago

```bash
curl -X POST "https://uql9xumqp5.execute-api.us-east-1.amazonaws.com/p1/payments?action=create" \
-H "Content-Type: application/json" \
-d '{"paymentId": "P001", "userId": "U001", "amount": 25000, "method": "card"}'
```

**Respuesta esperada:**

```json
{
  "message": "Payment created successfully",
  "paymentId": "P001",
  "status": "OK"
}
```

#### Ejemplo 2 – Listar pagos

```bash
curl -X GET "https://uql9xumqp5.execute-api.us-east-1.amazonaws.com/p1/payments?action=list"
```

**Respuesta esperada:**

```json
{
  "payments": [
    { "paymentId": "P001", "userId": "U001", "amount": 25000, "method": "card" },
    { "paymentId": "P002", "userId": "U002", "amount": 15000, "method": "cash" }
  ]
}
```


**Evidencias de las impleentaciones**
---

**funciones de lambda**

<img width="1440" height="900" alt="Captura de Pantalla 2025-11-07 a la(s) 9 53 49 p m" src="https://github.com/user-attachments/assets/475ec616-adec-4fc0-ab13-947fe6901f33" />

----
**funciòn UserService**

<img width="1440" height="900" alt="Captura de Pantalla 2025-11-07 a la(s) 9 54 27 p m" src="https://github.com/user-attachments/assets/ceba3738-adad-4193-bede-69fa6ec2c7a7" />
<img width="1440" height="900" alt="Captura de Pantalla 2025-11-07 a la(s) 9 55 24 p m" src="https://github.com/user-attachments/assets/092058ba-69e1-49c0-974a-4feb48775f49" />


----
**funciòn DriverService**

<img width="1440" height="900" alt="Captura de Pantalla 2025-11-07 a la(s) 9 56 09 p m" src="https://github.com/user-attachments/assets/ae23c31c-fb30-4021-a148-ec8e8f82126b" />
<img width="1440" height="900" alt="Captura de Pantalla 2025-11-07 a la(s) 9 56 21 p m" src="https://github.com/user-attachments/assets/cae2c7a4-f7db-45ce-a238-a0b9278acab5" />

----
**funciòn RideService**
<img width="1440" height="900" alt="Captura de Pantalla 2025-11-07 a la(s) 9 56 47 p m" src="https://github.com/user-attachments/assets/12ebf7b6-3fbb-4d7b-8dba-82e071a016c7" />
<img width="1440" height="900" alt="Captura de Pantalla 2025-11-07 a la(s) 9 57 07 p m" src="https://github.com/user-attachments/assets/a459661e-42da-4c66-8e26-f59b3eec0849" />

----
**funciòn RideService**
<img width="1440" height="900" alt="Captura de Pantalla 2025-11-07 a la(s) 9 57 55 p m" src="https://github.com/user-attachments/assets/3d9c8bd5-b8d1-4777-8e0a-576d5971f778" />

----
**API GATEWAY**

<img width="1440" height="900" alt="Captura de Pantalla 2025-11-07 a la(s) 9 59 26 p m" src="https://github.com/user-attachments/assets/381668a2-6959-4834-a13d-9245b762d6d4" />
<img width="1440" height="900" alt="Captura de Pantalla 2025-11-07 a la(s) 9 59 46 p m" src="https://github.com/user-attachments/assets/a8eaf983-da37-4e09-9cb3-f9cc4d47700f" />
<img width="1440" height="900" alt="Captura de Pantalla 2025-11-07 a la(s) 10 00 08 p m" src="https://github.com/user-attachments/assets/cee1305f-1102-4eff-9242-1fa774f640a9" />
<img width="1440" height="900" alt="Captura de Pantalla 2025-11-07 a la(s) 10 00 37 p m" src="https://github.com/user-attachments/assets/0a961afe-ca68-49a2-841b-f4efda0d4eaa" />



Perfecto 👌 Aquí tienes un **README.md** redactado **en español, con un tono natural, como si lo hubiera escrito un estudiante o desarrollador**, explicando el diseño y las decisiones arquitectónicas del proyecto. Está pensado para acompañar tu repositorio en GitHub.

---

# 🚗 Plataforma de Viajes - Arquitectura de Microservicios con AWS

## 📖 Descripción General

Este proyecto busca construir una **plataforma de viajes compartidos** basada en una arquitectura de **microservicios**.
La idea principal es dividir la aplicación en servicios independientes —como usuarios, conductores, viajes y pagos— que se comuniquen entre sí de manera ligera y eficiente, siguiendo los principios de **REST** y desplegados en la nube mediante **AWS Lambda** y **API Gateway**.

Cada microservicio fue desarrollado de forma modular, utilizando **Java** como lenguaje principal, empaquetado con **Maven**, y pensado para ejecutarse sin depender de servidores tradicionales gracias al modelo **serverless** de AWS.

---

## 💡 Metáfora de diseño

La metáfora que guía el diseño es la de un **sistema de transporte colaborativo**, donde cada componente del sistema cumple un rol claro dentro del “ecosistema” del viaje:

* **User Service** representa a los pasajeros que solicitan viajes.
* **Driver Service** simboliza a los conductores disponibles que aceptan solicitudes.
* **Ride Service** actúa como el intermediario que organiza y gestiona los trayectos entre usuarios y conductores.
* **Payment Service** funciona como el encargado de manejar los pagos, garantizando que cada viaje concluya correctamente.

Cada uno de estos servicios trabaja de forma autónoma, pero todos colaboran para que el proceso de un viaje (desde la solicitud hasta el pago final) se realice sin fricciones.
La independencia de cada servicio es la clave para escalar y mantener el sistema fácilmente.

---

##  Decisiones Arquitectónicas

### 1. **Arquitectura basada en microservicios**

Optè por una arquitectura de **microservicios desacoplados**, donde cada dominio del negocio se implementa y despliega por separado.
Esta decisión permite:

* Escalar cada componente según su demanda (por ejemplo, más capacidad para “rides” en horas pico).
* Facilitar la actualización de un servicio sin afectar a los demás.
* Mejorar la mantenibilidad del código y la organización del proyecto.

### 2. **Uso de AWS Lambda y API Gateway**

En lugar de tener un servidor centralizado, se utilizó el modelo **serverless**:

* **AWS Lambda** ejecuta la lógica de cada microservicio.
* **API Gateway** se encarga de recibir las peticiones HTTP, enrutar las solicitudes al Lambda correcto y devolver las respuestas en formato JSON.

Esto reduce costos, ya que solo se paga por el tiempo de ejecución real, y ofrece escalabilidad automática sin necesidad de administrar infraestructura.

### 3. **Comunicación RESTful**

Todas las interacciones entre los clientes y los microservicios siguen el estándar **REST**, utilizando métodos HTTP:

* `GET` para obtener información
* `POST` para crear recursos
* `PUT` para actualizaciones
* `DELETE` para eliminaciones

Además, se habilitó **CORS** en API Gateway para permitir que las peticiones puedan realizarse desde aplicaciones web o Postman sin restricciones de origen.

### 4. **Representación de datos en JSON**

Los datos se intercambian en formato **JSON** por su simplicidad y compatibilidad con múltiples lenguajes y plataformas.
Esto facilita la integración futura con interfaces web, móviles o incluso sistemas externos.

### 5. **Despliegue modular y escalable**

Cada servicio (`/users`, `/drivers`, `/rides`, `/payments`) fue desplegado como un endpoint independiente dentro de API Gateway.
Gracias a esta separación:

* Se pueden escalar funciones de forma individual.
* Es posible depurar errores o probar funcionalidades sin afectar al resto del sistema.
* El despliegue y mantenimiento son más ágiles.

### 6. **Pruebas con EC2 y herramientas locales**

Se utilizó una instancia EC2 para pruebas de integración y ejecución de comandos `curl`, verificando el correcto funcionamiento de los endpoints antes de publicarlos.
Además, se validaron las respuestas de las Lambdas y la integración completa con API Gateway.

---

##  Endpoints principales

| Servicio     | URI base    | Métodos soportados             | Descripción breve                |
| ------------ | ----------- | ------------------------------ | -------------------------------- |
| **Users**    | `/users`    | `GET`, `POST`, `DELETE`        | Gestión de usuarios              |
| **Drivers**  | `/drivers`  | `GET`, `POST`, `PUT`, `DELETE` | Gestión de conductores           |
| **Rides**    | `/rides`    | `GET`, `POST`, `PUT`, `DELETE` | Creación y seguimiento de viajes |
| **Payments** | `/payments` | `GET`, `POST`                  | Registro y consulta de pagos     |

---

##  Razonamiento detrás del diseño

El enfoque se centró en **simplificar la comunicación** entre servicios sin sacrificar flexibilidad.
Al usar **Lambda Functions**, se logra una implementación ligera, fácil de desplegar y mantener, ideal para entornos académicos o prototipos en crecimiento.
La **modularidad** garantiza que el sistema pueda evolucionar sin requerir una reestructuración completa.

Por ejemplo, el servicio de pagos podría escalar o reemplazarse por una integración con un gateway real (como Stripe o MercadoPago) sin afectar el resto de la aplicación.

---

##  Próximos pasos y mejoras posibles

* Integrar **DynamoDB** o **RDS** para almacenamiento persistente.
* Implementar **mensajería asíncrona (SQS o SNS)** para eventos entre servicios.
* Agregar **autenticación y autorización** con Amazon Cognito.
* Desarrollar una **interfaz web o móvil** conectada directamente al API Gateway.
* Integrar **CloudWatch** y **X-Ray** para monitoreo y trazabilidad.

---

##  Equipo y herramientas

**Lenguaje:** Java
**Gestor de dependencias:** Maven
**Infraestructura:** AWS (Lambda, API Gateway, EC2)
**Formato de datos:** JSON
**Estilo de arquitectura:** RESTful microservices

---

