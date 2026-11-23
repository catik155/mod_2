package dev.catik.cyberarena.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Client-only fictional web browser and NEX workstation. */
public final class CyberArenaScreen extends Screen {
    private static final String[] SITES = {"shop.arena", "news.arena", "grid.arena", "vault.arena", "labs.arena"};
    private static final String[] TITLES = {"NEX MARKET", "ARENA DAILY", "GRID CONTROL", "VAULT ARCHIVE", "NEX LABS"};
    private static final String[] DESCRIPTIONS = {
        "Public exchange storefront. Basic public records and market bulletins.",
        "Public news relay. Network notices, contracts and security bulletins.",
        "Energy grid control. Infrastructure telemetry and protected records.",
        "Encrypted archive. High-value data packages behind advanced defenses.",
        "Research laboratory. Experimental data and expert-level security."
    };
    private static final String[] RISKS = {"LOW", "LOW", "MEDIUM", "HIGH", "EXPERT"};
    private static final String[][] FILE_NAMES = {
        {"catalog.idx", "offers.dat", "ledger.log"},
        {"bulletins.txt", "dispatch.log", "public.key"},
        {"load.map", "relay.cfg", "grid.log"},
        {"records.enc", "ledger.bak", "access.log"},
        {"research.dat", "prototype.idx", "lab-notes.txt"}
    };
    private static final String[] SERVICES = {
        "021 WEB-GATE / OPEN", "044 DATA-VAULT / GUARDED", "071 ADMIN-CORE / LOCKED"
    };
    private static final Set<String> BOOKMARKS = new LinkedHashSet<>();
    private static final List<String> HISTORY = new ArrayList<>();

    private final int page;
    private final String selectedSite;
    private final String query;
    private TextFieldWidget input;

    public CyberArenaScreen() { this(0, "", ""); }
    private CyberArenaScreen(int page, String selectedSite, String query) {
        super(Text.literal("Cyber Arena"));
        this.page = page;
        this.selectedSite = selectedSite;
        this.query = query;
    }

    private void navigate(int nextPage) {
        if (client != null) client.setScreen(new CyberArenaScreen(nextPage, "", ""));
    }
    private void openSite(String site) {
        HISTORY.remove(site);
        HISTORY.add(0, site);
        while (HISTORY.size() > 12) HISTORY.remove(HISTORY.size() - 1);
        if (client != null) client.setScreen(new CyberArenaScreen(3, site, ""));
    }
    private int siteIndex(String site) {
        for (int i = 0; i < SITES.length; i++) if (SITES[i].equals(site)) return i;
        return 0;
    }
    private List<String> visibleSites() {
        List<String> source = page == 4 ? new ArrayList<>(BOOKMARKS) : page == 5 ? new ArrayList<>(HISTORY) : List.of(SITES);
        if (query.isBlank() || page != 0) return source;
        String q = query.toLowerCase();
        return source.stream().filter(s -> s.contains(q) || TITLES[siteIndex(s)].toLowerCase().contains(q)
            || DESCRIPTIONS[siteIndex(s)].toLowerCase().contains(q)).toList();
    }

    @Override
    protected void init() {
        int panelWidth = Math.min(520, this.width - 24);
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(18, (this.height - 340) / 2);
        int tabWidth = (panelWidth - 36) / 3;
        addDrawableChild(ButtonWidget.builder(Text.literal("BROWSER"), b -> navigate(0))
            .dimensions(left + 12, top + 48, tabWidth, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("NEX TERMINAL"), b -> navigate(1))
            .dimensions(left + 18 + tabWidth, top + 48, tabWidth, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("AUTOMATION"), b -> navigate(2))
            .dimensions(left + 24 + tabWidth * 2, top + 48, tabWidth, 20).build());

        if (page == 0 || page == 3 || page == 4 || page == 5 || page == 6) initBrowser(left, top, panelWidth);
        else if (page == 1) initTerminal(left, top, panelWidth);
        else initAutomation(left, top, panelWidth);
    }

