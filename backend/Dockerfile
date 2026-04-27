# ── Build stage ─────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Cache Maven dependencies
COPY pom.xml .
RUN apk add --no-cache maven && mvn dependency:go-offline -q

COPY src ./src
RUN mvn clean package -DskipTests -q

# ── Runtime stage ────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S apuf && adduser -S apuf -G apuf
USER apuf

COPY --from=build /app/target/apuf-backend-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
