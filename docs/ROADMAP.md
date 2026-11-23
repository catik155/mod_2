# Cyber Arena implementation roadmap

## Phase 1 — Playable foundation
- [x] Fabric 1.21.1 project scaffold
- [x] Java 21 and Gradle build configuration
- [x] Server-side `/arena` command entry point
- [x] Initial allow-listed NEX interpreter
- [x] Fictional `.arena` node registry
- [ ] Verify a successful remapped JAR build in GitHub Actions

## Phase 2 — Player computer and persistence
- [ ] Persist per-player profiles on the server
- [ ] Add credits, reputation, rank, and upgrade state
- [ ] Add an interactable virtual computer block or item
- [ ] Build a terminal screen with command history and clear feedback

## Phase 3 — Simulated network world
- [ ] Expand node definitions and connection graph
- [ ] Add discovery, access levels, services, and simulated protocols
- [ ] Add an in-game virtual browser with fictional sites only
- [ ] Keep every address and operation inside the `.arena` simulation

## Phase 4 — Contracts and risk systems
- [ ] Add server-generated contracts and rewards
- [ ] Add alert, trace, cooldown, and defensive systems
- [ ] Add tool/program unlocks and computer upgrades

## Phase 5 — Multiplayer competition
- [ ] Validate every state-changing action on the server
- [ ] Add fair simulated player-vs-player encounters
- [ ] Add leaderboards, groups, and tournaments

## Phase 6 — Content and release
- [ ] Add tutorial, story contracts, NPC clients, and random events
- [ ] Add sound, particles, localization, and accessibility polish
- [ ] Run multiplayer and persistence tests
- [ ] Publish a versioned JAR release after CI passes

## Safety boundary
All cyber-themed mechanics are fictional game logic. No real network access, external target scanning, operating-system command execution, or real exploit payloads are part of this project.
