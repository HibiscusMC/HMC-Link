# To create an image for the docker-compose file, run
# docker image build --label hmclink-backend .
# in this directory.

ARG BACKEND_RAM

FROM alpine as prebuild

# Create user and group and install git
RUN  apk -U upgrade \
  && apk add git \
  && git clone https://github.com/HibiscusMC/HMC-Link.git

FROM eclipse-temurin:17-alpine as build
WORKDIR /

# Build the jar
RUN mkdir hmclink && cd hmclink && mkdir build && cd build && mkdir HMCLink
COPY --from=prebuild /HMC-Link ./HMCLink/
RUN cd HMCLink && chmod +x gradlew && ./gradlew :backend:build

FROM eclipse-temurin:17-alpine
WORKDIR /

# Create run folder
RUN mkdir run && cd run

# Copy jars
COPY --from=build /hmclink/build/HMCLink/backend/build/libs/backend-*.*.*.jar backend.jar

RUN adduser -u 1000 -G hmclink -D -H backend

# Run jars
USER backend
ENTRYPOINT [ "java", "-Xmx${BACKEND_RAM}M", "-jar backend.jar" ]
