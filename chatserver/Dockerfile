
# Multi-stage build for smaller image
FROM gradle:7.6-jdk17 AS build

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY src ./src

RUN gradle build -x test --no-daemon

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

# SPRING_PROFILES_ACTIVE is set via environment variable in task-definition.json
ENTRYPOINT ["java", "-jar", "app.jar"]
