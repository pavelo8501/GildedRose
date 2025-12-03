
val kotlinVersion: String by project
val funHelpersVersion:String by project
val serializationVersion: String by project
val kotlinReflectVersion: String by project
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
    maven {
        name = "PublicGitHubPackages"
        url = uri("https://maven.pkg.github.com/pavelo8501/ReKotlin")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

kotlin {
    jvmToolchain(23)
}

dependencies {
	implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinReflectVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${serializationVersion}")
    api("po.misc:funhelpers:1.0.0")

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
	mainClass.set("com.gildedrose.Main")
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