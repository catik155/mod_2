package dev.catik.cyberarena;

import net.minecraft.server.network.ServerPlayerEntity;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class NexEngine {
    private static final Map<String, String> NODES = Map.of(
        "relay-01.arena", "PUBLIC RELAY",
        "market-02.arena", "EXCHANGE TERMINAL",
        "archive-03.arena", "DATA ARCHIVE"
    );
    private static final Map<String, String> SITES = Map.of(
        "shop.arena", "SHOP / EASY",
        "news.arena", "NEWS / PUBLIC",
        "grid.arena", "GRID / MEDIUM",
        "vault.arena", "VAULT / HARD",
        "labs.arena", "LABS / EXPERT"
    );
    private static final Map<String, List<String>> FILES = Map.of(
        "shop.arena", List.of("catalog.idx", "offers.dat", "ledger.log"),
        "news.arena", List.of("bulletins.txt", "dispatch.log", "public.key"),
        "grid.arena", List.of("load.map", "relay.cfg", "grid.log"),
        "vault.arena", List.of("records.enc", "ledger.bak", "access.log"),
        "labs.arena", List.of("research.dat", "prototype.idx", "lab-notes.txt")
    );

    private NexEngine() {}
    public static String execute(String raw) { return execute(raw, null); }

    public static String execute(String raw, ServerPlayerEntity player) {
        if (raw == null || raw.isBlank()) return "§cNEX: empty instruction. Type MANU.";
        String[] parts = raw.trim().split("\\s+", 2);
        String cmd = parts[0].toUpperCase(Locale.ROOT);
        String arg = parts.length > 1 ? parts[1].trim().toLowerCase(Locale.ROOT) : "";
        return switch (cmd) {
            case "MANU", "HELP" -> "§bNEX // MANUAL\n§fSEEK <word> §7- search fictional web index\n§fPORTA <site.arena> §7- inspect simulated services\n§fFILES <site.arena> §7- list fictional records\n§fREAD <site.arena> <file> §7- inspect a public record\n§fCRACK <site.arena> §7- extract virtual data\n§fMINE §7- run a virtual mining cycle\n§fUPGR <module> §7- buy an upgrade\n§fSELR §7- exchange data shards\n§fFLOW <a; b; c> §7- run up to 6 safe NEX instructions\n§fNODR / SCAN / STAT / CONTRACTS / RUN";
            case "STAT", "STATUS" -> "§aNEX CORE ONLINE §7| §fFictional .arena network §7| §fExternal I/O: disabled";
            case "NODR", "NODES" -> "§bVIRTUAL NODES: §f" + String.join(", ", NODES.keySet());
            case "SEEK" -> seek(arg);
            case "PORTA" -> ports(arg);
            case "FILES" -> files(arg);
            case "READ" -> readFile(arg);
            case "CRACK" -> player == null ? "§cOperator context required." : ArenaState.get(player.getServer()).breachSite(player, arg);
            case "MINE" -> player == null ? "§cOperator context required." : ArenaState.get(player.getServer()).mine(player);
            case "UPGR" -> player == null ? "§cOperator context required." : ArenaState.get(player.getServer()).upgrade(player, arg);
            case "SELR" -> player == null ? "§cOperator context required." : ArenaState.get(player.getServer()).sellData(player);
            case "FLOW" -> flow(arg, player);
            case "SCAN" -> scan(arg, player);
            case "CONTRACTS" -> "§bSTARTER CONTRACTS: §fsite-audit (40c), patch-rush (55c), data-recovery (70c)";
            case "RUN" -> player == null ? "§cPlayer context required." : ArenaState.get(player.getServer()).completeContract(player, arg);
            case "SITE" -> player == null ? "§cPlayer context required." : ArenaState.get(player.getServer()).siteEvent(player, arg);
            default -> "§cNEX ERROR: unknown instruction '" + parts[0] + "'. Type MANU.";
        };
    }

    private static String flow(String script, ServerPlayerEntity player) {
        if (player == null) return "§cFLOW requires an operator session.";
        if (script.isBlank()) return "§cFLOW requires instructions separated by semicolons.";
        if (script.length() > 240) return "§cFLOW limit: 240 characters.";
        String[] steps = script.split(";", -1);
        if (steps.length > 6) return "§cFLOW limit: 6 instructions per routine.";
        StringBuilder result = new StringBuilder("§bNEX FLOW // ").append(steps.length).append(" STEPS");
        Set<String> allowed = Set.of("SEEK", "PORTA", "FILES", "READ", "CRACK", "MINE", "UPGR", "SELR", "SCAN", "STAT", "NODR", "CONTRACTS");
        for (int i = 0; i < steps.length; i++) {
            String step = steps[i].trim();
            if (step.isEmpty()) return "§cFLOW stopped: step " + (i + 1) + " is empty.";
            String opcode = step.split("\\s+", 2)[0].toUpperCase(Locale.ROOT);
            if (!allowed.contains(opcode)) return "§cFLOW stopped: " + opcode + " is not permitted in routines.";
            String output = execute(step, player);
            result.append("\n§7[").append(i + 1).append("] §f").append(output);
            if (output.startsWith("§c") || output.startsWith("§e")) {
                result.append("\n§eFLOW halted after step ").append(i + 1).append(".");
                break;
            }
        }
        return result.toString();
    }

    private static String seek(String query) {
        if (query.isBlank()) return "§cSEEK requires a search word.";
        String result = SITES.entrySet().stream()
            .filter(e -> e.getKey().contains(query) || e.getValue().toLowerCase(Locale.ROOT).contains(query))
            .map(e -> e.getKey() + " §7- " + e.getValue())
            .collect(Collectors.joining("\n§f"));
        return result.isEmpty() ? "§eNo .arena sites found." : "§bNEX WEB INDEX\n§f" + result;
    }

    private static String ports(String site) {
        if (!SITES.containsKey(site)) return "§cUnknown fictional host. Use SEEK first.";
        return "§bPORTA REPORT §7| §f" + site
            + "\n§aPORT 021 §7- WEB-GATE / OPEN"
            + "\n§ePORT 044 §7- DATA-VAULT / GUARDED"
            + "\n§cPORT 071 §7- ADMIN-CORE / LOCKED"
            + "\n§7Fictional game service slots only; no real network access.";
    }

    private static String files(String site) {
        List<String> entries = FILES.get(site);
        if (entries == null) return "§cUnknown fictional host. Use SEEK first.";
        return "§bNEX FILE INDEX §7| §f" + site + "\n§f" + String.join("\n§f", entries)
            + "\n§7Use READ " + site + " <file> to inspect a record.";
    }

    private static String readFile(String args) {
        String[] parts = args.split("\\s+", 2);
        if (parts.length != 2) return "§cREAD syntax: READ <site.arena> <file>";
        List<String> entries = FILES.get(parts[0]);
        if (entries == null || !entries.contains(parts[1])) return "§cRecord not found in the fictional file index.";
        String description = switch (parts[1]) {
            case "catalog.idx" -> "Public catalog index: 24 virtual product entries.";
            case "offers.dat" -> "Public offers: rotating in-game contract hints and market values.";
            case "ledger.log" -> "Public ledger snapshot: simulated exchange activity.";
            case "bulletins.txt" -> "Public bulletin archive: three recent fictional network notices.";
            case "dispatch.log" -> "Relay dispatch log: simulated message routing events.";
            case "public.key" -> "Public verification key fingerprint for the Arena Daily service.";
            case "load.map" -> "Grid load map: fictional district demand and supply values.";
            case "relay.cfg" -> "Relay configuration summary: three simulated service endpoints.";
            case "grid.log" -> "Grid event log: recent virtual load-balancing events.";
            case "records.enc" -> "Encrypted archive manifest. Payload requires a successful CRACK operation.";
            case "ledger.bak" -> "Protected backup ledger. Payload requires a successful CRACK operation.";
            case "access.log" -> "Vault access log: fictional authentication events.";
            case "research.dat" -> "Research manifest: experimental modules and lab contracts.";
            case "prototype.idx" -> "Prototype index: three fictional device blueprints.";
            case "lab-notes.txt" -> "Research notes: hints for advanced in-game contracts.";
            default -> "Fictional record metadata.";
        };
        return "§bNEX RECORD §7| §f" + parts[0] + "/" + parts[1] + "\n§f" + description
            + "\n§7Read-only metadata. Protected payloads are only obtained through game progression.";
    }

    private static String scan(String id, ServerPlayerEntity player) {
        String type = NODES.get(id);
        if (type == null) return "§cNode not found in the fictional .arena network.";
        if (player == null) return "§aSCAN COMPLETE §7| §f" + id + " §7| §f" + type;
        boolean fresh = ArenaState.get(player.getServer()).discoverNode(player, id);
        return "§aSCAN COMPLETE §7| §f" + id + " §7| §f" + type
            + (fresh ? "\n§d+1 reputation for mapping the simulated network." : "\n§7Already mapped.");
    }
}
