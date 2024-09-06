FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/SecureWebSocketServer.jar server.jar
COPY --from=build src/main/java/org/christu/secure/websocket/cert src/main/java/org/christu/secure/websocket/cert
EXPOSE 8443
ENTRYPOINT ["java","-jar","server.jar"]