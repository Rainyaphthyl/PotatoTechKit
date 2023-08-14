# Potato Tech Kit / Potteckit

## Usage

### Deployment

- Download the `*.litemod` and put it into `.minecraft/mods/`.

### Features and Functions

- [ChangeLog](ChangeLog.md)

## Development

### Setup and Edit

- Clone the repository.
- You MAY have to run `gradle setupDecompWorkspace`.

### Build and Release

Before release, ensure the mod version is correct:

- [gradle.properties](gradle.properties)
- [LiteModEntry](src/main/java/io/github/rainyaphthyl/potteckit/LiteModPotteckit.java)

Run your task by one of the following commands:

- Run `gradle build`, and the release will be a file with postfix `.litemod` in the [`build/libs/`](build/libs) directory.
- Run `gradle runClient` to test the mod on the IDE.
