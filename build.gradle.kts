plugins {
    java
    id("com.gradleup.shadow") version "8.3.1"
}

group = "com.github.lukesky19"
version = "1.1.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://maven.citizensnpcs.co/repo") {
        name = "citizens-repo"
    }
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") {
        name = "PlaceholderAPI Repo"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly("net.kyori:adventure-api:4.17.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.17.0")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("net.citizensnpcs:citizens-main:2.0.35-SNAPSHOT") {
        exclude("*", "*")
    }
    implementation("org.spongepowered:configurate-yaml:4.1.2")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.shadowJar {
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }
    archiveClassifier.set("")
    relocate("org.spongepowered.configurate", "com.github.lukesky19.skyleaderboards.libs.configurate")
    minimize()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
