# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia POMs
COPY pom.xml .
COPY servico-usuario/pom.xml servico-usuario/
COPY servico-pedido/pom.xml servico-pedido/

# Download dependencies
RUN mvn dependency:go-offline -pl servico-usuario

# Copia código fonte
COPY servico-usuario/src servico-usuario/src

# Build
RUN mvn clean package -pl servico-usuario -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia JAR do build stage
COPY --from=build /app/servico-usuario/target/*.jar app.jar

# Expõe porta
EXPOSE 8081

# Variáveis de ambiente
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8081/health || exit 1

# Executa aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
