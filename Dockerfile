FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /workspace
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

COPY src ./src
RUN mvn -B -ntp clean package -DskipTests \
    && mvn -B -ntp dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory=target/dependency

FROM mcr.microsoft.com/openjdk/jdk:25-azurelinux AS runtime

WORKDIR /app
COPY --from=build /workspace/target/projeto-xadrez-1.0.0.jar app.jar
COPY --from=build /workspace/target/dependency dependency

USER app
ENTRYPOINT ["java", "-cp", "app.jar:dependency/*", "com.chess.App"]
