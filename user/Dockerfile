FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/user-service.jar user-service.jar
EXPOSE 3003
ENTRYPOINT ["java", "-jar", "user-service.jar"]