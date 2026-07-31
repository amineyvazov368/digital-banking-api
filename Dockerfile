# 1. Mərhələ: Layihəni compile etmək
FROM gradle:8.5-jdk21-alpine AS builder

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN ./gradlew bootJar -x test

# 2. Mərhələ: Yalnız .jar faylını işə salmaq
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]