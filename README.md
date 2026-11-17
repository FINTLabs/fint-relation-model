# FINT Relation Model

[![Build Status](https://travis-ci.org/FINTmodels/fint-relation-model.svg?branch=master)](https://travis-ci.org/FINTmodels/fint-relation-model) 
[![FINT javadocs](https://img.shields.io/badge/FINT-javadocs-blue.svg)](https://docs.felleskomponent.no/fint-relation-model/)

## Requirements and compatibility

This library targets **Java 25** and is tested with **Spring Boot 4.1.2** and its
Jackson 3 mapper. It remains a regular library JAR with no Spring runtime dependency.
Its runtime dependency is Jackson annotations; Lombok is used only during compilation.
The public model API and JSON property names remain unchanged.

This Java 25 build requires Java 25 or newer in consuming applications. Jackson
databind is supplied by the application (for Boot, use `spring-boot-starter-jackson`).
Jackson 3 retains the `com.fasterxml.jackson.annotation` package for annotations.

## Build and test

Install JDK 25 and set `JAVA_HOME` to that installation. Use the checked-in Gradle
9.9.1 wrapper; no system Gradle installation is needed.

```sh
./gradlew clean build
./gradlew test --tests 'no.fint.model.relation.RelationSpec'
./gradlew jacocoTestReport
```

`build` runs the Spock tests, Spring Boot JSON compatibility tests, and the coverage
gate (at least 90% instruction and branch coverage). The HTML coverage report is
`build/reports/jacoco/test/html/index.html`. JARs, including sources and Javadocs,
are generated under `build/libs/`; API documentation is in `build/docs/javadoc/`.
Dependencies are resolved from Maven Central without remote build scripts.

## Installation

For local development, publish the current snapshot first:

```sh
./gradlew publishToMavenLocal
```

Then add it to the consuming application's Gradle build:

```groovy
repositories {
    mavenLocal() // For the locally published 0-SNAPSHOT build.
    maven {
        url = uri('https://repo.fintlabs.no/releases')
    }
}

dependencies {
    implementation 'no.fint:fint-relation-model:0-SNAPSHOT'
}
```

For released builds, use the published release version and omit `mavenLocal()`.
Publishing to Reposilite uses the Gradle `publish` task with a release version
supplied through `-Pversion` and credentials through the `reposiliteUsername`
and `reposilitePassword` Gradle properties. The GitHub release workflow maps the
`REPOSILITE_USERNAME` and `REPOSILITE_PASSWORD` secrets to these properties using
the `ORG_GRADLE_PROJECT_` environment variable prefix. Gradle resolves credentials
from the repository name, `reposilite`, and requires them only for remote publishing.

## Usage


### Relation

Create a new Relation with type, field and value
```java
new Relation.Builder().with(TestDto.Relasjonsnavn.TESTREL).forType(TestDto.class).field("test-field").value("123").build();
```
or with type and value
```java
new Relation.Builder().with(TestDto.Relasjonsnavn.TESTREL).forType(TestDto.class).value("123").build();
```
or with link
```java
new Relation.Builder().with(TestDto.Relasjonsnavn.TESTREL).link("http://localhost/test").build();
```
----

### FintResource

```java
FintResource.with(testDto).addRelations(relation);
```
