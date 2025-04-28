FROM openjdk:17-slim

WORKDIR /app

COPY target/logging-service-*.jar app.jar

EXPOSE 8081 9090

ENTRYPOINT ["java", "-jar", "app.jar"]