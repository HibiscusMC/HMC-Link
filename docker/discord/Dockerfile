# To create an image for the docker-compose file, run
# docker image build --label hmclink-discord .
# in this directory.

ARG DISCORD_RAM

FROM eclipse-temurin:17-alpine as build
WORKDIR /

# Get repository
RUN  apk -U upgrade \
  && apk add git \
  && git clone https://github.com/HibiscusMC/HMC-Link.git

# Build the jar
RUN cd HMC-Link && chmod +x gradlew && ./gradlew :discord-bot:build

FROM eclipse-temurin:17-alpine
WORKDIR /usr/discord/

# Create run folder
RUN mkdir run && cd run

# Copy jars
COPY --from=build /HMC-Link/discord-bot/build/libs/discord-bot-*.*.*.jar discord.jar

# Add the user
RUN adduser -u 1000 -D -H discord

# Run jars
USER discord
ENTRYPOINT [ "java", "-Xmx${DISCORD_RAM}M", "-jar discord.jar" ]