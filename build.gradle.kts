plugins {
    kotlin("jvm") version "2.2.20"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    `maven-publish`
}

group = "dev.vhoyd"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly("net.dmulloy2:ProtocolLib:5.4.0")
}

tasks {
    runServer {
        minecraftVersion("1.21.8")
    }
    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set("Blockworks")
        archiveVersion.set("1.0-SNAPSHOT")
        minimize()
        doLast {
            println("Shadow jar built at: ${archiveFile.get().asFile.absolutePath}")
        }
    }
}
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                artifact(tasks.named("shadowJar")) {
                    builtBy(tasks.named("shadowJar"))
                }
                println("PUSHED TO MVN")
            }
        }
    }
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.jar {
    enabled = false

}


val exportPluginThingy by tasks.registering(Copy::class) {
    dependsOn(tasks.build)
    from(tasks.shadowJar)
    into(File(System.getProperty("user.home"), "Desktop/Paper 1.21.8 server/plugins/"))
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
