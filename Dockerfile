FROM gradle:7.1.0-jdk11-openj9 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build test --no-daemon

FROM adoptopenjdk:11-jre-openj9
EXPOSE 8080
RUN mkdir /app
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/SpringRESTExample-0.0.1-SNAPSHOT.jar /app/SpringRESTExample-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "SpringRESTExample-0.0.1-SNAPSHOT.jar"]