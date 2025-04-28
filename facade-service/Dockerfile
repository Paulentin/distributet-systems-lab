FROM openjdk:17-slim

WORKDIR /app

COPY target/facade-service-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
