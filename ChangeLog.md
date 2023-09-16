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

### Fix LAN Bugs

|         Config          |                        Issue                        |    Side    | Default |
|:-----------------------:|:---------------------------------------------------:|:----------:|:-------:|
| Fix LAN Quitting Freeze | [MC-72943](https://bugs.mojang.com/browse/MC-72943) | **server** | `true`  |
|  Fix LAN Skin Absence   | [MC-52974](https://bugs.mojang.com/browse/MC-52974) | **server** | `true`  |

### Miscellaneous

- Realm Page Access
  - Side: client
  - Type: Option List
  - Default: `disabled`
  - Values: `vanilla`, `disabled`, `invisible`

## Modified Features

## Code Changes
