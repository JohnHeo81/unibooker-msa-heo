FROM eclipse-temurin:17-jre

WORKDIR /app

# 빌드된 JAR 복사
COPY build/libs/*.jar app.jar

EXPOSE 8081

ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]