import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.1.5.RELEASE"
    id("io.spring.dependency-management") version "1.0.7.RELEASE"
    kotlin("jvm") version "1.2.71"
    kotlin("plugin.spring") version "1.2.71"
}

group = "com.withaion"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.security.oauth.boot:spring-security-oauth2-autoconfigure:2.0.0.RELEASE")

    implementation ("org.springframework.cloud:spring-cloud-gcp-starter-storage")
    implementation("net.coobird:thumbnailator:0.4.8")

    implementation("org.keycloak:keycloak-admin-client:6.0.1")
    implementation("javax.ws.rs:javax.ws.rs-api:2.0")
    implementation("org.jboss.resteasy:resteasy-jaxrs:3.6.3.Final")
    implementation("org.jboss.resteasy:resteasy-client:3.6.3.Final")
    implementation("org.jboss.resteasy:resteasy-jackson2-provider:3.6.3.Final")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

dependencyManagement {
    imports {
        mavenBom ("org.springframework.cloud:spring-cloud-dependencies:Greenwich.SR3")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
