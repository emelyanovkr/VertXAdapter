plugins {
    id("java")
}

group = "vertx.impl"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.vertx:vertx-core:4.5.1")
    testImplementation("io.vertx:vertx-unit:4.5.1")
    testImplementation("io.vertx:vertx-junit5:4.5.1")

    // For future testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:5.10.1")
    testImplementation("org.mockito:mockito-core:5.9.0")
}

tasks.test {
    useJUnitPlatform()
}