FROM maven:3.9.16-eclipse-temurin-25-alpine AS builder
WORKDIR /app

COPY pom.xml .
COPY user-service/pom.xml user-service/pom.xml
COPY notification-service/pom.xml notification-service/pom.xml
COPY eureka-server/pom.xml eureka-server/pom.xml
COPY config-server/pom.xml config-server/pom.xml
COPY gateway/pom.xml gateway/pom.xml

RUN mvn -q -pl user-service -am dependency:go-offline

COPY user-service/src user-service/src

RUN mvn -q -pl user-service -am package -DskipTests

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

COPY --from=builder /app/user-service/target/*.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]