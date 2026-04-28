FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/donor-search-backend-*.jar /app/app.jar
RUN echo "jdk.tls.disabledAlgorithms=SSLv3, RC4, DES, MD5withRSA, DH keySize < 1024, EC keySize < 2048, anon, NULL, include jdk.disabled.namedCurves" > /app/custom.security
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.security.properties=/app/custom.security", "-jar", "app.jar"]
