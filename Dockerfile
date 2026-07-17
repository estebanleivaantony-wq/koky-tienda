# Etapa 1: Construcción (Build) con Maven usando Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Compilamos el proyecto ignorando los tests para avanzar rápido
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final super ligera para ejecutar la app
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copiamos el archivo JAR generado (coincide con el artifactId de tu pom.xml)
COPY --from=build /app/target/clinica-santafe-jwt-0.0.1-SNAPSHOT.jar app.jar
# Exponemos el puerto 3001 que configuraste en tu application.yml
EXPOSE 3001
# Comando para iniciar tu app de Spring Boot
CMD ["java", "-jar", "app.jar"]
