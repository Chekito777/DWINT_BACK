# Build stage: compila ms-a y ms-b
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace
COPY . .
RUN chmod +x ms-a/mvnw ms_b/mvnw
RUN cd ms-a && ./mvnw clean package -DskipTests -Dspring-boot.repackage.skip=false
RUN cd ms_b && ./mvnw clean package -DskipTests -Dspring-boot.repackage.skip=false

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /workspace
RUN apk add --no-cache supervisor bash
COPY --from=builder /workspace/ms-a/target/*.jar ms-a/
COPY --from=builder /workspace/ms_b/target/*.jar ms_b/
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf
CMD ["supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]
