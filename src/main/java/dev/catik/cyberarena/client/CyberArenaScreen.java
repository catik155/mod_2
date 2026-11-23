package dev.catik.cyberarena.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

/** Client-only browser and NEX workstation. All game operations are sent to and validated by the server. */
public final class CyberArenaScreen extends Screen {
    private static final String[] SITES = {"shop.arena", "news.arena", "grid.arena", "vault.arena", "labs.arena"};
    private static final String[] DESCRIPTIONS = {
        "Public exchange storefront / low risk",
        "Public news relay / low risk",
        "Energy grid control / medium risk",
        "Encrypted archive / high risk",
        "Research laboratory / expert risk"
    };
    private final int page;
    private TextFieldWidget input;

    public CyberArenaScreen() { this(0); }
    private CyberArenaScreen(int page) {
        super(Text.literal("Cyber Arena"));
        this.page = page;
    }

    private void navigate(int nextPage) {
        if (client != null) client.setScreen(new CyberArenaScreen(nextPage));
    }

    @Override
    protected void init() {
        int panelWidth = Math.min(520, this.width - 24);
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(18, (this.height - 330) / 2);
        int tabY = top + 48;
        int tabWidth = (panelWidth - 36) / 3;
        addDrawableChild(ButtonWidget.builder(Text.literal("BROWSER"), b -> navigate(0))
            .dimensions(left + 12, tabY, tabWidth, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("NEX TERMINAL"), b -> navigate(1))
            .dimensions(left + 18 + tabWidth, tabY, tabWidth, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("AUTOMATION"), b -> navigate(2))
            .dimensions(left + 24 + tabWidth * 2, tabY, tabWidth, 20).build());

        if (page == 0) initBrowser(left, top, panelWidth);
        else if (page == 1) initTerminal(left, top, panelWidth);
        else initAutomation(left, top, panelWidth);
    }

    private void initBrowser(int left, int top, int panelWidth) {
        input = new TextFieldWidget(textRenderer, left + 14, top + 91, panelWidth - 28, 20, Text.literal("Search .arena"));
        input.setMaxLength(48);
        input.setPlaceholder(Text.literal("Search fictional sites..."));
        addDrawableChild(input);
        addDrawableChild(ButtonWidget.builder(Text.literal("SEARCH INDEX"), b -> {
            String query = input.getText().trim();
            if (!query.isEmpty()) runNex("SEEK " + query);
        }).dimensions(left + 14, top + 116, panelWidth - 28, 20).build());

        int rowY = top + 151;
        for (int i = 0; i < SITES.length; i++) {
            final String site = SITES[i];
            int y = rowY + i * 31;
            contextlessSiteRow(left, panelWidth, y, site, i);
        }
    }

    private void contextlessSiteRow(int left, int panelWidth, int y, String site, int index) {
        int nameWidth = Math.max(110, panelWidth - 206);
        addDrawableChild(ButtonWidget.builder(Text.literal("VIEW"), b -> runNex("PORTA " + site))
            .dimensions(left + panelWidth - 184, y, 76, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("EXTRACT"), b -> runNex("CRACK " + site))
            .dimensions(left + panelWidth - 102, y, 88, 20).build());
    }

