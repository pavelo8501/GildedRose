# Gilded Rose starting position in Kotlin

This is the Kotlin version of the Gilded Rose Refactoring Kata. 
This kata is designed to practice refactoring legacy code while preserving its behavior.

## Prerequisites

- Java 23 or later
- Gradle 8.0 or later
- Docker (optional, for containerized execution)
## Getting Started

### Clone the Repository

```bash
    git clone https://github.com/pavelo8501/GildedRose.git
    cd GildedRose-Kotlin
```

### Build the project:
```bash
    ./gradlew build
```

## Run the Text Fixture from Command-Line with gradle

Uncomment this line in Main.kt
```kotlin
    fun main(args: Array<String>) = runMain(args, useItems = null)

```

 ```
./gradlew -q text

```

### Specify Number of Days

For e.g. 10 days:

```
./gradlew run --args 10
```

#### Docker Installation

1. Docker installation:
   1.1. Build and run the application using Docker Compose:

```bash
    docker-compose up
    docker run -it gildedrose:latest

```

### API Documentation



There are instructions in the [TextTest Readme](https://github.com/emilybache/GildedRose-Refactoring-Kata/blob/main/GildedRoseRequirements.md) for setting up TextTest. 
What's unusual for the Java version is there are two executables listed in [config.gr](../texttests/config.gr) for Java. One uses Gradle wrapped in a python script, the other relies on your CLASSPATH being set correctly in [environment.gr](../texttests/environment.gr).

