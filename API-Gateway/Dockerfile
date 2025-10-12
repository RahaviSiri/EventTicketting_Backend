# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:21-jdk-alpine
# Set the working directory in the container
WORKDIR /app
# Copy the application JAR file to the container
COPY target/API-Gateway-0.0.1-SNAPSHOT.jar app.jar
# Expose the port the application runs on
EXPOSE 8080
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
