FROM gradle:8-jdk17
WORKDIR /app

COPY . .
RUN gradle clean bootJar --no-daemon -x test \
 && cp build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]