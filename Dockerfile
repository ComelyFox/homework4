# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /project

COPY pom.xml .
COPY ./user-service/pom.xml ./user-service/
COPY ./user-service/src ./user-service/src

RUN mvn clean package -DskipTests -pl user-service -am

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=builder /project/user-service/target/*.jar app.jar

EXPOSE 8082
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]