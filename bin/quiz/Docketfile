FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/quiz-service.jar quiz-service.jar
EXPOSE 3001
ENTRYPOINT ["java", "-jar", "quiz-service.jar"]