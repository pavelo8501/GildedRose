
val kotlinVersion: String by project
val funHelpersVersion:String by project
val serializationVersion: String by project

val junitVersion: String by project


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
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${serializationVersion}")
    implementation("po.misc:funhelpers:${funHelpersVersion}")

	//testImplementation(kotlin("test"))
	//testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")

    testImplementation("org.jetbrains.kotlin:kotlin-test:${kotlinVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter:${junitVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}

tasks.test {
	useJUnitPlatform()
}

tasks.register<JavaExec>("texttest") {
	description = "Allow you to run text-based approval tests with texttest"
	group = JavaBasePlugin.BUILD_TASK_NAME
	mainClass.set("com.gildedrose.TexttestFixtureKt")
	classpath = sourceSets["test"].runtimeClasspath
	args("30")
}

application {
	mainClass.set("po.gildedrose.TexttestFixtureKt")
}

tasks.shadowJar {
    archiveBaseName.set("gildedrose")
    archiveVersion.set("")
    archiveClassifier.set("")
    mergeServiceFiles()
    manifest {
        attributes["Main-Class"] = "po.gildedrose.MainKt"
    }
}