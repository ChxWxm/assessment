FROM amazoncorretto:17.0.9-alpine3.18

WORKDIR /app

ADD . .

RUN ["./gradlew","bootJar"]

EXPOSE 8888
ENTRYPOINT ["java", "-jar", "build/libs/posttest-0.0.1-SNAPSHOT.jar"]
