# Cyber Arena roadmap

## Inspiration and direction (research-informed)
- [x] Research comparable fictional hacking/programming games: Hacknet, Uplink, Grey Hack, Bitburner, EXAPUNKS, Cyber Manhunt
- [ ] Use Hacknet/Uplink inspiration for mission pacing, readable logs, alerts, contracts and story clues
- [ ] Use Grey Hack/Bitburner inspiration for a deeper sandbox and custom scripting—adapted into safe, fully fictional Minecraft systems
- [ ] Use EXAPUNKS inspiration for optional puzzle challenges that reward optimization and creativity
- [ ] Use Cyber Manhunt inspiration for investigation missions, cross-site clues, NPC stories and data verification
- [ ] Keep all systems strictly simulated: no real network, real addresses, OS commands, real data, or actionable real intrusion tooling

## Core
- [x] Fabric 1.21.1 / Java 21 project
- [x] Server-authoritative persistent profiles and zero-start economy
- [x] Simulated player-vs-player raids and alerts\n- [x] Add RAID <player> to the NX console so PvP attacks are launched from the console, not the browser

## Browser / fictional websites
- [x] Browser workstation opened with O; separate Browser, ordinary Console (P), and Automation tabs
- [x] Browser has a dedicated top-right “NX консоль сайта” shortcut on each opened .arena page; it opens a site-context console without replacing the ordinary Console tab
- [x] Keep the regular Console independent of the browser site context; the player can switch to it at any time
- [x] Site-context NX console: selected site context is applied to targeted commands, has site-specific quick buttons, and a clear “current site” indicator
- [x] Searchable, paginated fictional .arena directory (30 sites)
- [x] 30 themed landing-page snippets: markets, recipes, news, routes, sports, archives, social NPC groups and more
- [x] Fictional service panels and site record index (110 service entries, 90 records)
- [ ] Give every site a distinct layout module (header, navigation, cards, content sections, footer) rather than the same generic page
- [x] Browser is view-only: no service scan, extraction, or hacking buttons on site pages; all gameplay actions go through NX/FLEX console
- [x] Browser panel width and tab widths now adapt to available screen width; continue testing small resolutions and vertical overflow
- [ ] Finish responsive layout for short screens: stack controls, scale spacing, and prevent any clipped widgets at all GUI scales
- [ ] Add a competition panel in the upper-left with online players, actual Minecraft skin heads, and server-authoritative reputation/points; never display fabricated scores
- [ ] Add competition score synchronization for all online players and sort leaderboard by earned points
- [ ] Add site-specific sample posts, recipes, catalog entries, NPC profiles, bulletin boards, maps, schedules and fake transactions
- [ ] Site actions should yield site-themed loot/payloads, clues, contract objectives and upgrade resources
- [ ] Browser page navigation links, pagination inside sites, back/forward and saved bookmarks
- [ ] Add a visual status strip: site risk, player alert, discovered/locked content and contract relevance
- [ ] Site-to-site clue chains and recurring NPC/organizations
- [ ] More varied content generation with deterministic seed and multiplayer consistency

## NX (formerly NEX) language — planned full programming language
- [x] Existing command vocabulary and command manual (current baseline)
- [x] FLOW routine: up to six whitelisted instructions; server validates actions
- [ ] Canonical language name NX; keep NEX as a compatibility alias during migration
- [ ] Lexer/parser with clear error line, column, token and suggestions
- [ ] Variables, assignment, constants, strings, numbers, booleans
- [ ] Arithmetic, comparison and logical operators with precedence
- [ ] IF / ELSE / ENDIF branches
- [ ] WHILE and FOR loops with hard iteration/time budget
- [ ] Functions, parameters, return values, local scope and recursion cap
- [ ] Arrays/lists, maps/records, indexing and safe iteration
- [ ] Comments and multiline scripts
- [ ] Built-in fictional API: browser index, simulated site data, contracts, credits, upgrades and alert state
- [ ] Script editor, save/load, named scripts, syntax highlighting and autocomplete
- [ ] Script debugger: step, breakpoints, variables/watch panel, error stack
- [ ] Dry-run mode and run budget/limits
- [ ] Tutorial missions unlock language features in sequence
- [ ] Script share/import format with strict validation and no external file execution

