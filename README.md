# Distributed Systems Lab

This project is a multi-module Spring Boot application demonstrating various aspects of distributed systems, including service discovery, configuration management, distributed logging, and distributed data storage.

## Project Overview

The project consists of the following modules:

- **config-server**: Configuration server for the distributed system
- **facade-service**: Frontend service that interfaces with other services
- **logging-service**: Service for centralized logging
- **message-service**: Service for message handling
- **hz-cluster**: Hazelcast cluster configuration for distributed data storage

## Prerequisites

- Java 21
- Maven 3.6+

## Setup Instructions

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd distributed-systems-lab
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

## Launching the Project

The services should be started in the following order:

1. Start the config-server:
   ```bash
   cd config-server
   mvn spring-boot:run
   ```

2. Start the hz-cluster:
   ```bash
   cd hz-cluster
   mvn spring-boot:run
   ```

3. Start the logging-service:
   ```bash
   cd logging-service
   mvn spring-boot:run
   ```

4. Start the message-service:
   ```bash
   cd message-service
   mvn spring-boot:run
   ```

5. Start the facade-service:
   ```bash
   cd facade-service
   mvn spring-boot:run
   ```

## Testing

### Running Tests

To run all tests:
```bash
mvn test
```

To run tests for a specific module:
```bash
mvn test -pl <module-name>
```

Example:
```bash
mvn test -pl logging-service
```

To run a specific test class:
```bash
mvn test -pl <module-name> -Dtest=<TestClassName>
```

Example:
```bash
mvn test -pl logging-service -Dtest=LoggingServiceTest
```

### Test Coverage

The project includes the following tests:

- **config-server**: Tests for the configuration server
  - ConfigControllerTest: Tests the configuration controller

- **facade-service**: Tests for the facade service
  - FacadeControllerTest: Tests the facade controller

- **logging-service**: Tests for the logging service
  - LoggingServiceTest: Tests the REST logging service
  - GrpcLoggingServiceTest: Tests the gRPC logging service

- **message-service**: Tests for the message service
  - MessageServiceTest: Tests the message service

- **hz-cluster**: Tests for the Hazelcast cluster
  - HzClientServiceTest: Tests the Hazelcast client service
  - HazelcastConfigTest: Tests the Hazelcast configuration

## API Endpoints

### Facade Service
- POST `/message`: Sends a message to the logging service
- GET `/messages`: Retrieves all messages from the logging and message services

### Logging Service
- GET `/messages`: Retrieves all messages from the logging service

### Message Service
- GET `/messages`: Retrieves a static message (not implemented yet)

### Hz-Cluster
- GET `/hz-test`: Tests the Hazelcast cluster with a simple counter
- GET `/hz-test-pesim`: Tests the Hazelcast cluster with pessimistic locking
- GET `/hz-test-optimistic`: Tests the Hazelcast cluster with optimistic locking

## Architecture

The project demonstrates a microservices architecture with the following components:

1. **Service Discovery**: The config-server provides service discovery for other services.
2. **API Gateway**: The facade-service acts as an API gateway, routing requests to appropriate services.
3. **Distributed Logging**: The logging-service provides centralized logging for all services.
4. **Distributed Data Storage**: The hz-cluster provides distributed data storage using Hazelcast.
5. **Circuit Breaking**: The facade-service implements circuit breaking using Resilience4j.

## Technologies Used

- Spring Boot
- Hazelcast
- gRPC
- Resilience4j
- JUnit 5
- Mockito
