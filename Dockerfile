FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY . .

# Make mvnw executable inside container
RUN chmod +x mvnw

# Build jar
RUN ./mvnw clean package

# Copy jar to final location
COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
