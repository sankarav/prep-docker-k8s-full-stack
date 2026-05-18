#FROM eclipse-temurin:26.0.1_8-jre-alpine
FROM eclipse-temurin:26-jdk-alpine AS builder

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src/ src/
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:26-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/docker-k8s-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]