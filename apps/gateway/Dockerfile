FROM eclipse-temurin:17-jre-alpine AS builder
# Copy and extract JAR
WORKDIR /builder

# Application's jar file(defined by dockerfile-maven-plugin)
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy layers individually for better caching
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
