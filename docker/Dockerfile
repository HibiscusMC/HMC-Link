ARG BACKEND_RAM
ARG DISCORD_RAM

FROM alpine as prebuild
WORKDIR ~/hmclink

# Create user and group and install git
RUN groupadd -g 1000 hmclink \
  && useradd -r -u 1000 -g 1000 -m hmclink \
  && apt-get update \
  && apt-get install -y git \
  && git clone git://github.com/HibiscusMC/HMC-Link.git

FROM eclipse-temurin:17-alpine as build

# Build the jars
RUN mkdir "~/build/"
COPY --from=prebuild /HMCLink /build/HMCLink
RUN cd ~/build/HMCLink && ./gradlew clean build

# Create run folder
RUN cd ~/ && mkdir ~/run/

FROM eclipse-temurin:17-alpine
WORKDIR ~/hmclink/run/

# Copy jars
COPY --from=build /build/HMCLink/backend/build/libs/backend-*.*.*.jar backend.jar
COPY --from=build /build/HMCLink/discord-bot/build/libs/discord-bot-*.*.*.jar discord.jar

# Run jars
ENTRYPOINT [ "java", "-Xmx${BACKEND_RAM}M", "-jar backend.jar" ]
ENTRYPOINT [ "java", "-Xmx${DISCORD_RAM}M", "-jar discord.jar" ]

USER hmclink
