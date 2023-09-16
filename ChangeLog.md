# Potato Tech Kit / Potteckit

This is a **pre-release** version.

## Info

### Minecraft Versions

- Supported Minecraft version: `1.12.2`

### Mod Versions

- Current version: `0.1.4`
- Previous version: `0.1.3`
- Previous annotated version: `0.1.2`
- Previous release version: `null`

## Abstract

## New Features

### Game Phases

New sub-phase details in Game Phase Clocks:

| abbr |       phase        |     arg 0     |     arg 1      |       arg 2       |
|:----:|:------------------:|:-------------:|:--------------:|:-----------------:|
|  TT  |     Tile Tick      | `long` delay  | `int` priority | `long` relativeID |
| TEU  | Tile Entity Update | `int` ordinal |       \        |         \         |

### Fix LAN Quitting Freeze

Fixes [MC-72943](https://bugs.mojang.com/browse/MC-72943) in `1.12.2`.

| Option                  |   Side   |        Type         | Default        |
|-------------------------|:--------:|:-------------------:|----------------|
| Fix LAN Quitting Freeze | **both** | Boolean with Hotkey | `true`, `null` |

## Modified Features

## Code Changes
