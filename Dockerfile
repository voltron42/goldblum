# Stage 1: Build stage
FROM clojure:temurin-21-lein-2.11.2 as builder

WORKDIR /app

# Copy project files
COPY project.clj .
COPY src/ ./src/
COPY resources/ ./resources/

# Build uberjar
RUN lein uberjar

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the uberjar from builder
COPY --from=builder /app/target/goldblum-*-standalone.jar ./app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
