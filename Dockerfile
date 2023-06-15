FROM eclipse-temurin:19.0.2_7-jre-jammy
ADD target/Oak-1.0-SNAPSHOT.jar oak.jar
ENTRYPOINT ["java", "-jar","oak.jar"]
EXPOSE 8080
