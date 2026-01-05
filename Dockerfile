# ---------------------------
# Stage 1: Build the application
# ---------------------------
FROM eclipse-temurin:21-jdk AS build

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first (for dependency caching)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Give execution permission to mvnw
RUN chmod +x mvnw

# Download dependencies (optional cache step)
RUN ./mvnw dependency:go-offline -B

# Copy the rest of the source code
COPY src src

# Build the project and skip tests
RUN ./mvnw clean package -DskipTests

# ---------------------------
# Stage 2: Create the final runtime image
# ---------------------------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (change if your app uses a different one)
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java","-jar","app.jar"]

ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
