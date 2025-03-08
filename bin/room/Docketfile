FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/room-service.jar room-service.jar
EXPOSE 3002
ENTRYPOINT ["java", "-jar", "room-service.jar"]