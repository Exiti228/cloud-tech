FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /usr/src/app/
COPY mvnw pom.xml /usr/src/app/
COPY src /usr/src/app/src
COPY .mvn /usr/src/app/.mvn
RUN dos2unix mvnw
RUN ./mvnw -DskipTests clean package


FROM eclipse-temurin:17-jre-alpine

COPY --from=builder /usr/src/app/target/*.jar /server.jar
CMD ["java", "-jar", "/server.jar"]