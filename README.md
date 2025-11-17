# ⭐ **README.md — TechnicalTestBankingJava**
**Prueba Técnica — Microservicio de Iniciación de Pagos (Payment Initiation)**
**Autor**: Lenin Alomoto
**Arquitectura**: Hexagonal • Spring Boot 3.5.7 • Java 21 • Docker & Docker Compose

---

# 🚀 **1. Resumen del Proyecto**

Este repositorio corresponde al desarrollo del microservicio **Payment Initiation**, utilizado para:

- Iniciar órdenes de pago  
- Consultar órdenes de pago  
- Consultar el estado de órdenes de pago  

Partiendo de un **WSDL legado**, se realiza un análisis, modernización y exposición REST usando:

- **Enfoque Contract-First con OpenAPI 3.0**  
- **Arquitectura Hexagonal limpia (Ports & Adapters)**  
- **Java 21 + Spring Boot 3.5.7**  
- **MapStruct para mapeos**  
- **PostgreSQL + Docker Compose**  
- **Testing con cobertura del 87%**  
- **Registro de Prompts como evidencia del uso asistido de IA**

---

# 🧱 **2. Arquitectura Hexagonal**

```
                          +----------------------+
                          |    REST Controller   |
                          +----------+-----------+
                                     |
                                     v
+------------------+      +----------+-----------+      +----------------------+
|    Domain        | ---> | Application UseCases | ---> | Infrastructure Adapters |
+------------------+      +----------------------+      +----------------------+
| Entities (core)  |      | Business logic       |      | JPA / REST Mapper    |
| Value Objects    |      | No framework deps    |      | Repository Adapter   |
| Domain rules     |      | Ports (interfaces)   |      | Exception Handler    |
+------------------+      +----------------------+      +----------------------+
```

### **Componentes principales**

| Capa | Contenido |
|------|-----------|
| **Domain** | `PaymentOrder`, `BankAccount`, `PaymentAmount`, `PaymentOrderStatus` |
| **Application** | `PaymentOrderUseCase`, `RetrievePaymentUseCase` |
| **Infrastructure** | REST, JPA, MapStruct, Handlers, Config |
| **Adapter** | `PaymentOrderRepositoryAdapter`, `PaymentOrderRestMapper` |

---

# 📄 **3. Contract-First con OpenAPI**

El contrato se encuentra en:

```
micro-payment-initiation/src/main/resources/openapi.yaml
```

Este contrato genera:

- Interfaces REST  
- DTOs  
- Validaciones  
- Endpoints documentados con Swagger  

Swagger UI disponible en:

```
http://localhost:8080/swagger-ui.html
```

El contrato fue refinado para cumplir con:
- OpenAPI 3.0.3
- Modelos consistentes con el dominio
- Validaciones correctas con @NotNull, @Valid
- Paths:
	- POST /payment-initiation/payment-orders
	- GET /payment-initiation/payment-orders/{id}
	- GET /payment-initiation/payment-orders/{id}/status

---

# 🧪 **4. Testing — Cobertura 89%**

Herramientas:

- JUnit 5  
- Mockito / MockMvc  
- Jacoco  

Cobertura:

- Controllers → 100% líneas  
- Application → 94%+  
- Mappers → 85%  
- Domain → 84%  
- Adapters JPA → cubiertos  
- Exception Handler → 100%  

Jacoco report:

```
target/site/jacoco/index.html
```

---

# 🐳 **5. Docker & Docker Compose**

## 🟦 5.1 Árbol de archivos

```
TechnicalTestBankingJava/
├── micro-payment-initiation/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/...
├── ia/
│   ├── prompts.md
│   └── decisions.md
├── docker-compose.yml
├── .env
└── README.md
```

---

## 🟦 5.2 **Dockerfile (Java 21 + Spring Boot 3.5.7)**

```dockerfile
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```

---

## 🟦 5.3 **docker-compose.yml**

```yaml
services:
  postgres:
    image: postgres:17.6-alpine
    container_name: payment-postgres
    env_file:
      - .env
    environment:
      POSTGRES_DB: ${POSTGRES_DB}
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    ports:
      - "${POSTGRES_PORT}:5432"
    volumes:
      - payment_data:/var/lib/postgresql/data

  payment-initiation:
    build: ./micro-payment-initiation
    container_name: payment-initiation-service
    env_file:
      - .env
    ports:
      - "${SERVER_PORT}:8080"
    depends_on:
      - postgres

volumes:
  payment_data:
```

---

## 🟦 5.4 **.env**

```
POSTGRES_DB=paymentdb
POSTGRES_USER=payment_user
POSTGRES_PASSWORD=payment_pass
POSTGRES_PORT=5435

SERVER_PORT=8080

SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/paymentdb
SPRING_APPLICATION_NAME=micro-payment-initiation
```

---

# ▶️ **6. Cómo ejecutar**

## ✔ Local (sin Docker)

- Instalar Java 21
- Instalar Maven
- Crear la BD en Postgres

Ejecutar:

```
mvn spring-boot:run
```

---

## ✔ Con Docker

```
docker compose up -d --build
```

---

## ✔ Reporte

```
mvn clean verify
```

---

# 🧠 **7. Registro de Uso Asistido de Inteligencia Artificial (Obligatorio)**

Se encuentra en la crapeta **ia**

---

# 📚 **8. Actuator — Health, Metrics, Info**

El microservicio expone endpoints Actuator para:

✔ Health Check
/actuator/health

✔ Métricas (Prometheus-ready)
/actuator/metrics
/actuator/prometheus


Incluyendo:
- heap / non-heap usage
- CPU
- requests por endpoint
- respuestas por estado
- tiempo de ejecución por handler

✔ Información del servicio
/actuator/info

✔ Listado completo
/actuator

Actuator UI disponible en:

```
http://localhost:8088/actuator/
```

---

# 📚 **9. Referencias**

- BIAN 11.0  
- Spring Boot 3.5.7  
- OpenAPI 3.0.3 Specification  
- PostgreSQL 17  
- Docker & Docker Compose  
- OWASP API Security Top 10  

---

# 🏁 **10. Conclusiones**

Este proyecto demuestra:

- Habilidad para migrar servicios SOAP → REST
- Modelado basado en BIAN
- Contratos Contract-First
- Arquitecturas mantenibles
- Capacidad de testing avanzado
- Uso responsable de IA generativa
