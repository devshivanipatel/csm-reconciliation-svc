FROM eclipse-temurin:21-jre-alpine
COPY target/*.jar /tmp/app.jar
EXPOSE 8090
RUN chmod +x /tmp/app.jar
CMD ["java", "-jar", "/tmp/app.jar"]
USER spring:spring
