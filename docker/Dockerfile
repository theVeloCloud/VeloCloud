# syntax=docker/dockerfile:1

FROM --platform=$BUILDPLATFORM eclipse-temurin:21-jdk AS build

WORKDIR /workspace

COPY gradle/ gradle/
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY settings.gradle.kts build.gradle.kts gradle.properties ./

COPY agent/ agent/
COPY common/ common/
COPY platforms/ platforms/
COPY updater/ updater/
COPY launcher/ launcher/

RUN chmod +x ./gradlew
RUN ./gradlew --no-daemon :launcher:jar

FROM eclipse-temurin:21-jre

WORKDIR /cloud

ARG VELOCLOUD_VERSION=dev
ENV VELOCLOUD_VERSION=$VELOCLOUD_VERSION

# Default to talking to a mounted Docker socket; can be overridden at runtime.
ENV DOCKER_HOST=unix:///var/run/docker.sock

COPY --from=build /workspace/launcher/build/libs/velocloud-launcher.jar /cloud/velocloud-launcher.jar

# Stop-Signal explizit setzen
STOPSIGNAL SIGTERM

EXPOSE 8932

ENTRYPOINT ["java", "-jar", "/cloud/velocloud-launcher.jar"]
