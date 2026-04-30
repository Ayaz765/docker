FROM maven:3.9.6-eclipse-temurin-17

WORKDIR /app

# Copy all project files
COPY . .

# Build project (skip tests during build)
RUN mvn clean install -DskipTests

# Run tests when container starts
CMD ["mvn", "test"]