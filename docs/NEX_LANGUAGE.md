# NX and FLEX — language guide / roadmap

Cyber Arena will have two fictional programming languages. The current command interpreter is a limited prototype; the full programming constructs below are planned and are **not all implemented yet**.

## Language identities
- **NX** (current NEX command vocabulary): short, direct terminal language.
- **FLEX** (current VORA alternate vocabulary): different keywords and style, same safe game capabilities.
- Temporary compatibility aliases: NEX and VORA while the migration is in progress.

Both will compile/interpret into the same restricted game-only instruction model. They cannot access the real internet, IPs, external ports, operating-system commands, local files, or real devices.

## Current implemented command baseline
| Purpose | NX/NEX command |
|---|---|
| Help | `MANU`, `HELP`, `?` |
| Status | `STAT` |
| Node map | `NODR`, `MAPA` |
| Discover node | `SCAN <node>` |
| Search site directory | `SEEK <word>` |
| Site overview | `WHO <site.arena>` |
| Simulated latency | `PING <site.arena>` |
| Fictional service list | `PORTA <site.arena>` |
| Service detail | `PORTA <site.arena> <slot>` |
| File list / metadata | `FILES <site.arena>`, `READ <site.arena> <file>` |
| Simulated extraction | `CRACK <site.arena>` |
| Mining / sell shards | `MINE`, `SELR` |
| Upgrade | `UPGR <module>` |
| Contracts | `CONTRACTS`, `RUN <id>` |
| Safe routine | `FLOW <step; step; ...>` |

## FLEX current command equivalents (prototype translator)
| FLEX | Equivalent NX/NEX action |
|---|---|
| `LUMA` | `MANU` — help |
| `KERI` | `SEEK` — search |
| `DORA` | `PORTA` — services |
| `NAVA` | `NODR` — node map |
| `MIRA` | `STAT` — status |
| `TARA` | `CRACK` — simulated extraction |
| `DARA` | `MINE` — mining |
| `SELI` | `SELR` — exchange shards |
| `UPRA` | `UPGR` — upgrade |
| `JOBS` | `CONTRACTS` — contract board |
| `RUNA` | `RUN` — contract |
| `SAVA` | `SCAN` — discover node |
| `PIRA` | `PING` — simulated latency |
| `FILA` | `FILES` — list game records |
| `REVA` | `READ` — read record metadata |
| `WORA` | `WHO` — site info |

## Full language features — planned

### 1. Variables, types, assignment
Planned types: `number`, `text`, `bool`, `list`, `record`, and `null`.
```nx
let target = "vault.arena"
let attempts = 3
let stealthMode = true
const rewardLimit = 120
```

FLEX syntax will use its own words for declaration and assignment, but the same data types and rules.

### 2. Operators
Planned:
- Arithmetic: `+`, `-`, `*`, `/`, `%`
- Comparison: `==`, `!=`, `<`, `<=`, `>`, `>=`
- Logic: `AND`, `OR`, `NOT` (plus symbolic equivalents)
- String concatenation and safe interpolation

### 3. Conditions
```nx
if alert < 40
    print("Risk is manageable")
else
    print("Pause and review")
endif
```

### 4. Loops
Planned `while`, bounded `for`, `break` and `continue`. Scripts will have a strict instruction budget and maximum iteration count.
```nx
for index in 1..3
    print("Training pass " + index)
endfor
```

### 5. Functions
Planned named functions, parameters, return values, local scope, recursion limit.
```nx
function estimateReward(base, bonus)
    return base + bonus
endfunction
print(estimateReward(40, 15))
```

### 6. Lists and records
```nx
let sites = ["shop.arena", "food.arena", "music.arena"]
let profile = {name: "rookie", credits: 50, alert: 0}
print(sites[1])
print(profile.credits)
```

### 7. Comments and script files
Planned single-line `#` comments and multiline comments, named scripts, save/load, versioned import/export, validation before execution.

### 8. Built-in game functions (planned)
Only fictional game APIs, for example:
- `siteInfo(name)` — read public simulated site summary
- `listRecords(name)` — return game records
- `contractList()` — contracts in the player's game board
- `playerStatus()` — own profile/status
- `upgradeInfo(module)` — view upgrade information

These functions will not perform real-world network actions or interact with outside systems.

### 9. Error handling / debugging (planned)
- Syntax diagnostics with line/column and highlighted token
- Suggestions for misspelled commands
- `try/catch` for game simulation errors (planned)
- Step mode, breakpoints, variable watch and execution trace (planned)
- `dryrun` preview and instruction-budget display (planned)

## Example current FLOW routine
FLOW is the current limited automation feature, not the planned full scripting language. It accepts up to six approved instructions, separated by semicolons, and stops on error:
```
FLOW SEEK vault; WHO vault.arena; PORTA vault.arena; FILES vault.arena
```

## Example future NX script (illustrative, not yet executable)
```nx
# inspect fictional sites, don't run external actions
let target = "food.arena"
let profile = playerStatus()
print("Credits: " + profile.credits)
if profile.alert < 30
    print(siteInfo(target))
else
    print("High alert: review before accepting more work")
endif
```

## Example future FLEX script (illustrative, not yet executable)
```flex
set destination to "food.arena"
show player
when alert below 30 do
    show site(destination)
otherwise
    say "Alert high; pause and review"
end
```

## Safety and runtime constraints (planned)
- No shell, eval, reflection, external libraries, network or file system.
- Strict instruction/time/iteration limits, recursion depth cap, bounded memory.
- Server-side validation of any credit, extraction, upgrade, contract or PvP effect.
- Scripts may be previewed; shared scripts are validated and sandboxed.
