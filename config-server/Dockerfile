FROM openjdk:17-slim

WORKDIR /app

COPY target/config-server-*.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]