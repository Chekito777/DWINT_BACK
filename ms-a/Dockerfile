# Etapa 1: Build con Maven y JDK 21
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

# Copiar POM primero para cache de dependencias
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline || true

# Copiar código fuente
COPY src src
RUN ./mvnw clean package -DskipTests -Dspring-boot.repackage.skip=false

# Etapa 2: Imagen final ligera con JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /workspace/target/*.jar app.jar

# Variables de entorno por defecto (se pueden sobrescribir)
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV SPRING_PROFILES_ACTIVE=default

EXPOSE 8081
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
