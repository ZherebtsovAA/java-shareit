FROM amazoncorretto:11-alpine-jdk
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} shareIt-server.jar
ENTRYPOINT ["java", "-jar", "/shareIt-server.jar"]