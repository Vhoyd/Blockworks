plugins {
    kotlin("jvm") version "2.3.10"
    id("org.jetbrains.dokka") version "2.2.0"
    id("org.jetbrains.dokka-javadoc") version "2.2.0"
    id("com.gradleup.shadow") version  "9.3.0"
    `maven-publish`
}


version = "${project.property("version_number")}-${project.property("build_type")}"


kotlin {
    jvmToolchain(project.property("java_version").toString().toInt())

}

java {
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
}


dependencies {
    compileOnly("io.papermc.paper:paper-api:26.3.build.+")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
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

    shadowJar {
        dependsOn(compileKotlin, processResources)
        archiveClassifier.set("")
        archiveBaseName.set(project.name)
        archiveVersion.set(version.toString())
    }

    register<Jar>("dokkaDocs") {
        description = "A Javadoc JAR containing Dokka Javadoc"
        dependsOn(dokkaGeneratePublicationJavadoc)
        from(dokkaGeneratePublicationJavadoc.flatMap { it.outputDirectory })
        archiveClassifier.set("javadoc")
    }

    register<Copy>("Export") {
        description = "Exports the plugin into my testing directory. I don't care if it's ugly."
        from(shadowJar)
        into(File(System.getProperty("user.home"), "Desktop/Paper 26.3/plugins/"))
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.shadowJar)
            artifact(tasks.named("dokkaDocs"))
            groupId = "dev.vhoyd"
            artifactId = "blockworks"
            version = project.version.toString()
        }
    }
}

