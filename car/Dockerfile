FROM maven:3.8.4-openjdk-17 AS builder
WORKDIR /
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests


FROM openjdk:17-jdk-alpine
WORKDIR /
ARG JAR_FILE=target/*.jar
COPY --from=builder ${JAR_FILE} ./car-service.jar
ENTRYPOINT ["java", "-jar", "car-service.jar"]