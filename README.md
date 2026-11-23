# Cyber Arena

A fictional multiplayer cybersecurity competition mod for Minecraft 1.21.1 (Fabric), built with Java 21.

## Simulation-only design

Every computer, address, network node, service, command, attack, defense, and data object is fictional and exists only inside the Minecraft game. The mod never connects to real networks, scans real hosts, visits external targets, executes operating-system commands, or launches external payloads.

## Current prototype

- Fabric mod entrypoint and server command registration.
- `/arena help`, `/arena status`, and `/arena nodes` commands.
- `/arena nex <instruction>` with a small allow-listed NEX command interpreter.
- NEX instructions: `HELP`, `STATUS`, `NODES`, `SCAN <node.arena>`, `CONNECT <node.arena>`.
- Three fictional nodes: `relay-01.arena`, `market-02.arena`, and `archive-03.arena`.
- GitHub Actions workflow builds the remapped mod JAR with Java 21 and publishes it as a workflow artifact.

## Build

The project uses Fabric Loom and Gradle. Run `gradle build` with Java 21 installed. The distributable remapped JAR is written to `build/libs/` (the `-dev.jar` file is for development only).

## Roadmap

1. Persistent server-side player profiles and virtual computer UI.
2. NEX parser expansion, terminal history, and tutorial.
3. Simulated node discovery, contracts, and virtual browser.
4. Credits, progression, alert/trace/risk systems, and defensive tools.
5. Multiplayer competition, validation, leaderboards, groups, and tournaments.
