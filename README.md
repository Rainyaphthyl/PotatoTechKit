# Potato Tech Kit / Potteckit

## Usage

### Deployment

1. Download the `*.litemod` from the release and put it into `.minecraft/mods/`.
2. This mod requires [MaLiLib](https://github.com/maruohon/malilib). Download it from https://masa.dy.fi/mcmods/client_mods/ and put it into `.minecraft/mods/`.

### Features and Functions

1. Press `K + C` to open the config screen.
2. [ChangeLog](ChangeLog.md).

## Development

### Setup and Edit

- Clone the repository.
- You MAY have to run `gradle setupDecompWorkspace`.

### Build and Release

Before release, ensure the mod version is correct:

- [gradle.properties](gradle.properties)
- [Reference.java](src/main/java/io/github/rainyaphthyl/potteckit/util/Reference.java)

Run your task by one of the following commands:

- Run `gradle build`, and the release will be a file with postfix `.litemod` in the [`build/libs/`](build/libs) directory.
- Run `gradle runClient` to test the mod on the IDE.
