FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/UserService-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "app.jar"]