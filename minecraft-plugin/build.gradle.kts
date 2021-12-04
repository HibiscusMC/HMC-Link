import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val shadePattern = "de.skyslycer.hmclink.plugin.shade"
val commandApiVersion = "6.3.1"

plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("kapt") version "1.5.30"
    kotlin("plugin.serialization") version "1.5.31"

    id("com.github.johnrengelman.shadow") version "7.0.0"

    id("de.nycode.spigot-dependency-loader") version "1.1.1"
}

group = "de.skyslycer.hmclink"
version = "0.0.1"

repositories {
    mavenCentral()

    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://jitpack.io/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    implementation(project(":common"))

    spigot(kotlin("stdlib"))
    spigot("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")

    spigot("net.axay:kspigot:1.17.4")
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")

    implementation("dev.jorel.CommandAPI:commandapi-shade:$commandApiVersion")

    compileOnly("me.clip:placeholderapi:2.10.10")

    implementation("net.kyori:adventure-text-minimessage:4.1.0-SNAPSHOT")
}

tasks {
    withType<KotlinCompile>().all {
        kotlinOptions {
            jvmTarget = "16"
        }
    }

    build {
        dependsOn("shadowJar")
    }

    shadowJar {
        val classifier: String? = null
        archiveClassifier.set(classifier)

        relocate("dev.jorel.commandapi", "$shadePattern.commandapi")
        relocate("net.kyori.adventure", "$shadePattern.adventure")
    }

    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            filesMatching("plugin.yml") {
                expand(
                    "version" to project.version,
                    "name" to project.name
                )
            }

            filesMatching("version") {
                expand(
                    "version" to project.version,
                )
            }
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }
}