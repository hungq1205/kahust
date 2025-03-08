FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/api-gateway.jar api-gateway.jar
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]