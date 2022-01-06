# To create an image for the docker-compose file, run
# docker image build --label hmclink-backend .
# in this directory.

ARG BACKEND_RAM

FROM eclipse-temurin:17 as build
WORKDIR /

# Get repository
RUN git clone https://github.com/HibiscusMC/HMC-Link.git

# Build the jar
RUN cd HMC-Link && chmod +x gradlew && ./gradlew :backend:build

FROM eclipse-temurin:17-alpine
WORKDIR /usr/backend/

# Create run folder
RUN mkdir run && cd run

# Copy jars
COPY --from=build /HMC-Link/backend/build/libs/backend-*.*.*.jar backend.jar

# Add the user
RUN adduser -u 1000 -D -H backend

# Run jars
USER backend
ENTRYPOINT [ "java", "-Xmx${BACKEND_RAM}M", "-jar backend.jar" ]