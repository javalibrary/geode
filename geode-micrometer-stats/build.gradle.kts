import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.2.60"
}

group = "org.apache.geode"
version = "1.8.0-SNAPSHOT"

repositories {
    maven { setUrl("http://dl.bintray.com/kotlin/kotlin-eap") }
    maven { setUrl("file:///home/ukohlmeyer/.m2/repository") }
    mavenCentral()
    mavenLocal()
}

dependencies {
    compile( project(":geode-stats-common"))
    compile(kotlin("stdlib-jdk8"))
    compile(group = "io.micrometer", name = "micrometer-core", version = "1.0.6")
    compile(group = "io.micrometer", name = "micrometer-registry-influx", version = "1.0.6")
    compile(group = "io.micrometer", name = "micrometer-registry-jmx", version = "1.0.6")
    compile(group = "io.micrometer", name = "micrometer-registry-prometheus", version = "1.0.6")
//    implementation(group = "test", name = "micrometer-stats", version = "1.0-SNAPSHOT")

}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.suppressWarnings = true
}