import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.41"
    id("org.jlleitschuh.gradle.ktlint") version "8.2.0"
}

group = "com.johnowl.toggle"
version = "1.0.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.springframework:spring-core:5.1.8.RELEASE")
    implementation("org.springframework:spring-beans:5.1.8.RELEASE")
    implementation("org.springframework:spring-context:5.1.8.RELEASE")
    implementation("org.springframework:spring-expression:5.1.8.RELEASE")
    implementation("org.springframework:spring-web:5.1.8.RELEASE")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.+")
    testImplementation("org.mockito:mockito-inline:2.13.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}