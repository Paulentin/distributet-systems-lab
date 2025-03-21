FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/hz-cluster-0.0.1-SNAPSHOT.jar /app/hz-cluster.jar

EXPOSE 8081
EXPOSE 5701


ENTRYPOINT ["java", "-jar", "/app/hz-cluster.jar"]