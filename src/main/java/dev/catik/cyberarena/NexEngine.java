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

    private NexEngine() {
    }

    public static String execute(String rawInstruction) {
        return execute(rawInstruction, null);
    }

    public static String execute(String rawInstruction, ServerPlayerEntity player) {
        if (rawInstruction == null || rawInstruction.isBlank()) {
            return "§cNEX: empty instruction. Type HELP.";
        }

        String[] parts = rawInstruction.trim().split("\\s+", 2);
        String command = parts[0].toUpperCase(Locale.ROOT);
        String argument = parts.length > 1 ? parts[1].trim().toLowerCase(Locale.ROOT) : "";

        return switch (command) {
            case "HELP" -> "§bNEX instruction set:\n§fHELP §7- show commands\n§fSTATUS §7- runtime status\n§fNODES §7- list virtual nodes\n§fSCAN <node.arena> §7- inspect and discover a simulated node\n§fCONNECT <node.arena> §7- open a simulated session";
            case "STATUS" -> "§aNEX RUNTIME READY §7| §fSandbox: active §7| §fExternal I/O: blocked";
            case "NODES" -> "§bKNOWN NODES: §f" + String.join(", ", NODES.keySet());
            case "SCAN" -> scan(argument, player);
            case "CONNECT" -> connect(argument);
            default -> "§cNEX ERROR: unknown instruction '" + parts[0] + "'. Type HELP.";
        };
    }

    private static String scan(String nodeId, ServerPlayerEntity player) {
        String nodeType = NODES.get(nodeId);
        if (nodeType == null) {
            return "§cSCAN FAILED: node not found in the fictional .arena network.";
        }

        String result = "§aSCAN COMPLETE §7| §f" + nodeId + " §7| §fTYPE: " + nodeType + " §7| §fRISK: LOW";
        if (player == null) {
            return result;
        }

        boolean firstDiscovery = ArenaState.get(player.getServer()).discoverNode(player, nodeId);
        if (firstDiscovery) {
            return result + "\n§6FIRST DISCOVERY: +25 credits, +5 reputation.";
        }
        return result + "\n§7Node already recorded in your profile. No repeat reward.";
    }

    private static String connect(String nodeId) {
        String nodeType = NODES.get(nodeId);
        if (nodeType == null) {
            return "§cCONNECT FAILED: unknown virtual node.";
        }
        return "§aSIMULATED SESSION OPEN §7| §f" + nodeId + " §7| §f" + nodeType + "\n§7No real network connection was made.";
    }
}
