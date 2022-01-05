plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.serialization") version "1.5.31"
}

group = "de.skyslycer.hmclink"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(kotlin("stdlib"))

    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")

    api("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.3.0")

    api("redis.clients:jedis:3.7.0")
}