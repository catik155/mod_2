package dev.catik.cyberarena;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CyberArena implements ModInitializer {
    public static final String MOD_ID = "cyber_arena";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(CommandManager.literal("arena")
                .then(CommandManager.literal("help").executes(c -> reply(c.getSource(),
                    "§bCyber Arena\n§f/arena profile §7- profile and upgrades\n§f/arena contracts §7- starter contracts\n§f/arena contract <id> §7- complete a starter job\n§f/arena raid <player> §7- simulated PvP breach\n§f/arena upgrade <firewall|automation> §7- improve your system\n§f/arena site <shop.arena|news.arena|vault.arena> §7- resolve a fictional site incident\n§f/arena nex <instruction> §7- NEX terminal")))
                .then(CommandManager.literal("profile").executes(c -> {
                    ServerPlayerEntity p = c.getSource().getPlayerOrThrow();
                    ArenaState.ProfileView v = ArenaState.get(c.getSource().getServer()).view(p);
                    return reply(c.getSource(), "§bOPERATOR: §f" + p.getName().getString()
                        + "\n§6Credits: §f" + v.credits() + " §7| §dRep: §f" + v.reputation()
                        + "\n§bData shards: §f" + v.dataShards() + " §7| §cAlert: §f" + v.alertLevel() + "/5"
                        + "\n§aFirewall: §f" + v.firewall() + "/5 §7| §eAutomation: §f" + v.automation() + "/5"
                        + "\n§7Completed jobs: " + v.completedContracts() + " §8| mapped nodes: " + v.discoveredNodes());
                }))
                .then(CommandManager.literal("contracts").executes(c -> reply(c.getSource(),
                    "§bSTARTER JOBS\n§fsite-audit §7- 40 credits\n§fpatch-rush §7- 55 credits\n§fdata-recovery §7- 70 credits\n§7Use /arena contract <id>. Each starter job pays once.")))
                .then(CommandManager.literal("contract").then(CommandManager.argument("id", StringArgumentType.word()).executes(c -> {
                    ServerPlayerEntity p = c.getSource().getPlayerOrThrow();
                    return reply(c.getSource(), ArenaState.get(c.getSource().getServer()).completeContract(p, StringArgumentType.getString(c, "id")));
                })))
                .then(CommandManager.literal("raid").then(CommandManager.argument("target", StringArgumentType.word()).executes(c -> {
                    ServerPlayerEntity attacker = c.getSource().getPlayerOrThrow();
                    String name = StringArgumentType.getString(c, "target");
                    ServerPlayerEntity target = c.getSource().getServer().getPlayerManager().getPlayer(name);
                    if (target == null) return reply(c.getSource(), "§cTarget must be online in this Minecraft world.");
                    long now = attacker.getWorld().getTime();
                    return reply(c.getSource(), ArenaState.get(c.getSource().getServer()).raid(attacker, target, now));
                })))
                .then(CommandManager.literal("upgrade").then(CommandManager.argument("type", StringArgumentType.word()).executes(c -> {
                    ServerPlayerEntity p = c.getSource().getPlayerOrThrow();
                    return reply(c.getSource(), ArenaState.get(c.getSource().getServer()).upgrade(p, StringArgumentType.getString(c, "type").toLowerCase()));
                })))
                .then(CommandManager.literal("site").then(CommandManager.argument("site", StringArgumentType.word()).executes(c -> {
                    ServerPlayerEntity p = c.getSource().getPlayerOrThrow();
                    return reply(c.getSource(), ArenaState.get(c.getSource().getServer()).siteEvent(p, StringArgumentType.getString(c, "site").toLowerCase()));
                })))
                .then(CommandManager.literal("nex").then(CommandManager.argument("instruction", StringArgumentType.greedyString()).executes(c -> {
                    String result = NexEngine.execute(StringArgumentType.getString(c, "instruction"), c.getSource().getPlayer());
                    return reply(c.getSource(), result);
                })))
                .executes(c -> reply(c.getSource(), "§bCyber Arena §7| Use /arena help"))
            )
        );
        LOGGER.info("Cyber Arena initialized. All cyber operations are simulated in-game.");
    }

    private static int reply(net.minecraft.server.command.ServerCommandSource source, String message) {
        source.sendFeedback(() -> Text.literal(message), false);
        return 1;
    }
}
