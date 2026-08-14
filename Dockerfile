# Build stage: compila todos los servicios
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

# Copiar todo el código fuente
COPY . .

# Compilar cada módulo desde su directorio
RUN cd ms-a && ./mvnw clean package -DskipTests -Dspring-boot.repackage.skip=false
RUN cd ms_b && ./mvnw clean package -DskipTests -Dspring-boot.repackage.skip=false
RUN cd api-gateway && ./mvnw clean package -DskipTests -Dspring-boot.repackage.skip=false
RUN cd authserver && ./mvnw clean package -DskipTests -Dspring-boot.repackage.skip=false
RUN cd idgs15 && ./mvnw clean package -DskipTests -Dspring-boot.repackage.skip=false

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