    private void initTerminal(int left, int top, int panelWidth) {
        input = new TextFieldWidget(textRenderer, left + 14, top + 99, panelWidth - 28, 20, Text.literal("NEX instruction"));
        input.setMaxLength(160);
        input.setPlaceholder(Text.literal("MANU / SEEK vault / PORTA vault.arena ..."));
        addDrawableChild(input);
        addDrawableChild(ButtonWidget.builder(Text.literal("EXECUTE NEX"), b -> submit("arena nex "))
            .dimensions(left + 14, top + 128, (panelWidth - 36) / 2, 22).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("MANUAL"), b -> runNex("MANU"))
            .dimensions(left + 22 + (panelWidth - 36) / 2, top + 128, (panelWidth - 36) / 2, 22).build());
        String[] commands = {"STAT", "NODR", "CONTRACTS", "MINE", "SELR", "UPGR stealth"};
        for (int i = 0; i < commands.length; i++) {
            final String command = commands[i];
            int col = i % 2;
            int row = i / 2;
            addDrawableChild(ButtonWidget.builder(Text.literal(command), b -> runNex(command))
                .dimensions(left + 14 + col * ((panelWidth - 36) / 2 + 8), top + 166 + row * 27, (panelWidth - 36) / 2, 21).build());
        }
    }

    private void initAutomation(int left, int top, int panelWidth) {
        input = new TextFieldWidget(textRenderer, left + 14, top + 100, panelWidth - 28, 20, Text.literal("NEX FLOW routine"));
        input.setMaxLength(240);
        input.setPlaceholder(Text.literal("SEEK vault; PORTA vault.arena; MINE"));
        addDrawableChild(input);
        addDrawableChild(ButtonWidget.builder(Text.literal("RUN ROUTINE"), b -> submit("arena nex FLOW "))
            .dimensions(left + 14, top + 130, panelWidth - 28, 22).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("DISCOVERY ROUTINE"), b ->
            runNex("FLOW SEEK arena; NODR; STAT"))
            .dimensions(left + 14, top + 166, panelWidth - 28, 22).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("RESOURCE ROUTINE"), b ->
            runNex("FLOW MINE; SELR; STAT"))
            .dimensions(left + 14, top + 194, panelWidth - 28, 22).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("SITE AUDIT ROUTINE"), b ->
            runNex("FLOW PORTA shop.arena; PORTA news.arena; PORTA grid.arena"))
            .dimensions(left + 14, top + 222, panelWidth - 28, 22).build());
    }

    private void submit(String prefix) {
        String value = input == null ? "" : input.getText().trim();
        if (!value.isEmpty()) send(prefix + value);
    }

    private void runNex(String instruction) { send("arena nex " + instruction); }

    private void send(String command) {
        if (client == null || client.player == null || client.getNetworkHandler() == null) return;
        client.getNetworkHandler().sendChatCommand(command);
        close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        int panelWidth = Math.min(520, this.width - 24);
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(18, (this.height - 330) / 2);
        context.fill(left, top, left + panelWidth, top + 330, 0xEE101923);
        context.fill(left, top, left + panelWidth, top + 3, 0xFF29D6C5);
        context.drawTextWithShadow(textRenderer, Text.literal("CYBER ARENA // OPERATOR WORKSTATION"),
            left + 14, top + 13, 0xFF55E6D2);
        context.drawTextWithShadow(textRenderer, Text.literal("NEX SANDBOX  /  OFFLINE FICTIONAL WEB"),
            left + 14, top + 29, 0xFFB8C8D8);
        if (page == 0) {
            context.drawTextWithShadow(textRenderer, Text.literal("VIRTUAL WEB INDEX"), left + 14, top + 78, 0xFF70D8FF);
            for (int i = 0; i < SITES.length; i++) {
                int y = top + 156 + i * 31;
                context.fill(left + 14, y - 3, left + panelWidth - 14, y + 22, 0xFF172634);
                context.drawTextWithShadow(textRenderer, Text.literal(SITES[i]), left + 22, y + 3, 0xFFBDEBFF);
                context.drawTextWithShadow(textRenderer, Text.literal(DESCRIPTIONS[i]), left + 22, y + 13, 0xFF8295A8);
            }
        } else if (page == 1) {
            context.drawTextWithShadow(textRenderer, Text.literal("NEX > // ENTER A PSEUDO-LANGUAGE INSTRUCTION"),
                left + 14, top + 78, 0xFF70D8FF);
            context.drawTextWithShadow(textRenderer, Text.literal("Commands are interpreted by the server-side NEX engine."),
                left + 14, top + 235, 0xFFD0DCE6);
        } else {
            context.drawTextWithShadow(textRenderer, Text.literal("NEX FLOW // SAFE AUTOMATION ROUTINES"),
                left + 14, top + 78, 0xFF70D8FF);
            context.drawTextWithShadow(textRenderer, Text.literal("Separate instructions with ;  |  Up to 6 steps per run"),
                left + 14, top + 238, 0xFFD0DCE6);
            context.drawTextWithShadow(textRenderer, Text.literal("Allowed: SEEK PORTA CRACK MINE UPGR SELR SCAN STAT NODR"),
                left + 14, top + 254, 0xFF8295A8);
        }
        super.render(context, mouseX, mouseY, delta);
        context.drawTextWithShadow(textRenderer, Text.literal("All sites, files and services are fictional game data."),
            left + 14, top + 311, 0xFF8295A8);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 && input != null && input.isFocused()) {
            if (page == 0) {
                String query = input.getText().trim();
                if (!query.isEmpty()) runNex("SEEK " + query);
            } else if (page == 1) submit("arena nex ");
            else submit("arena nex FLOW ");
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override public boolean shouldPause() { return false; }
}