## FLEX (formerly VORA) — alternate language
- [x] Current VORA word mapping exists as a simple UI translator
- [x] Expanded FLEX aliases for existing command set (including FLOW and SITE); premium purchase gate and advanced scripting are still pending
- [ ] Rename canonical language to FLEX; keep VORA alias during transition
- [ ] FLEX command aliases and vocabulary parallel NX without being identical
- [ ] Add structured FLEX scripts and equivalent variables/branches/loops/functions
- [ ] Switch language per terminal session and display selected language consistently
- [ ] Help, autocomplete and diagnostics in both NX and FLEX
- [ ] Language conversion view: show NX and FLEX equivalent side by side
- [ ] Both languages compile to the same safe internal instruction set; parity tests

## Missions and site content
- [ ] 50+ contract templates with different objective patterns and difficulty tiers
- [ ] Site-specific recovery missions and protected payloads with story fragments
- [ ] Recipe/crafting, travel, media, legal, sports, social and research themed mission chains
- [ ] NPC clients and organizations, reputation and rival responses
- [ ] Dynamic contracts with risk/reward previews and cooldowns
- [ ] Events: timed competitions, site refreshes, incidents, bonus objectives
- [ ] Mission narrative, choices, optional objectives and multiple reward types

## Progression / economy / automation
- [x] Seven upgrade modules: FIREWALL, AUTOM, STEALTH, EXTRACT, MINER, CPU, STORAGE
- [x] Credits, data shards, extraction rewards and cooldowns
- [x] Safe routine runner (up to six whitelisted instructions)
- [x] Add a dedicated Improvements tab with purchase buttons for all seven upgrade modules; purchases are validated and charged by the server\n- [ ] Automation tab must remain purchase-gated: before AUTOM is bought, show only the price and locked state; after purchase, reveal presets and FLOW
- [ ] Once purchased, unlock ready-made unattended automation presets and custom FLOW scripts (bounded, game-only actions); automation can run without manual command-by-command player input
- [ ] FLEX is a separate premium language purchase in the Improvements shop, with more capabilities than NX; clearly explain price and unlocked feature tiers
- [ ] Saved scripts and reusable automation presets
- [ ] Automation triggers, schedule and bounded repeat behavior
- [ ] Make FLEX the premium, purchased language with more advanced scripting features than NX (variables, reusable routines and expanded control flow, subject to implementation)
- [ ] Module prerequisite tree, specialized builds and skill perks
- [ ] Unique tools/programs that unlock new simulated actions
- [ ] Balance economy and rewards against risk and time investment

## Defense / multiplayer
- [x] Raid success influenced by attacker upgrades and target firewall
- [ ] Defense dashboard, honeypots, backup/recovery and incident notifications
- [ ] NPC rivals, factions, teams, tournaments and seasonal leaderboards
- [ ] Multiplayer contract sync and authoritative validation for all actions
- [ ] Anti-grief, rate limits, audit trail and clear consent rules for PvP\n- [ ] Add an explicit opt-in PvP setting and configurable protection for new players

## UI / polish
- [ ] Add a compact competition/leaderboard panel at the upper-left: player skins or faces, names, and earned points
- [x] Browser pages are informational only; gameplay commands are kept in the separate NX/FLEX console
- [x] Start adapting browser tabs and controls to available screen width
- [ ] Finish full width-and-height responsive layout, including narrow windows and mobile-like GUI scales
- [ ] Terminal autocomplete, command suggestions and contextual help
- [ ] Show next-command hints while typing, including example arguments
- [ ] Separate editor/console/output/log panes and resizable layout
- [ ] Browser themes and unique mini-layouts for each site category
- [ ] Clear navigation: browser-site NX shortcut at the top-right of site pages, separate ordinary Console tab, and separate purchase-gated Automation tab
- [ ] Accessibility: keyboard navigation, readable scaling, color-safe statuses
- [ ] Sound, animations, feedback, loading/error states and tutorial tooltips

## Testing and release
- [ ] Unit tests for NX/FLEX parser, type checks, limits and translation parity
- [ ] Site content consistency tests: arrays/indexes, file lists and services
- [ ] Persistence migration tests and multiplayer server/client checks
- [ ] Build CI and verify downloadable artifact
- [ ] Balance/content playtest, bug triage, release notes and install guide
- [ ] Publish JAR only after verified build

## Simulation boundary
All nodes, sites, services, records, scripts, extraction, mining and attacks are fictional Minecraft game systems. NX/FLEX must not perform real network access, scan real targets, execute OS commands, read real files or manipulate external systems.
