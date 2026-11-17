# Repository Guidelines

## Project Structure & Module Organization

This repository is a Java library for FINT relations and resource wrappers. Production code lives in `src/main/java/no/fint/model/relation/`: `Relation` builds relation links, and `FintResource<T>` groups a model with its relations. Spock specifications live in `src/test/groovy/no/fint/model/relation/`; Java test fixtures live in the corresponding `src/test/java/` package. Gradle configuration is at the repository root; wrapper files are under `gradle/wrapper/`.

## Build, Test, and Development Commands

Use JDK 25 to match the toolchain and CI configuration. The wrapper pins Gradle 9.9.1.

- `./gradlew clean build`: compile, run tests, and assemble the library JAR under `build/libs/`.
- `./gradlew test`: run all Spock specifications.
- `./gradlew test --tests 'no.fint.model.relation.RelationSpec'`: run one specification.
- `./gradlew test jacocoTestReport`: run tests and generate coverage reports under `build/reports/jacoco/test/`.
- `./gradlew javadoc`: generate API documentation in `build/docs/javadoc/`.
- `./gradlew publishToMavenLocal`: publish the library, sources, and Javadocs locally.

Dependencies resolve from Maven Central. Spring Boot 4.1.2's BOM aligns test dependencies; published runtime dependencies contain only Jackson annotations.

## Coding Style & Naming Conventions

Follow existing four-space indentation, Java semicolons, and Groovy style without semicolons. Target Java 25. Keep packages lowercase, class names in PascalCase, and methods and fields in camelCase. Preserve Lombok-generated model methods and Jackson annotations when changing serialization behavior. No formatter or lint configuration is checked in.

## Testing Guidelines

Use Spock specifications named `*Spec.groovy`, with descriptive string feature names and `given:`, `when:`, and `then:` blocks as appropriate. Tests run on JUnit Platform with Groovy 5. Cover builder validation, relation collections, and Jackson 3 round trips. Keep Spring Boot mapper compatibility tests passing. The Gradle `check` task generates a JaCoCo report and enforces at least 90% instruction and branch coverage.

## Commit & Pull Request Guidelines

History uses short descriptions such as `Added Link.` and `Switched to reposilite.`; no enforced commit prefix is evident. Write concise, action-oriented subjects. Pull requests should explain the behavior change, reference relevant issues, and record test commands and results. Highlight public API or JSON compatibility changes. Keep publishing credentials out of commits; supply Reposilite credentials through Gradle properties.
