# Stage 1: Build the application
FROM debian:latest AS builder
RUN apt-get update && apt-get install -y openjdk-21-jdk maven
WORKDIR /app
COPY ./src ./src
COPY ./pom.xml .
RUN mvn clean package

# Stage 2: Create the runtime image
FROM openjdk:21-jdk-slim
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY --from=builder /app/${JAR_FILE} voiture-service.jar
ENTRYPOINT ["java", "-jar", "/voiture-service.jar"]