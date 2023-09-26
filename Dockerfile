# Build stage with Maven
FROM maven:3.8.3-eclipse-temurin-17 as build
WORKDIR /workspace

ARG ACTOR
ARG GITHUB_TOKEN

# Copy the pre-generated settings.xml with placeholders
COPY settings.xml /root/.m2/settings.xml

# Replace placeholders with actual values
RUN sed -i "s/\${ACTOR}/${ACTOR}/g" /root/.m2/settings.xml
RUN sed -i "s/\${GITHUB_TOKEN}/${GITHUB_TOKEN}/g" /root/.m2/settings.xml

# Copy the pom.xml
COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src /workspace/src

RUN mvn clean install

# Extract layers from the built jar
FROM eclipse-temurin:17 as builder
WORKDIR extracted
COPY --from=build /workspace/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# Application stage
FROM eclipse-temurin:17-jre
WORKDIR application
COPY --from=builder extracted/dependencies/ ./
COPY --from=builder extracted/spring-boot-loader/ ./
COPY --from=builder extracted/snapshot-dependencies/ ./
COPY --from=builder extracted/application/ ./

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
