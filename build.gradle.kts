plugins {
    kotlin("jvm") version "2.1.0"
}

group = "ch.sbb.thelu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("ch.qos.logback:logback-classic:1.4.12")
    implementation("me.tongfei:progressbar:0.10.1")
    implementation("org.jgrapht:jgrapht-core:1.5.2")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}