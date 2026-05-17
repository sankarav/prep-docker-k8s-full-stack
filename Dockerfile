#FROM eclipse-temurin:26.0.1_8-jre-alpine
FROM eclipse-temurin:26-jdk-alpine

WORKDIR /app

COPY target/docker-k8s-0.0.1-SNAPSHOT.jar .

ENTRYPOINT ["java", "-jar", "docker-k8s-0.0.1-SNAPSHOT.jar"]