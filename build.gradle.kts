import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.xhyrom.peddlerspocket"
version = "1.1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.17-R0.1-SNAPSHOT")

    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")

    implementation("dev.jorel:commandapi-bukkit-shade:9.5.1")
    implementation("net.kyori:adventure-text-minimessage:4.14.0")
}


tasks {
    named<Jar>("jar") {
        archiveClassifier.set("unshaded")
    }
    shadowJar {
        configureRelocations()
        archiveClassifier.set("")
    }
    named("build") {
        dependsOn(shadowJar)
    }
    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(sourceSets.main.get().resources.srcDirs) {
            filter(
                org.apache.tools.ant.filters.ReplaceTokens::class, "tokens" to mapOf(
                    "name" to project.name,
                    "version" to project.version,
                )
            )
        }
    }
}

fun ShadowJar.configureRelocations() {
    relocate("dev.jorel.commandapi", "dev.xhyrom.peddlerspocket.libs.commandapi")
}

publishing {
    publications.create<MavenPublication>("maven") {
        artifact(tasks["shadowJar"])


        repositories.maven {
            url = uri("https://repo.jopga.me/releases")

            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }

        groupId = rootProject.group as String
        artifactId = project.name
        version = rootProject.version as String

        pom {
            name.set("PeddlersPocket")
        }
    }
}