    private void initBrowser(int left, int top, int panelWidth) {
        addDrawableChild(ButtonWidget.builder(Text.literal("DIRECTORY"), b -> navigate(0))
            .dimensions(left + 14, top + 76, 105, 19).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("BOOKMARKS " + BOOKMARKS.size()), b -> navigate(4))
            .dimensions(left + 125, top + 76, 105, 19).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("HISTORY"), b -> navigate(5))
            .dimensions(left + 236, top + 76, 105, 19).build());

        if (page == 3) {
            initSitePage(left, top, panelWidth);
            return;
        }
        if (page == 6) {
            initFilePage(left, top, panelWidth);
            return;
        }
        if (page == 0) {
            input = new TextFieldWidget(textRenderer, left + 14, top + 113, panelWidth - 128, 20, Text.literal("Search"));
            input.setMaxLength(48);
            input.setPlaceholder(Text.literal("Search fictional .arena sites..."));
            input.setText(query);
            addDrawableChild(input);
            addDrawableChild(ButtonWidget.builder(Text.literal("FIND"), b -> {
                String q = input.getText().trim();
                if (client != null) client.setScreen(new CyberArenaScreen(0, "", q));
            }).dimensions(left + panelWidth - 106, top + 113, 92, 20).build());
        }
        List<String> sites = visibleSites();
        int rowY = top + (page == 0 ? 146 : 111);
        for (int i = 0; i < Math.min(5, sites.size()); i++) {
            String site = sites.get(i);
            int idx = siteIndex(site);
            int y = rowY + i * 35;
            contextlessSiteRow(left, panelWidth, y, site, idx);
        }
        if (sites.isEmpty()) {
            addDrawableChild(ButtonWidget.builder(Text.literal("CLEAR SEARCH"), b -> navigate(0))
                .dimensions(left + 14, rowY + 4, 120, 20).build());
        }
    }

    private void contextlessSiteRow(int left, int panelWidth, int y, String site, int index) {
        addDrawableChild(ButtonWidget.builder(Text.literal("OPEN"), b -> openSite(site))
            .dimensions(left + panelWidth - 184, y, 76, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("PORTS"), b -> runNex("PORTA " + site))
            .dimensions(left + panelWidth - 102, y, 88, 20).build());
    }

    private void initSitePage(int left, int top, int panelWidth) {
        addDrawableChild(ButtonWidget.builder(Text.literal("< BACK"), b -> navigate(0))
            .dimensions(left + 14, top + 78, 90, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal(BOOKMARKS.contains(selectedSite) ? "UNBOOKMARK" : "BOOKMARK"), b -> {
            if (BOOKMARKS.contains(selectedSite)) BOOKMARKS.remove(selectedSite);
            else BOOKMARKS.add(selectedSite);
            if (client != null) client.setScreen(new CyberArenaScreen(3, selectedSite, ""));
        }).dimensions(left + panelWidth - 126, top + 78, 112, 20).build());
        int third = (panelWidth - 36) / 3;
        addDrawableChild(ButtonWidget.builder(Text.literal("PORTS"), b -> runNex("PORTA " + selectedSite))
            .dimensions(left + 14, top + 292, third, 22).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("EXTRACT"), b -> runNex("CRACK " + selectedSite))
            .dimensions(left + 22 + third, top + 292, third, 22).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("FILES"), b -> {
            if (client != null) client.setScreen(new CyberArenaScreen(6, selectedSite, ""));
        }).dimensions(left + 30 + third * 2, top + 292, third, 22).build());
    }

    private void initFilePage(int left, int top, int panelWidth) {
        int idx = siteIndex(selectedSite);
        addDrawableChild(ButtonWidget.builder(Text.literal("< SITE PAGE"), b -> openSite(selectedSite))
            .dimensions(left + 14, top + 78, 110, 20).build());
        for (int i = 0; i < FILE_NAMES[idx].length; i++) {
            final String file = FILE_NAMES[idx][i];
            int y = top + 143 + i * 43;
            addDrawableChild(ButtonWidget.builder(Text.literal("READ"), b -> runNex("READ " + selectedSite + " " + file))
                .dimensions(left + panelWidth - 94, y, 80, 22).build());
        }
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
            int col = i % 2, row = i / 2;
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
        addDrawableChild(ButtonWidget.builder(Text.literal("DISCOVERY ROUTINE"), b -> runNex("FLOW SEEK arena; NODR; STAT"))
            .dimensions(left + 14, top + 166, panelWidth - 28, 22).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("RESOURCE ROUTINE"), b -> runNex("FLOW MINE; SELR; STAT"))
            .dimensions(left + 14, top + 194, panelWidth - 28, 22).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("SITE AUDIT ROUTINE"), b -> runNex("FLOW PORTA shop.arena; PORTA news.arena; PORTA grid.arena"))
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
        int top = Math.max(18, (this.height - 340) / 2);
        context.fill(left, top, left + panelWidth, top + 340, 0xEE101923);
        context.fill(left, top, left + panelWidth, top + 3, 0xFF29D6C5);
        context.drawTextWithShadow(textRenderer, Text.literal("CYBER ARENA // OPERATOR WORKSTATION"), left + 14, top + 13, 0xFF55E6D2);
        context.drawTextWithShadow(textRenderer, Text.literal("NEX SANDBOX / OFFLINE FICTIONAL WEB"), left + 14, top + 29, 0xFFB8C8D8);

        if (page == 6) {
            int idx = siteIndex(selectedSite);
            context.drawTextWithShadow(textRenderer, Text.literal("ARENA:// " + selectedSite + " / FILE INDEX"), left + 14, top + 108, 0xFF70D8FF);
            for (int i = 0; i < FILE_NAMES[idx].length; i++) {
                int y = top + 143 + i * 43;
                context.fill(left + 14, y - 4, left + panelWidth - 14, y + 26, 0xFF172634);
                context.drawTextWithShadow(textRenderer, Text.literal(FILE_NAMES[idx][i]), left + 22, y + 5, 0xFFBDEBFF);
                context.drawTextWithShadow(textRenderer, Text.literal("FICTIONAL RECORD / READ-ONLY METADATA"), left + 22, y + 16, 0xFF8295A8);
            }
        } else if (page == 3) {
            int idx = siteIndex(selectedSite);
            context.drawTextWithShadow(textRenderer, Text.literal("ARENA:// " + selectedSite), left + 14, top + 116, 0xFF70D8FF);
            context.drawTextWithShadow(textRenderer, Text.literal(TITLES[idx]), left + 14, top + 139, 0xFFBDEBFF);
            drawWrapped(context, DESCRIPTIONS[idx], left + 14, top + 160, panelWidth - 28, 0xFFD0DCE6);
            context.drawTextWithShadow(textRenderer, Text.literal("SECURITY CLASS: " + RISKS[idx]), left + 14, top + 207, 0xFFFFC66D);
            context.drawTextWithShadow(textRenderer, Text.literal("SIMULATED SERVICE SLOTS"), left + 14, top + 218, 0xFF70D8FF);
            for (int i = 0; i < SERVICES.length; i++)
                context.drawTextWithShadow(textRenderer, Text.literal(SERVICES[i]), left + 22, top + 255 + i * 13, i == 0 ? 0xFF79E6A4 : 0xFFFFC66D);
        } else if (page == 0 || page == 4 || page == 5) {
            String heading = page == 4 ? "SAVED BOOKMARKS" : page == 5 ? "RECENTLY VISITED" : "VIRTUAL WEB DIRECTORY";
            context.drawTextWithShadow(textRenderer, Text.literal(heading), left + 14, top + 98, 0xFF70D8FF);
            List<String> sites = visibleSites();
            int rowY = top + (page == 0 ? 137 : 111);
            for (int i = 0; i < Math.min(5, sites.size()); i++) {
                int idx = siteIndex(sites.get(i)), y = rowY + i * 35;
                context.fill(left + 14, y - 3, left + panelWidth - 14, y + 27, 0xFF172634);
                context.drawTextWithShadow(textRenderer, Text.literal(sites.get(i) + " / " + RISKS[idx]), left + 22, y + 1, 0xFFBDEBFF);
                context.drawTextWithShadow(textRenderer, Text.literal(TITLES[idx]), left + 22, y + 14, 0xFF8295A8);
            }
            if (sites.isEmpty()) context.drawTextWithShadow(textRenderer, Text.literal("No entries yet. Open a site or change your search."), left + 18, rowY + 12, 0xFF8295A8);
        } else if (page == 1) {
            context.drawTextWithShadow(textRenderer, Text.literal("NEX > // PSEUDO-LANGUAGE CONSOLE"), left + 14, top + 78, 0xFF70D8FF);
            context.drawTextWithShadow(textRenderer, Text.literal("Server validates every instruction before applying game state."), left + 14, top + 235, 0xFFD0DCE6);
        } else {
            context.drawTextWithShadow(textRenderer, Text.literal("NEX FLOW // SAFE AUTOMATION ROUTINES"), left + 14, top + 78, 0xFF70D8FF);
            context.drawTextWithShadow(textRenderer, Text.literal("Separate instructions with ; | Up to 6 steps per run"), left + 14, top + 258, 0xFFD0DCE6);
            context.drawTextWithShadow(textRenderer, Text.literal("Whitelisted NEX operations only; no external commands."), left + 14, top + 274, 0xFF8295A8);
        }
        super.render(context, mouseX, mouseY, delta);
        context.drawTextWithShadow(textRenderer, Text.literal("All sites, files and services are fictional game data."), left + 14, top + 321, 0xFF8295A8);
    }

    private void drawWrapped(DrawContext context, String value, int x, int y, int width, int color) {
        String remaining = value;
        int lineY = y;
        while (!remaining.isEmpty()) {
            int end = Math.min(remaining.length(), textRenderer.getWidth(remaining) <= width ? remaining.length() : 1);
            while (end < remaining.length() && textRenderer.getWidth(remaining.substring(0, end + 1)) <= width) end++;
            if (end < remaining.length()) {
                int space = remaining.lastIndexOf(' ', end);
                if (space > 0) end = space;
            }
            context.drawTextWithShadow(textRenderer, Text.literal(remaining.substring(0, end)), x, lineY, color);
            remaining = remaining.substring(end).trim();
            lineY += 12;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 && input != null && input.isFocused()) {
            if (page == 0) {
                String q = input.getText().trim();
                if (client != null) client.setScreen(new CyberArenaScreen(0, "", q));
            } else if (page == 1) submit("arena nex ");
            else if (page == 2) submit("arena nex FLOW ");
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    @Override public boolean shouldPause() { return false; }
}
