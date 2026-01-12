# Stage 1: Build the application
FROM maven:3.9.4-eclipse-temurin-17 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven project files
COPY pom.xml ./
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --destination application

# Stage 2: Create the final runtime container
FROM eclipse-temurin:17-jre

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=builder /app/application/dependencies/ ./
COPY --from=builder /app/application/snapshot-dependencies/ ./
COPY --from=builder /app/application/spring-boot-loader/ ./
COPY --from=builder /app/application/application/ ./

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-Xmx96m", "-jar", "application.jar"]