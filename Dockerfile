# Java version
FROM eclipse-temurin:17-jdk

# App directory
WORKDIR /app

# Copy jar
COPY target/*.jar app.jar

# Expose port (Render uses 8080 internally)
EXPOSE 8080

# Run app
ENTRYPOINT ["java","-jar","/app/app.jar"]
