# To create an image for the docker-compose file, run
# docker image build --label hmclink-discord .
# in this directory.

ARG DISCORD_RAM

FROM alpine as prebuild
WORKDIR /hmclink/

# Create user and group and install git
RUN addgroup hmclink \
  && adduser -u 1000 -G hmclink -D -H discord \
  && apk -U upgrade \
  && apk add git \
  && git clone https://github.com/HibiscusMC/HMC-Link.git

FROM eclipse-temurin:17-alpine as build

# Build the jars
RUN mkdir "/hmclink/build/"
COPY --from=prebuild / /build/HMCLink
RUN cd /hmclink/build/HMCLink && ./gradlew clean build

# Create run folder
RUN cd /hmclink/ && mkdir run

FROM eclipse-temurin:17-alpine
WORKDIR /hmclink/run/

# Copy jars
COPY --from=build /hmclink/build/HMCLink/backend/build/libs/backend-*.*.*.jar backend.jar

# Run jars
USER discord
ENTRYPOINT [ "java", "-Xmx${DISCORD_RAM}M", "-jar discord.jar" ]
