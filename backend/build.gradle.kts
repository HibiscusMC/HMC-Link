val ktorVersion = "1.6.4"
val exposedVersion = "0.34.1"

plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.serialization") version "1.5.31"

    id("com.github.johnrengelman.shadow") version "7.1.1"
}

group = "de.skyslycer.hmclink"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))

    implementation(kotlin("stdlib"))

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-cio:$ktorVersion")

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")

    implementation("org.apache.commons:commons-lang3:3.12.0")

    implementation("ch.qos.logback:logback-classic:1.2.5")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.10")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    implementation("org.postgresql:postgresql:42.2.19")
}

tasks {
    build {
        dependsOn("shadowJar")
    }

    shadowJar {
        minimize()
        val classifier: String? = null
        archiveClassifier.set(classifier)
    }

    jar {
        manifest {
            attributes["Main-Class"] = "de.skyslycer.hmclink.backend.HMCLinkBackendKt"
        }
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}