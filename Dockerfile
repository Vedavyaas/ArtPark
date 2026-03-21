# Stage 1: Build the Java Spring Boot Backend
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Final Image with Python and Java
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Install Python 3 and pip
RUN apt-get update && apt-get install -y python3 python3-pip && rm -rf /var/lib/apt/lists/*

# Copy built Java JAR from the builder stage
COPY --from=builder /app/target/*.jar ./app.jar

# Copy Python backend
COPY python-ml-backend ./python-ml-backend

# Install Python dependencies (we use break-system-packages for jammy to avoid venv overhead in docker)
RUN pip3 install --no-cache-dir --break-system-packages -r python-ml-backend/requirements.txt || \
    pip3 install --no-cache-dir -r python-ml-backend/requirements.txt

# Create a shell script to run both applications concurrently
RUN echo '#!/bin/bash\n\
# Start Java App in background\n\
java -jar /app/app.jar &\n\
\n\
# Start Python App in foreground\n\
cd /app/python-ml-backend && python3 main.py &\n\
\n\
# Wait for any process to exit\n\
wait -n\n\
exit $?\n\
' > start.sh && chmod +x start.sh

# Expose Java (8092) and Python (8000) ports
EXPOSE 8092 8000

# Start both applications
CMD ["./start.sh"]
