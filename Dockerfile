# multi stage build
FROM eclipse-temurin:26-jdk-alpine AS builder

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src/ src/
RUN ./mvnw package -DskipTests

# deployment image
FROM eclipse-temurin:26-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/full-stack-backend-k8s.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]