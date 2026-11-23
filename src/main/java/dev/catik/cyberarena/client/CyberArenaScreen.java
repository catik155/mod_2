package dev.catik.cyberarena.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

/** Client-only workstation UI. All game actions are still validated by the server. */
public final class CyberArenaScreen extends Screen {
    private TextFieldWidget commandField;

    public CyberArenaScreen() {
        super(Text.literal("Cyber Arena"));
    }

    @Override
    protected void init() {
        int panelWidth = Math.min(420, this.width - 32);
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(28, (this.height - 230) / 2);

        commandField = new TextFieldWidget(textRenderer, left + 14, top + 102, panelWidth - 28, 20,
            Text.literal("NEX instruction"));
        commandField.setMaxLength(160);
        commandField.setFocused(true);
        addDrawableChild(commandField);

        int gap = 8;
        int buttonWidth = (panelWidth - 28 - gap) / 2;
        int x1 = left + 14;
        int x2 = x1 + buttonWidth + gap;
        int y = top + 132;
        addDrawableChild(ButtonWidget.builder(Text.literal("SEARCH .ARENA"), b -> runNex("SEEK arena"))
            .dimensions(x1, y, buttonWidth, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("PORT SCAN: VAULT"), b -> runNex("PORTA vault.arena"))
            .dimensions(x2, y, buttonWidth, 20).build());
        y += 25;
        addDrawableChild(ButtonWidget.builder(Text.literal("OPEN VAULT TARGET"), b -> runNex("CRACK vault.arena"))
            .dimensions(x1, y, buttonWidth, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("MINE CYCLE"), b -> runNex("MINE"))
            .dimensions(x2, y, buttonWidth, 20).build());
        y += 25;
        addDrawableChild(ButtonWidget.builder(Text.literal("UPGRADE AUTOM"), b -> runNex("UPGR autom"))
            .dimensions(x1, y, buttonWidth, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("UPGRADE FIREWALL"), b -> runNex("UPGR firewall"))
            .dimensions(x2, y, buttonWidth, 20).build());
        y += 29;
        addDrawableChild(ButtonWidget.builder(Text.literal("EXECUTE NEX"), b -> submitField())
            .dimensions(x1, y, buttonWidth, 22).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("CLOSE"), b -> close())
            .dimensions(x2, y, buttonWidth, 22).build());
    }

    private void submitField() {
        String instruction = commandField.getText().trim();
        if (!instruction.isEmpty()) runNex(instruction);
    }

    private void runNex(String instruction) {
        if (client == null || client.player == null || client.getNetworkHandler() == null) return;
        client.getNetworkHandler().sendChatCommand("arena nex " + instruction);
        close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        int panelWidth = Math.min(420, this.width - 32);
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(28, (this.height - 230) / 2);
        context.fill(left, top, left + panelWidth, top + 230, 0xEE101923);
        context.fill(left, top, left + panelWidth, top + 3, 0xFF29D6C5);
        context.drawTextWithShadow(textRenderer, Text.literal("CYBER ARENA // NEX WORKSTATION"),
            left + 14, top + 13, 0xFF55E6D2);
        context.drawTextWithShadow(textRenderer, Text.literal("VIRTUAL WEB  /  TERMINAL  /  OPERATOR TOOLS"),
            left + 14, top + 29, 0xFFB8C8D8);
        context.fill(left + 14, top + 49, left + panelWidth - 14, top + 91, 0xFF172634);
        context.drawTextWithShadow(textRenderer, Text.literal("NEX WEB INDEX  |  .ARENA SANDBOX"),
            left + 22, top + 57, 0xFF70D8FF);
        context.drawTextWithShadow(textRenderer, Text.literal("Type an instruction or choose a quick action."),
            left + 22, top + 73, 0xFFD0DCE6);
        context.drawTextWithShadow(textRenderer, Text.literal("NEX >"),
            left + 14, top + 108, 0xFF55E6D2);
        super.render(context, mouseX, mouseY, delta);
        context.drawTextWithShadow(textRenderer, Text.literal("All targets and ports are fictional game data."),
            left + 14, top + 215, 0xFF8295A8);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 && commandField != null && commandField.isFocused()) {
            submitField();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
