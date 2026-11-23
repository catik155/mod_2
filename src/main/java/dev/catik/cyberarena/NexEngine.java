package dev.catik.cyberarena;

import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Locale;
import java.util.Map;

public final class NexEngine {
    private static final Map<String, String> NODES = Map.of(
        "relay-01.arena", "PUBLIC RELAY",
        "market-02.arena", "EXCHANGE TERMINAL",
        "archive-03.arena", "DATA ARCHIVE"
    );
    private NexEngine() {}
    public static String execute(String raw) { return execute(raw, null); }

    public static String execute(String raw, ServerPlayerEntity player) {
        if (raw == null || raw.isBlank()) return "§cNEX: empty instruction. Type HELP.";
        String[] parts = raw.trim().split("\\s+", 2);
        String cmd = parts[0].toUpperCase(Locale.ROOT);
        String arg = parts.length > 1 ? parts[1].trim().toLowerCase(Locale.ROOT) : "";
        return switch (cmd) {
            case "HELP" -> "§bNEX COMMANDS\n§fHELP §7| STATUS | NODES\n§fSCAN <node.arena> §7- inspect a simulated node\n§fCONTRACTS §7- list starter jobs\n§fRUN <contract> §7- complete a simulated site task\n§fSITE <site.arena> §7- resolve a fictional site incident";
            case "STATUS" -> "§aNEX CORE ONLINE §7| §fSandbox: active §7| §fExternal I/O: disabled";
            case "NODES" -> "§bVIRTUAL NODES: §f" + String.join(", ", NODES.keySet());
            case "SCAN" -> scan(arg, player);
            case "CONTRACTS" -> "§bSTARTER CONTRACTS: §fsite-audit (40c), patch-rush (55c), data-recovery (70c)";
            case "RUN" -> player == null ? "§cPlayer context required." : ArenaState.get(player.getServer()).completeContract(player, arg);
            case "SITE" -> player == null ? "§cPlayer context required." : ArenaState.get(player.getServer()).siteEvent(player, arg);
            default -> "§cNEX ERROR: unknown command '" + parts[0] + "'. Type HELP.";
        };
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
