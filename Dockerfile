
# Multi-stage build for smaller image
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY gradlew gradlew.bat ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
COPY src ./src

RUN chmod +x gradlew && ./gradlew build -x test --no-daemon

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

# SPRING_PROFILES_ACTIVE is set via environment variable in task-definition.json
ENTRYPOINT ["java", "-jar", "app.jar"]
