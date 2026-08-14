# Build stage: compila todos los servicios
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw mvnw.cmd pom.xml ./
RUN ./mvnw dependency:go-offline -pl ms-a,ms_b,api-gateway,authserver,idgs15 -am || true

COPY ms-a ms-a
COPY ms_b ms_b
COPY api-gateway api-gateway
COPY authserver authserver
COPY idgs15 idgs15

RUN ./mvnw clean package -pl ms-a,ms_b,api-gateway,authserver,idgs15 -am -DskipTests -Dspring-boot.repackage.skip=false

# Runtime stage: supervisord + JARs
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache supervisor bash

# Copiar JARs compilados
COPY --from=builder /workspace/ms-a/target/*.jar ms-a.jar
COPY --from=builder /workspace/ms_b/target/*.jar ms-b.jar
COPY --from=builder /workspace/api-gateway/target/*.jar api-gateway.jar
COPY --from=builder /workspace/authserver/target/*.jar authserver.jar
COPY --from=builder /workspace/idgs15/target/*.jar idgs15.jar

# Config supervisord
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

EXPOSE 8761 8080 8081 8082 8083

CMD ["supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]