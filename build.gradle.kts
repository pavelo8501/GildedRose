
val kotlinVersion: String by project
val serializationVersion: String by project
val kotlinReflectVersion: String by project
val junitVersion: String by project
val coroutinesVersion :String by project
val typesafeVersion: String by project

plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow") version "8.1.1"
	application
}

group = "po.gildedrose"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
    mavenLocal()
}

kotlin {
    jvmToolchain(23)
}

dependencies {
	implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinReflectVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${serializationVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutinesVersion}")
    implementation("com.typesafe:config:${typesafeVersion}")

    testImplementation("org.jetbrains.kotlin:kotlin-test:${kotlinVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}

tasks.test {
	useJUnitPlatform()
}

tasks.register<JavaExec>("texttest") {
	description = "Allow you to run text-based approval tests with texttest"
	group = JavaBasePlugin.BUILD_TASK_NAME
	mainClass.set("po.gildedrose.MainKt")
	classpath = sourceSets["test"].runtimeClasspath
	args("30")
}

application {
	mainClass.set("po.gildedrose.MainKt")
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.shadowJar {
    dependsOn(tasks.test)
    archiveBaseName.set("gildedrose")
    archiveVersion.set("")
    archiveClassifier.set("")
    mergeServiceFiles()

    manifest {
        attributes["Main-Class"] = "po.gildedrose.MainKt"
    }
}