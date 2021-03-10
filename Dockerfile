# =======================
# === BUILD TIME ========
# =======================
 
FROM maven:3.6.3-jdk-11 AS MAVEN_TOOL_CHAIN
COPY pom.xml /tmp/
COPY src /tmp/src/
WORKDIR /tmp/
RUN mvn package

 
# =======================
# === RUN TIME ==========
# =======================
 
FROM openjdk:11-jre
 
WORKDIR /app
 
COPY --from=MAVEN_TOOL_CHAIN /tmp/target/*.jar service.jar
 
ENTRYPOINT exec java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=${ENV:=local} -Xss256k -Xms512m -Xmx512m -jar service.jar
