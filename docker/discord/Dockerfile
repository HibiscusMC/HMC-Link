# To create an image for the docker-compose file, run
# docker image build --label hmclink-discord .
# in this directory.

ARG DISCORD_RAM

FROM alpine as prebuild

# Create user and group and install git
RUN addgroup hmclink \
  && adduser -u 1000 -G hmclink -D -H discord \
  && apk -U upgrade \
  && apk add git \
  && git clone https://github.com/HibiscusMC/HMC-Link.git

FROM eclipse-temurin:17-alpine as build

# Build the jar
RUN mkdir hmclink && cd hmclink && mkdir build && cd build && mkdir HMCLink
COPY --from=prebuild /HMC-Link ./HMCLink/
RUN cd HMCLink && ./gradlew clean build

FROM eclipse-temurin:17-alpine
WORKDIR /hmclink/

# Create run folder
RUN mkdir run && cd run

# Copy jars
COPY --from=build /hmclink/build/HMCLink/backend/build/libs/backend-*.*.*.jar backend.jar

# Run jars
USER discord
ENTRYPOINT [ "java", "-Xmx${DISCORD_RAM}M", "-jar discord.jar" ]
