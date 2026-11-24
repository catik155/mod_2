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
- [x] Expand browser directory from 30 to 40 fictional sites with dedicated titles, descriptions, content sections, file records and service ports
- [ ] Site actions should yield site-themed loot/payloads, clues, contract objectives and upgrade resources
- [ ] Browser page navigation links, pagination inside sites, back/forward and saved bookmarks
- [ ] Add a visual status strip: site risk, player alert, discovered/locked content and contract relevance
- [ ] Site-to-site clue chains and recurring NPC/organizations
- [ ] More varied content generation with deterministic seed and multiplayer consistency

## Terminal UI (Linux / GNU Readline inspired)
- [x] Full-screen terminal view with persistent output pane and bottom command prompt
- [x] Route server command responses into the terminal while it is open
- [x] Dynamic command and subcommand suggestions while typing
- [x] TAB completion cycles through matching suggestions
- [x] Up/down command history and Ctrl+R substring search
- [ ] Cursor-aware completion in the middle of a command line
- [ ] Argument suggestions from live player, site, discovered host and file data
- [ ] Scrollback selection, copy, clear and persistent per-player command history
- [ ] Syntax highlighting and inline diagnostics without executing a command

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

## Home economy, mining farm, VPN and police
- [x] Buy a physical mining-farm machine inside the personal house; farm level increases passive credits per minute
- [x] Track farm durability; sabotage can damage it and stop its income until repaired
- [x] Upgrade and repair the mining farm through the NX terminal
- [x] Add fictional per-player IP addresses using documentation-only TEST-NET ranges
- [x] Add a purchasable in-game VPN that masks the displayed fictional IP and reduces trace chance
- [x] Add wanted level, continuous simulated police review of suspicious network activity, VPN-adjusted detection, fines and patrol NPCs at high heat
- [x] Add staged PvP network workflow: target discovery, virtual IP attribution, port scan, service enumeration and module-specific exploit outcomes
- [x] Add terminal home sabotage: farm damage, power outage, supply theft, data theft, false alarm, station lock, false police call and raider diversion
- [ ] Add physical block-break interception for farm machine with durability, ownership and protection checks
- [x] Police stakeout appears beside the identified player home; entering or remaining inside the home triggers arrest and round elimination
- [ ] Add multiple farm models, cooling, power use, storage capacity, overheating and power-grid upgrades
- [ ] Add police investigation missions, bribes, evidence cleanup, warrants, escape and reputation effects
- [ ] Add VPN node selection, rotating virtual endpoints, bandwidth and temporary VPN outages

## Home, raids and power sabotage
- [x] Generate a personal starter house near world spawn and set the player's respawn point inside it
- [x] Passive mining income is paid automatically while the player is online; MINER levels increase the payout
- [x] Periodic home incidents: hostile pillagers can raid the house and power sabotage can disable the workstation
- [x] Workstation commands are blocked during a blackout except status/help/repair; emergency REPAIR costs credits
- [x] Consumable BREAKER and GUARD charges can each stop one matching incident; they are not permanent invulnerability
- [x] Add more home sabotage events: workstation damage, stolen supplies, false alerts, station lock, data theft, false police calls and raider diversion
- [x] Show purchased upgrades as physical stations and structures inside the personal home; refresh displays after purchases and on rejoin
- [ ] Add settings for incident frequency and safe-start protection on new servers

## Progression / economy / automation
- [x] Seven core upgrade modules: FIREWALL, AUTOM, STEALTH, EXTRACT, MINER, CPU, STORAGE\n- [x] Add consumable BREAKER and GUARD defense upgrades
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
- [x] Round objective: reach 10,000 credits to win; police arrest at the home eliminates the player

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
