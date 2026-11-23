package dev.catik.cyberarena;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
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
                .executes(context -> {
                    context.getSource().sendFeedback(
                        () -> Text.literal("§bCyber Arena §7| §fUse /arena help"),
                        false
                    );
                    return 1;
                })
                .then(CommandManager.literal("help")
                    .executes(context -> {
                        context.getSource().sendFeedback(
                            () -> Text.literal("§bCyber Arena commands:\n§f/arena status §7- simulation status\n§f/arena nodes §7- known virtual nodes\n§f/arena nex <command> §7- run a NEX instruction"),
                            false
                        );
                        return 1;
                    })
                )
                .then(CommandManager.literal("status")
                    .executes(context -> {
                        context.getSource().sendFeedback(
                            () -> Text.literal("§aSIMULATION ONLINE §7| §fExternal network access: disabled §7| §fNEX runtime: ready"),
                            false
                        );
                        return 1;
                    })
                )
                .then(CommandManager.literal("nodes")
                    .executes(context -> {
                        context.getSource().sendFeedback(
                            () -> Text.literal("§bVirtual nodes:\n§frelay-01.arena §7- public relay\n§fmarket-02.arena §7- exchange terminal\n§farchive-03.arena §7- data archive"),
                            false
                        );
                        return 1;
                    })
                )
                .then(CommandManager.literal("nex")
                    .then(CommandManager.argument("instruction", StringArgumentType.greedyString())
                        .executes(context -> {
                            String instruction = StringArgumentType.getString(context, "instruction");
                            String result = NexEngine.execute(instruction);
                            context.getSource().sendFeedback(() -> Text.literal(result), false);
                            return 1;
                        })
                    )
                )
            )
        );

        LOGGER.info("Cyber Arena initialized. All network activity is simulated in-game.");
    }
}
