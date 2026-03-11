plugins {
    kotlin("jvm") version "2.2.20"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("org.jetbrains.dokka") version "2.1.0"
    `maven-publish`
}

val targetJavaVersion = 21
group = "dev.vhoyd"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
}


dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}


afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                artifact(tasks.jar) {
                    builtBy(tasks.jar)
                }
            }
        }
    }
}

kotlin {
    jvmToolchain(targetJavaVersion)

}

tasks {

    processResources {

        val props = mapOf("version" to project.version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    jar {
        archiveClassifier.set("")
        archiveBaseName.set(project.name)
        archiveVersion.set(project.version.toString())
    }
}



// Hacked this together just so that I can test the plugin quicker without needing a file manager open.
val export by tasks.registering(Copy::class) {
    dependsOn(listOf(tasks.build, tasks.jar, tasks.publishToMavenLocal))
    from(tasks.jar)
    into(File(System.getProperty("user.home"), "Desktop/Paper 1.21.11 server/plugins/"))
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
