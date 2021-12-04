FROM ubuntu as prebuild
WORKDIR /home/hmclink

# Install git
RUN apt-get update
RUN apt-get install -y git

# Add credentials on build
RUN mkdir /root/.ssh/
RUN echo "${SSH_PRIVATE_KEY}" > /root/.ssh/id_rsa

# Add GitHub to known hosts
RUN touch /root/.ssh/known_hosts
RUN ssh-keyscan github.com >> /root/.ssh/known_hosts

RUN git clone "${GITHUB_USER}"@github.com:HibiscusMC/HMCLink.git

FROM eclipse-temurin:17

# Build the jars
RUN mkdir "/build/"
COPY --from=prebuild /HMCLink /build/HMCLink
RUN cd /build/HMCLink
RUN ./gradlew clean build

# Create run folder
RUN cd /
RUN mkdir "/run/"

# Copy jars
COPY /build/HMCLink/backend/build/libs/backend-*.*.*.jar /run/backend.jar
COPY /build/HMCLink/discord-bot/build/libs/discord-bot-*.*.*.jar /run/discord.jar
RUN cd /run/

# Run jars
RUN java -Xmx"${BACKEND_RAM}"M -jar backend.jar
RUN java -Xmx"${DISCORD_RAM}"M -jar discord.jar