# Registro de Prompts Usados (Uso Asistido de IA)

Este documento registra los prompts utilizados para resolver el
TechnicalTestBankingJava y resume las respuestas generadas por la
IA, así como fragmentos relevantes producidos por el modelo.

------------------------------------------------------------
1. RESUMEN DEL WSDL + MAPEOS HACIA BIAN
------------------------------------------------------------

## Prompt
“Resume este WSDL y propón el mapeo correspondiente hacia el dominio
y service domain de BIAN. Quiero identificar operaciones, tipos de datos,
estructura del servicio SOAP y un posible Service Domain en BIAN”

## Resumen de la respuesta
El WSDL fue resumido identificando:
- Servicio: PaymentOrderService
- Operación: `initiatePaymentOrder`
- Endpoint SOAP identificado
- Tipos relevantes extraídos del esquema

La IA propuso el mapeo BIAN:
- Service Domain: Payment Execution
- Control Record: Payment Order
- BQs sugeridos: Initiation, Fulfillment, Status
- Justificación alineada con PSD2/ISO20022

## Fragmento generado relevante
“Service Domain recomendado: Payment Execution.  
El PaymentOrder del WSDL corresponde directamente al Control Record.
Los Behavior Qualifiers aplicables serían Initiation, Fulfillment y 
Status, basados en ISO20022 pain.001 y en el rol del servicio.”

## Correcciones manuales
Ninguna — análisis conceptual entero generado por IA


------------------------------------------------------------
2. GENERACIÓN DEL BORRADOR OPENAPI 3.0
------------------------------------------------------------

## Prompt
“Necesito que generes un borrador de OpenAPI 3.0 basado en el WSDL. 
Debe incluir operaciones, estructura del servicio, request y response, 
y los modelos necesarios."

## Resumen de la respuesta
La IA generó el archivo completo con:
- POST /payment-initiation/payment-orders
- GET /payment-initiation/payment-orders/{paymentOrderId}
- GET /payment-initiation/payment-orders/{paymentOrderId}/status
- Modelos: PaymentOrderInitiateRequest, Amount, AccountReference, etc.
- Manejo de errores y examples

## Fragmento generado relevante
Amount:
  type: object
  required: [ amount, currency ]
  properties:
    amount:
      type: number
      format: double

## Correcciones manuales
Se cambió:
- format: double → format: decimal  
para alinear con BigDecimal del dominio.


------------------------------------------------------------
3. ESQUELETO DE ARQUITECTURA HEXAGONAL
------------------------------------------------------------

## Prompt enviado por el usuario
“Arquitectura hexagonal (dominio, aplicación, puertos, adaptadores, config). 
Esta arquitectura ya la tengo definida y te la envío para que la revises, 
me digas si está bien y la mejores si es necesario. 

Los sufijos son fijos y NO se cambian (UseCase, UseCaseImpl, RepositoryPort,
RepositoryAdapter, RestMapper, Entity, Request, Response, Util, etc.).

La estructura base es:

micro-nombre-micro
|-- nombre_modulo (usar Screaming Architecture)
    |-- application
    |   |-- mappers/
    |   |   |-- NameMapper.java
    |   |-- validations/
    |   |   |-- NameValidator.java
    |   |-- ServiceNameUseCaseImpl.java
    |-- domain
    |   |-- models/ModelName.java
    |   |-- usecase/ServiceNameUseCase.java
    |   |-- port/PortNameRepositoryPort.java
    |-- infrastructure
    |   |-- adapter
    |   |   |-- entities/NameEntity.java
    |   |   |-- mappers/NameAdapterMapper.java
    |   |   |-- repositories/NameRepositoryJpa.java
    |   |   |-- NameRepositoryAdapter.java
    |   |-- rest
    |   |   |-- mappers/NameRestMapper.java
    |   |   |-- requests/NameRequest.java
    |   |   |-- responses/NameResponse.java
    |   |   |-- controllers/ControllNameController.java
    |-- config
        |-- utilities/nameUtil.java

Objetivo pedido:
“Respeta este estilo Screaming Architecture, en inglés, con suffijos 
fijos, mapeos REST, adapters, domain models, y casos de uso reales.”

## Resumen de la respuesta de la IA
La IA:
- Validó tu estructura existente (muy completa y bien organizada).
- Confirmó que sigue correctamente el patrón hexagonal.
- Ajustó los nombres y generó la **implementación alineada a tus sufijos fijos**.
- Generó los módulos *payment* bajo Screaming Architecture.
- Generó:
  - UseCases
  - Implementaciones UseCaseImpl
  - Puertos RepositoryPort
  - Adaptadores RepositoryAdapter
  - Mappers RestMapper y AdapterMapper con MapStruct
  - Controladores REST alineados al OpenAPI 3.0 generado
