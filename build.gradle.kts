plugins {
    kotlin("jvm") version "2.1.0"
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

dependencies {
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("ch.qos.logback:logback-classic:1.4.12")
    implementation("me.tongfei:progressbar:0.10.1")
}

tasks {
    wrapper {
        gradleVersion = "8.11.1"
    }
}
