FROM maven:3.8.4-openjdk-17 AS builder
WORKDIR /
COPY ./pom.xml .
RUN mvn dependency:go-offline
COPY ./src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-alpine
WORKDIR /
ARG JAR_FILE=target/*.jar
COPY --from=builder ${JAR_FILE} ./gateway-service.jar
ENTRYPOINT ["java", "-jar", "gateway-service.jar"]