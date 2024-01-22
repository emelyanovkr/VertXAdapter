plugins {
    id("java")
}

group = "vertx.impl"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.vertx:vertx-stack-depchain:4.5.1"))
    implementation("io.vertx:vertx-core")
}
