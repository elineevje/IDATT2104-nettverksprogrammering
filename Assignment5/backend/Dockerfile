FROM maven:3.8.3-openjdk-17-slim AS builder

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim AS finalApp

RUN apt-get update && \
    apt-get install -y python3 && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8090

CMD ["java", "-jar", "app.jar"]
