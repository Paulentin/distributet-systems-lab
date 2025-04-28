FROM openjdk:17-slim

WORKDIR /app

COPY target/message-service-*.jar app.jar

EXPOSE 8082 8083

ENTRYPOINT ["java", "-jar", "app.jar"]