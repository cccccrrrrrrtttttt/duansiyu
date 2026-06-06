FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY shusixue-backend/pom.xml ./
RUN mvn dependency:go-offline -B -q
COPY shusixue-backend/src ./src
RUN mvn package -DskipTests -B -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
CMD ["java", "-jar", "app.jar"]
