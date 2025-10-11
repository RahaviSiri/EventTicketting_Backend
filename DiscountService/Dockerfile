FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/DiscountService-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app.jar"]

