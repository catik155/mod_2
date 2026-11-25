# Cyber Arena

A fictional multiplayer cyber-competition mod for Minecraft 1.21.1 (Fabric), built with Java 21.

## Workstation

Press **K** in-game to open the Cyber Arena workstation. It combines a fictional browser with themed .arena pages (market, recipes, media, travel, sports and more), plus a terminal that can switch between NX (legacy NEX aliases) and FLEX (legacy VORA aliases) command languages. Both languages show command hints and translate into the same safe game actions. The workstation sends NEX instructions to the server; the server validates and applies every game action.

## NX quick reference

The concise language reference, including aliases, instructions and FLOW examples, is in [docs/NEX_LANGUAGE.md](docs/NEX_LANGUAGE.md). The fictional directory contains 40 .arena sites, 15 nodes, simulated service slots and collectible file records. Player file inventory is persistent: READ collects, FINV lists, FSELL sells explicitly; files do not generate passive credits.

| Instruction | Function |
|---|---|
| `MANU` | Show the NEX language manual |
| `SEEK <word>` | Search fictional .arena sites |
| `WHO <site.arena>` | Show a fictional host profile |
| `PING <site.arena>` | Show simulated response latency |
| `FILES <site.arena>` | List fictional site records |
| `READ <site.arena> <file>` | Read fictional record metadata |
| `FLOW <a; b; c>` | Run up to six safe NEX instructions |
| `PORTA <site.arena>` | Inspect simulated service slots |
| `CRACK <site.arena>` | Attempt virtual data extraction |
| `MINE` | Run a virtual mining cycle |
| `UPGR <module>` | Buy a module with credits |
| `SELR` | Exchange stored data shards for credits |
| `NODR` | List mapped virtual nodes |
| `SCAN <node.arena>` | Discover a fictional network node |

Use `/arena nex MANU` or open the workstation with **K**. The server command form is `/arena nex <instruction>`. File commands: `FINV`, `FSELL ALL`, or `FSELL <site.arena>/<file>`.

## Progression

New operators begin with zero credits, zero reputation and no installed modules. Earn credits through starter contracts, simulated site jobs, data extraction and mining. Upgrade seven branches, each with five levels:

- **FIREWALL** — defend against other players.
- **AUTOM** — improve simulated attack automation.
- **STEALTH** — improve concealment.
- **EXTRACT** — increase virtual data yield.
- **MINER** — improve mining output.
- **CPU** — increase mining capacity/output.
- **STORAGE** — increase data vault capacity.

Use `/arena profile` to inspect progression. Player raids are available with `/arena raid <player>` when both players are online.

## Simulation-only boundary

All computers, websites, service ports, files, mining, attacks, defenses and data are fictional Minecraft game-state objects. The mod does not connect to real networks, scan external hosts, execute operating-system commands, or access real data.

## Build

Requires Java 21. Run `gradle build`. The distributable remapped JAR is written to `build/libs/`; the `-dev.jar` is for development only. GitHub Actions builds the project and uploads the remapped JAR as an artifact when a workflow succeeds.
