FROM eclipse-temurin:17-jdk
WORKDIR /app

# Build jar first using Maven
COPY . .

RUN ./mvnw clean package

# Copy jar to container
COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
