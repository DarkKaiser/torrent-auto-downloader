# ------------------------------------------
# 1. Build Image
# ------------------------------------------
FROM openjdk:17 AS builder

WORKDIR /usr/src/app/

COPY ./src/TorrentAD ./src/TorrentAD

WORKDIR /usr/src/app/src/TorrentAD/

RUN chmod +x ./gradlew
RUN ./gradlew clean build

# ------------------------------------------
# 2. Production Image
# ------------------------------------------
FROM openjdk:17-jdk-slim-bullseye

COPY ./src/TorrentAD/docker-entrypoint.sh /docker-entrypoint/
RUN chmod +x /docker-entrypoint/docker-entrypoint.sh

WORKDIR /docker-entrypoint/dist/

COPY --from=builder /usr/src/app/src/TorrentAD/build/libs/torrentad-1.1.0.jar .

WORKDIR /usr/local/app/

ENTRYPOINT ["/docker-entrypoint/docker-entrypoint.sh"]
CMD ["java", "-jar", "/usr/local/app/torrentad-1.1.0.jar"]
