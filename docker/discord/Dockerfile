FROM eclipse-temurin:17-alpine as build
WORKDIR /usr/source/

RUN apk add git --no-cache && \
    git clone https://github.com/HibiscusMC/HMC-Link.git . && \
    chmod +x gradlew && ./gradlew :discord-bot:build --no-daemon

FROM eclipse-temurin:17-alpine
WORKDIR /usr/discord/

COPY --from=build /usr/source/discord-bot/build/libs/discord-bot-*.*.*.jar /usr/discord/discord.jar

ENTRYPOINT [ "java", "-jar", "discord.jar" ]