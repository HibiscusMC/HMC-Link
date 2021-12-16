ARG BACKEND_RAM

FROM alpine as prebuild
WORKDIR ~/hmclink

# Create user and group and install git
RUN addgroup hmclink \
  && adduser --uid 1000 -G hmclink discord \
  && apt-get update \
  && apt-get install git -y \
  && git clone https://github.com/HibiscusMC/HMC-Link.git

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

# Run jars
ENTRYPOINT [ "java", "-Xmx${BACKEND_RAM}M", "-jar backend.jar" ]

USER hmclink
