FROM eclipse-temurin:17-alpine as build
WORKDIR /usr/source/

RUN apk add git

RUN git clone https://github.com/HibiscusMC/HMC-Link.git .

# Build the jar
RUN chmod +x gradlew && ./gradlew :backend:build --no-daemon

FROM eclipse-temurin:17-alpine
WORKDIR /usr/backend/

# Copy jars
COPY --from=build /usr/source/backend/build/libs/backend-*.*.*.jar /usr/backend/backend.jar

ENTRYPOINT [ "java", "-jar", "backend.jar" ]