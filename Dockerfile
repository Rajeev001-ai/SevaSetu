FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY . .

# Safety: Make mvnw executable
RUN chmod +x mvnw

# Build project
RUN ./mvnw clean package

# Copy jar
COPY target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]


