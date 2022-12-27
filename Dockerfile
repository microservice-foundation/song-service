FROM openjdk:11-jdk as base
MAINTAINER Yusuf Murodov "yusuf.murodov1@gmail.com"
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

FROM base as development
CMD ["./gradlew", "bootRun", "-Dspring.profiles.active=dev", "-Dspring-boot.run.jvmArguments='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8001'"]

FROM base as build
RUN ./gradlew jar

FROM openjdk:11-jre-slim as production
COPY --from=build /app/build/libs/song-service-*.jar /song-service.jar
CMD ["java", "-Dspring.profiles.active=prod", "-jar", "/song-service.jar"]
