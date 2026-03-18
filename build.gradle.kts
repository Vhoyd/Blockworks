plugins {
    kotlin("jvm") version "2.3.10"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("org.jetbrains.dokka") version "2.1.0"
    id("org.jetbrains.dokka-javadoc") version "2.1.0"
    id("com.gradleup.shadow") version  "9.3.0"
    `maven-publish`
}

val targetJavaVersion = 21

val versionNumber = 4

group = "dev.vhoyd"
version = "ALPHA-$versionNumber"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
}


dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}


afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                artifact(tasks.shadowJar)
                 artifact(dokkaJavadocJar)
                artifact(sources.get())
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
        enabled = false
    }

    build {
        dependsOn(publishToMavenLocal)
    }

    shadowJar {
        dependsOn("compileKotlin")
        archiveClassifier.set("")
        archiveBaseName.set(project.name)
        archiveVersion.set(project.version.toString())
    }
}

val sources by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}



// Hacked this together just so that I can test the plugin quicker without needing a file manager open.
val export by tasks.registering(Copy::class) {
    dependsOn(tasks.build)
    from(tasks.shadowJar)
    into(File(System.getProperty("user.home"), "Desktop/Paper 1.21.11 server/plugins/"))
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

val dokkaJavadocJar by tasks.registering(Jar::class) {
    description = "A Javadoc JAR containing Dokka Javadoc"
    from(tasks.dokkaGeneratePublicationJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}
