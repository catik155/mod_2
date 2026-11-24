package dev.catik.cyberarena;

import dev.catik.cyberarena.client.CyberArenaScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public final class CyberArenaClient implements ClientModInitializer {
    private static KeyBinding openBrowser;
    private static KeyBinding openConsole;

    @Override
    public void onInitializeClient() {
        openBrowser = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.cyber_arena.open_browser",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            "category.cyber_arena"
        ));
        openConsole = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.cyber_arena.open_console",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            "category.cyber_arena"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openBrowser.wasPressed()) {
                if (client.player != null && client.currentScreen == null) {
                    client.setScreen(new CyberArenaScreen(0));
                }
            }
            while (openConsole.wasPressed()) {
                if (client.player != null && client.currentScreen == null) {
                    client.setScreen(new CyberArenaScreen(1));
                }
            }
        });
    }
}
