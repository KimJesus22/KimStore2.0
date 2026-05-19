# 1. Etapa de Construcción (Usamos Maven y Java 17 para compilar tu código)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 2. Etapa de Ejecución (Usamos una versión ligera de Java 17 para correr tu app)
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto que usa Spring Boot
EXPOSE 8080

# El comando final para arrancar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
