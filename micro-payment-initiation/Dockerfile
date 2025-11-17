# ---------- BUILD STAGE ----------
FROM eclipse-temurin:21-jdk-noble AS builder

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src ./src

RUN chmod +x mvnw

RUN ./mvnw -q -DskipTests clean package

# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:21-jre-noble

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
