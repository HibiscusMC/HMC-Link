plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.serialization") version "1.5.31"
}

group = "de.skyslycer.hmclink"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))

    implementation("io.ktor:ktor-server-core:1.6.4")
    implementation("io.ktor:ktor-server-cio:1.6.4")

    implementation("io.ktor:ktor-client-core:1.6.4")
    implementation("io.ktor:ktor-client-cio:1.6.4")
    implementation("io.ktor:ktor-client-serialization:1.6.4")

    implementation("org.apache.commons:commons-lang3:3.12.0")

    implementation("ch.qos.logback:logback-classic:1.2.5")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.10")

    implementation("org.jetbrains.exposed:exposed-core:0.34.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.34.1")

    implementation("org.postgresql:postgresql:42.2.19")
}