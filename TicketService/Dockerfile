# ==============================
#  Stage 1: Build the JAR
# ==============================
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ==============================
#  Stage 2: Create runtime image
# ==============================
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/TicketService-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]