- Siguió tu estructura exacta para empaquetar requests, responses y mappers REST.

## Fragmento generado relevante
public interface PaymentOrderUseCase {
    PaymentOrder initiatePayment(PaymentOrder order);
}

public class PaymentOrderUseCaseImpl implements PaymentOrderUseCase {
    private final PaymentOrderRepositoryPort repository;
    @Override
    public PaymentOrder initiatePayment(PaymentOrder order) { ... }
}

@Mapper(componentModel = "spring")
public interface PaymentOrderRestMapper {
    @Mapping(target="externalId", source="externalReference")
    PaymentOrder toDomain(PaymentOrderInitiateRequest request);
}

## Correcciones manuales (del usuario)
- Se reforzó que los sufijos son inmutables: UseCaseImpl, RepositoryPort, RepositoryAdapter, etc.
- Se ajustó el nombre de ciertos paquetes para alinearse 100% a Screaming Architecture.
- Se agregó lógica real al dominio.
- Se estandarizó a inglés.
- Se implemento el adapter, el usecase y controller.

------------------------------------------------------------
4. GENERACIÓN DE TESTS UNITARIOS
------------------------------------------------------------

## Prompt
“Genera los tests para controller, use case, repository adapter y 
rest mapper. Usa JUnit5 + Mockito. Los tests deben validar 
mapping, service calls, HTTP status.”

## Resumen de la respuesta
La IA generó:
- Tests con MockMvc para controller
- Tests con Mockito para UseCaseImpl
- Tests para el RepositoryAdapter simulando JPA
- Tests para el Mapper validando cada campo

## Fragmento generado relevante
mockMvc.perform(post("/payment-initiation/payment-orders")
  .contentType(MediaType.APPLICATION_JSON)
  .content("{...}"))
  .andExpect(status().isCreated());

## Correcciones manuales
- Ajustes para validación automática de @Valid
- Manejo explícito de excepciones personalizadas
- Corrección del test de error 500


-----------------------------------------------------------
5. Docker
------------------------------------------------------------
## Prompt
“Con el Dockerfile, ¿en dónde debe ir? ¿Dentro de la carpeta del proyecto
Spring? El docker-compose, ¿lo pongo dentro del proyecto o creo una carpeta
/docker? Lo tengo así, recomiéndame cómo debe ir:

TechnicalTestBankingJava/
├── micro-payment-initiation/
│   ├── src/...
│   ├── Dockerfile
│   ├── pom.xml
│   └── application-docker.properties (¿para qué serviría?)
├── docker/
│   ├── docker-compose.yml
│   └── .env  (¿o mejor en raíz?)
└── .env
└── README

Adjunto mis archivos de referencia:
- Dockerfile (Java 17 → me pediste Java 21)
- docker-compose.yml con PostgreSQL 17
- Archivo .env con credenciales
- application.properties actual

Objetivo del prompt:
“Quiero la estructura correcta recomendada para proyectos empresariales
y que Docker use variables de entorno correctamente.”

## Resumen de la respuesta
La IA revisó la estructura propuesta y determinó:
- El `docker-compose.yml` debe ubicarse en la raíz del monorepo.
- El `Dockerfile` debe vivir dentro del módulo Spring (`/micro-payment-initiation`).
- El archivo `.env` debe estar en raíz y ser consumido por docker-compose.
- application-docker.properties solo se usa si se activa el perfil `docker`.
- Se recomendó actualizar imágenes a Java 21 y Postgres 17.
- Se propuso un flujo empresarial de build (builder + runtime) usando Temurin.

También se validaron los archivos enviados: Dockerfile, compose y `.env`.

## Fragmento generado relevante
TechnicalTestBankingJava/
├── micro-payment-initiation/
│   ├── Dockerfile
│   ├── src/...
│   └── pom.xml
├── docker-compose.yml
├── .env
└── README.md

Recomendación del modelo:
- “El docker-compose debe vivir en la raíz.  
   El Dockerfile dentro del módulo Spring.  
   Usa Java 21 y un `.env` único en raíz.”

## Correcciones manuales
- Ajusté la imagen base del Dockerfile (la IA sugirió Java 17, 
  pero yo confirmé que debía ser Java 21).
- Se corrigió el puerto del host para Postgres (5435).
- Se mantuvo mi decisión de no usar application-docker.properties
  en esta primera versión.
- Validé manualmente que mi estructura del proyecto funciona con el compose.