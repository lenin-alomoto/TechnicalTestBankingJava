------------------------------------------------------------
1. OPENAPI — DECISIONES
------------------------------------------------------------
✔ Se aceptó el borrador generado por IA y luego se ajustaron formatos, 
  especialmente uso de BigDecimal en Amount.
✔ Se corrigió estructura de paths y responses para cumplir el estándar 3.0.3.
✔ Se eliminaron elementos no usados del YAML generado automáticamente.

------------------------------------------------------------
2. ARQUITECTURA HEXAGONAL — DECISIONES
------------------------------------------------------------
✔ Se mantuvieron los sufijos fijos definidos por el usuario:
  UseCase, UseCaseImpl, RepositoryPort, RepositoryAdapter, RestMapper,
  AdapterMapper, Request, Response, Entity, Util.

✔ Se respetó la convención base enviada por el usuario:
  micro-nombre-micro / nombre_modulo siguiendo Screaming Architecture.

✔ Se adoptó exactamente la estructura personalizada del usuario:
  domain/models, domain/usecase, domain/port
  application/mappers, application/validations
  infrastructure/adapter/entities, adapter/mappers, adapter/repositories
  infrastructure/rest/mappers, rest/requests, rest/responses, rest/controllers
  config/utilities

✔ Se reforzó el uso de inglés en nombres de clases y paquetes.

✔ Se incluyó RestMapper basado en MapStruct como aconsejado por buenas prácticas.

✔ Se eliminaron timestamps de tests del mapper porque son dinámicos.

✔ Se añadió lógica real de dominio manualmente (no generada por IA), tal como:
  - Validaciones internas
  - Proceso de creación de PaymentOrder
  - Uso de enums de estado
  - Manejo de UUID y fechas del dominio

✔ Se ajustó la estructura final del módulo payment/ para reflejar el estilo
  Screaming Architecture (el módulo grita “payment”).

✔ Se mantuvo la restricción del usuario: 
  “los sufijos son fijos y NO se cambian, solo se cambia lo que está antes”.

✔ Se reorganizó el mapper REST para alinear nombres: externalReference → externalId.

✔ Se revisaron los nombres de paquetes para evitar pluralización inconsistente.

------------------------------------------------------------
3. TESTING — DECISIONES
------------------------------------------------------------
✔ Se descartó generar pruebas automáticas poco fiables y se crearon pruebas
  manuales de:
    - Controller (MockMvc)
    - RepositoryAdapter
    - UseCaseImpl
    - Mapper

✔ Se eliminaron assertions sobre timestamps por ser volátiles.

✔ Se añadieron pruebas para errores:
    - 400 BadRequest (validación)
    - 500 errores inesperados
    - 404 NotFound

✔ Se mantuvo el principio de aislamiento: los tests de controller usan @WebMvcTest.

------------------------------------------------------------
4. DOCKER Y DEPLOY — DECISIONES
------------------------------------------------------------
(Decisiones actualizadas a partir de los archivos enviados por el usuario)

✔ Se revisaron los archivos enviados: Dockerfile, docker-compose.yml y .env.

✔ Se recomendó mover:
   - `docker-compose.yml` al **nivel raíz del monorepo**.
   - `Dockerfile` dentro del módulo SpringBoot `/micro-payment-initiation`.
   - `application-docker.properties` solo si se activa con:
     `--spring.profiles.active=docker`.

✔ El archivo `.env` se decidió colocar:
   → en raíz del proyecto, para que docker-compose lo use directamente.

✔ Se ajustó el Dockerfile para usar Java 21.

✔ Se recomendó **NO** usar mvnw dentro de Docker:
   → cambiar a `./mvnw` solo si existe en el proyecto,
     o usar `mvn -B` si está instalado en la imagen builder.

✔ Se adoptó un flujo estándar empresarial:
   1) Build → imagen builder con Maven
   2) Runtime → imagen liviana con `eclipse-temurin:21-jre`

✔ postgres se mantuvo con healthcheck óptimo.
✔ Se normalizó el uso de variables de entorno para despliegue real.
✔ Se alineó puerto de Postgres: contenedor(5432) → host(5435).

------------------------------------------------------------
5. IA — DECISIONES
------------------------------------------------------------
✔ Se creó la carpeta `ia/` como evidencia del proceso.
✔ prompts.md contiene el registro real de solicitudes del usuario.
✔ decisions.md documenta las decisiones técnicas tomadas.
✔ El README incluye referencias claras del uso asistido de IA.
