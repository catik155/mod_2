package dev.catik.cyberarena.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Client-only fictional web browser and NEX workstation. */
public final class CyberArenaScreen extends Screen {
    private static final String[] SITES = {
        "shop.arena", "news.arena", "grid.arena", "vault.arena", "labs.arena", "forum.arena", "bank.arena", "metro.arena", "weather.arena", "clinic.arena", "factory.arena", "museum.arena", "uni.arena", "port.arena", "security.arena", "casino.arena", "farm.arena", "media.arena", "space.arena", "auction.arena",
        "craft.arena", "music.arena", "travel.arena", "legal.arena", "archive2.arena", "robot.arena", "food.arena", "power.arena", "sports.arena", "social.arena"
    };
    private static final String[] TITLES = {
        "NEX MARKET", "ARENA DAILY", "GRID CONTROL", "VAULT ARCHIVE", "NEX LABS", "OPEN FORUM", "ARENA BANK", "METRO LINK", "SKYCAST", "CIVIC CLINIC", "FACTORIUM", "DIGITAL MUSEUM", "ARENA CAMPUS", "HARBOR CONTROL", "SENTINEL", "LUCKY BYTE", "GREENFIELD", "PIXEL PRESS", "STARLAB", "BIDNET",
        "PIXEL CRAFT", "SONIC ARCHIVE", "VOYAGER", "LEXICON", "MEMORY BANK", "AUTOMATA", "PANTRY", "VOLTAGE", "ARENA SPORTS", "SOCIAL BYTE"
    };
    private static final String[] DESCRIPTIONS = {
        "Public exchange storefront and market bulletins.", "Public news relay, contracts and security bulletins.", "Energy grid control and infrastructure telemetry.", "Encrypted archive with high-value protected data.", "Research laboratory and experimental records.", "Community message board with public threads.", "Fictional finance exchange and protected ledgers.", "Transit network with station maps and timetables.", "Weather simulation with forecasts and alerts.", "Medical research portal and supply manifests.", "Industrial production, robotics and assembly records.", "Public cultural archive and exhibit catalog.", "Education network, courses and library records.", "Logistics hub with cargo manifests and berth schedules.", "Advanced defense simulation with sensors and decoys.", "Entertainment network with games and simulated payouts.", "Agriculture network with harvest and storage reports.", "Media studio with stories and broadcast schedules.", "Space research with orbital telemetry and mission archives.", "Auction exchange with lots, bids and settlements.",
        "Crafting recipes, build catalogs and guild projects.", "Fictional tracks, sound packs and audio event schedules.", "Travel destinations, routes and expedition reports.", "Court cases, public rules and simulated legal records.", "Historical records, timelines and recovered fragments.", "Robotics research, machine profiles and test schedules.", "Food supply, recipe index and delivery orders.", "Power plant simulator, energy schedules and load records.", "League standings, match reports and event calendars.", "Profiles of fictional NPC groups, posts and reputation boards."
    };
    private static final String[] SITE_CONTENT = {
        "Market board: starter tools, rotating offers, trader notes.", "Daily feed: city headlines, event notes, public alerts.", "Grid status: district load, relay notices, maintenance windows.", "Vault landing: archive categories, access notices, recovery hints.", "Lab portal: experiments, prototype summaries, researcher notes.", "Forum: public threads, guild discussions, community rules.", "Bank: exchange rates, fictional accounts, transaction notices.", "Metro: station map, timetable, service disruptions.", "Weather: forecast, climate map, storm advisories.", "Clinic: research topics, supply lists, appointment notices.", "Factory: assembly lines, robot status, shift schedule.", "Museum: exhibits, visitor guide, archive highlights.", "Campus: courses, library shelf, campus map.", "Port: cargo board, berth schedule, logistics notices.", "Sentinel: defense bulletin, sensor status, decoy warnings.", "Casino: game catalog, odds notes, payout schedule.", "Farm: harvest calendar, silo stock and weather report.", "Media: latest stories, studio schedule, broadcast notes.", "Space: mission board, orbital map, telemetry highlights.", "Auction: current lots, bid history, settlement notices.",
        "Crafting recipes: wooden tools, stone kit, lantern set; guild build ideas.", "Audio catalog: ambient packs, event jingles, sound design notes.", "Travel board: forest camps, mountain routes, expedition checklists.", "Legal library: arena rules, case summaries, public verdicts.", "Timeline: ancient ruins, server milestones, recovered history fragments.", "Robot catalog: helper bots, test results, factory prototypes.", "Recipe pantry: bread, stew, baked potatoes; ingredient lists and delivery menu.", "Power dashboard: plant output, energy forecast, planned maintenance.", "Sports center: league table, match summaries, upcoming arena events.", "Social feed: NPC groups, community posts, reputation trends."
    };
    private static final String[] RISKS = {
        "LOW", "LOW", "MEDIUM", "HIGH", "EXPERT", "LOW", "HIGH", "MEDIUM", "LOW", "MEDIUM", "HIGH", "LOW", "MEDIUM", "MEDIUM", "EXPERT", "HIGH", "LOW", "MEDIUM", "EXPERT", "HIGH",
        "LOW", "MEDIUM", "LOW", "HIGH", "MEDIUM", "EXPERT", "LOW", "HIGH", "MEDIUM", "HIGH"
    };
    private static final String[][] FILE_NAMES = {
        {"catalog.idx", "offers.dat", "ledger.log"},
        {"bulletins.txt", "dispatch.log", "public.key"},
        {"load.map", "relay.cfg", "grid.log"},
        {"records.enc", "ledger.bak", "access.log"},
        {"research.dat", "prototype.idx", "lab-notes.txt"},
        {"threads.idx", "members.db", "rules.txt"},
        {"rates.dat", "accounts.enc", "audit.log"},
        {"timetable.dat", "stations.map", "signals.log"},
        {"forecast.dat", "climate.map", "alerts.log"},
        {"research.idx", "supply.dat", "patient.enc"},
        {"assembly.cfg", "robotics.dat", "shift.log"},
        {"exhibits.idx", "archive.dat", "visitors.log"},
        {"courses.idx", "library.db", "campus.map"},
        {"cargo.idx", "berths.map", "manifest.enc"},
        {"rules.cfg", "sensors.map", "incident.log"},
        {"games.idx", "payouts.dat", "vault.enc"},
        {"harvest.map", "silos.dat", "weather.log"},
        {"stories.idx", "studio.cfg", "broadcast.log"},
        {"telemetry.dat", "orbit.map", "mission.enc"},
        {"lots.idx", "bids.dat", "settlement.log"},
        {"recipes.idx", "blueprints.dat", "guild.log"},
        {"tracks.idx", "soundpacks.dat", "studio.log"},
        {"routes.map", "destinations.idx", "travel.log"},
        {"codes.idx", "cases.dat", "court.log"},
        {"timeline.map", "fragments.dat", "catalog.log"},
        {"robots.idx", "tests.dat", "factory.log"},
        {"recipes.idx", "supply.dat", "orders.log"},
        {"plants.map", "energy.dat", "load.log"},
        {"leagues.idx", "matches.dat", "events.log"},
        {"groups.idx", "posts.dat", "reputation.log"}
    };
    private static final String[] SERVICES = {
        "021 WEB-GATE / OPEN", "044 DATA-VAULT / GUARDED", "071 ADMIN-CORE / LOCKED"
    };
    private static final Set<String> BOOKMARKS = new LinkedHashSet<>();
    private static final List<String> HISTORY = new ArrayList<>();
    private static int directoryPage = 0;
    private static boolean voraSelected = false;
    private static final int SITES_PER_PAGE = 5;

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
        addDrawableChild(ButtonWidget.builder(Text.literal("БРАУЗЕР"), b -> navigate(0))
            .dimensions(left + 12, top + 48, tabWidth, 20).tooltip(Tooltip.of(Text.literal("Открыть виртуальные сайты, поиск, закладки и историю"))).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("КОНСОЛЬ"), b -> navigate(1))
            .dimensions(left + 18 + tabWidth, top + 48, tabWidth, 20).tooltip(Tooltip.of(Text.literal("Открыть консоль и вводить команды на NX или FLEX"))).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("АВТОМАТИЗАЦИЯ"), b -> navigate(2))
            .dimensions(left + 24 + tabWidth * 2, top + 48, tabWidth, 20).tooltip(Tooltip.of(Text.literal("Покупаемый модуль: приобретите AUTOM за 35 кредитов, чтобы открыть готовые сценарии и FLOW"))).build());

        if (page == 0 || page == 3 || page == 4 || page == 5 || page == 6) initBrowser(left, top, panelWidth);
        else if (page == 1) initTerminal(left, top, panelWidth);
        else if (page == 7) initSiteConsole(left, top, panelWidth);
        else initAutomation(left, top, panelWidth);
    }

    private void initBrowser(int left, int top, int panelWidth) {
        addDrawableChild(ButtonWidget.builder(Text.literal("КАТАЛОГ"), b -> navigate(0))
            .dimensions(left + 14, top + 76, 105, 19).tooltip(Tooltip.of(Text.literal("Вернуться к списку сайтов"))).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("ЗАКЛАДКИ " + BOOKMARKS.size()), b -> navigate(4))
            .dimensions(left + 125, top + 76, 105, 19).tooltip(Tooltip.of(Text.literal("Показать сайты, сохранённые в закладки"))).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("ИСТОРИЯ"), b -> navigate(5))
            .dimensions(left + 236, top + 76, 105, 19).tooltip(Tooltip.of(Text.literal("Показать недавно открытые сайты"))).build());

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
            addDrawableChild(ButtonWidget.builder(Text.literal("НАЙТИ"), b -> {
                String q = input.getText().trim();
                if (client != null) client.setScreen(new CyberArenaScreen(0, "", q));
            }).dimensions(left + panelWidth - 106, top + 113, 92, 20).tooltip(Tooltip.of(Text.literal("Найти сайт по имени или описанию"))).build());
        }
        List<String> sites = visibleSites();
        int pages = Math.max(1, (sites.size() + SITES_PER_PAGE - 1) / SITES_PER_PAGE);
        directoryPage = Math.max(0, Math.min(directoryPage, pages - 1));
        int rowY = top + (page == 0 ? 146 : 111);
        int offset = page == 0 ? directoryPage * SITES_PER_PAGE : 0;
        for (int i = offset; i < Math.min(offset + SITES_PER_PAGE, sites.size()); i++) {
            String site = sites.get(i);
            int idx = siteIndex(site);
            int y = rowY + (i - offset) * 35;
            contextlessSiteRow(left, panelWidth, y, site, idx);
        }
        if (sites.isEmpty()) {
            addDrawableChild(ButtonWidget.builder(Text.literal("СБРОСИТЬ ПОИСК"), b -> navigate(0))
                .dimensions(left + 14, rowY + 4, 120, 20).build());
        }
        if (page == 0 && pages > 1) {
            addDrawableChild(ButtonWidget.builder(Text.literal("← ПРЕД."), b -> { directoryPage = Math.max(0, directoryPage - 1); if (client != null) client.setScreen(new CyberArenaScreen(0, "", query)); }).dimensions(left + 14, top + 318, 72, 16).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("СЛЕД. →"), b -> { directoryPage = Math.min(pages - 1, directoryPage + 1); if (client != null) client.setScreen(new CyberArenaScreen(0, "", query)); }).dimensions(left + panelWidth - 86, top + 318, 72, 16).build());
        }
    }

    private void contextlessSiteRow(int left, int panelWidth, int y, String site, int index) {
        addDrawableChild(ButtonWidget.builder(Text.literal("OPEN"), b -> openSite(site))
            .dimensions(left + panelWidth - 184, y, 76, 20).tooltip(Tooltip.of(Text.literal("Открыть страницу сайта"))).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("СЛУЖБЫ"), b -> runNex("PORTA " + site))
            .dimensions(left + panelWidth - 102, y, 88, 20).tooltip(Tooltip.of(Text.literal("Посмотреть игровые службы сайта через консоль"))).build());
    }

    private void initSitePage(int left, int top, int panelWidth) {
        addDrawableChild(ButtonWidget.builder(Text.literal("← НАЗАД"), b -> navigate(0))
            .dimensions(left + 14, top + 78, 90, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("NX КОНСОЛЬ САЙТА"), b -> {
            if (client != null) client.setScreen(new CyberArenaScreen(7, selectedSite, ""));
        }).dimensions(left + panelWidth - 126, top + 78, 112, 20)
            .tooltip(Tooltip.of(Text.literal("Открыть отдельную NX-консоль, привязанную к этому сайту"))).build());
        addDrawableChild(ButtonWidget.builder(Text.literal(BOOKMARKS.contains(selectedSite) ? "УБРАТЬ ЗАКЛАДКУ" : "В ЗАКЛАДКИ"), b -> {
            if (BOOKMARKS.contains(selectedSite)) BOOKMARKS.remove(selectedSite);
            else BOOKMARKS.add(selectedSite);
            if (client != null) client.setScreen(new CyberArenaScreen(3, selectedSite, ""));
        }).dimensions(left + 110, top + 78, 118, 20).tooltip(Tooltip.of(Text.literal("Сохранить страницу в закладках"))).build());
        int third = (panelWidth - 36) / 3;
        addDrawableChild(ButtonWidget.builder(Text.literal("СЛУЖБЫ"), b -> runNex("PORTA " + selectedSite))
            .dimensions(left + 14, top + 292, third, 22).tooltip(Tooltip.of(Text.literal("Просмотреть виртуальные службы сайта"))).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("ИЗВЛЕЧЬ"), b -> runNex("CRACK " + selectedSite))
            .dimensions(left + 22 + third, top + 292, third, 22).tooltip(Tooltip.of(Text.literal("Запустить игровое извлечение данных с сайта"))).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("ФАЙЛЫ"), b -> {
            if (client != null) client.setScreen(new CyberArenaScreen(6, selectedSite, ""));
        }).dimensions(left + 30 + third * 2, top + 292, third, 22).tooltip(Tooltip.of(Text.literal("Открыть список файлов и читать игровые записи"))).build());
    }

    private void initSiteConsole(int left, int top, int panelWidth) {
        addDrawableChild(ButtonWidget.builder(Text.literal("← К САЙТУ"), b -> openSite(selectedSite))
            .dimensions(left + 14, top + 76, 105, 20)
            .tooltip(Tooltip.of(Text.literal("Вернуться к странице сайта"))).build());
        input = new TextFieldWidget(textRenderer, left + 14, top + 105, panelWidth - 28, 20, Text.literal("NX-команда сайта"));
        input.setMaxLength(160);
        input.setPlaceholder(Text.literal("Команда NX — сайт добавится автоматически"));
        addDrawableChild(input);
        addDrawableChild(ButtonWidget.builder(Text.literal("ВЫПОЛНИТЬ NX"), b -> submitSiteConsole())
            .dimensions(left + 14, top + 134, (panelWidth - 36) / 2, 22)
            .tooltip(Tooltip.of(Text.literal("Выполнить команду с контекстом открытого сайта"))).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("СЛУЖБЫ САЙТА"), b -> runNex("PORTA " + selectedSite))
            .dimensions(left + 22 + (panelWidth - 36) / 2, top + 134, (panelWidth - 36) / 2, 22)
            .tooltip(Tooltip.of(Text.literal("Просмотреть службы текущего сайта"))).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("ИЗВЛЕЧЕНИЕ"), b -> runNex("CRACK " + selectedSite))
            .dimensions(left + 14, top + 166, panelWidth - 28, 22)
            .tooltip(Tooltip.of(Text.literal("Запустить игровое извлечение для текущего сайта"))).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("ФАЙЛЫ САЙТА"), b -> { if (client != null) client.setScreen(new CyberArenaScreen(6, selectedSite, "")); })
            .dimensions(left + 14, top + 194, panelWidth - 28, 22)
            .tooltip(Tooltip.of(Text.literal("Открыть игровые записи этого сайта"))).build());
    }
    private void submitSiteConsole() {
        String value = input == null ? "" : input.getText().trim();
        if (value.isEmpty()) return;
        String[] parts = value.split("\\s+", 2);
        String head = parts[0].toUpperCase();
        String args = parts.length > 1 ? parts[1].trim() : "";
        String command = value;
        if (List.of("PORTA", "PORTS", "CRACK", "FILES", "LS", "WHO", "WHOIS", "PING").contains(head)) {
            if (args.isEmpty()) command = head + " " + selectedSite;
        } else if (head.equals("READ") && !args.isEmpty()) {
            if (!args.regionMatches(true, 0, selectedSite, 0, selectedSite.length()))
                command = "READ " + selectedSite + " " + args;
        }
        send("arena nex " + command);
    }

    private void initFilePage(int left, int top, int panelWidth) {
        int idx = siteIndex(selectedSite);
        addDrawableChild(ButtonWidget.builder(Text.literal("← САЙТ"), b -> openSite(selectedSite))
            .dimensions(left + 14, top + 78, 110, 20).build());
        for (int i = 0; i < FILE_NAMES[idx].length; i++) {
            final String file = FILE_NAMES[idx][i];
            int y = top + 143 + i * 43;
            addDrawableChild(ButtonWidget.builder(Text.literal("READ"), b -> runNex("READ " + selectedSite + " " + file))
                .dimensions(left + panelWidth - 94, y, 80, 22).build());
        }
    }

    private void initTerminal(int left, int top, int panelWidth) {
        addDrawableChild(ButtonWidget.builder(Text.literal(voraSelected ? "ЯЗЫК: FLEX (АКТИВЕН)" : "ЯЗЫК: NX (АКТИВЕН)"), b -> {
            voraSelected = !voraSelected;
            if (client != null) client.setScreen(new CyberArenaScreen(1, "", ""));
        }).dimensions(left + 14, top + 76, panelWidth - 28, 20).build());
        input = new TextFieldWidget(textRenderer, left + 14, top + 103, panelWidth - 28, 20, Text.literal("Command"));
        input.setMaxLength(160);
        input.setPlaceholder(Text.literal(voraSelected ? "LUMA / KERI vault / DORA vault.arena ..." : "MANU / SEEK vault / PORTA vault.arena ..."));
        addDrawableChild(input);
        addDrawableChild(ButtonWidget.builder(Text.literal("ВЫПОЛНИТЬ " + (voraSelected ? "FLEX" : "NX")), b -> submit("arena nex "))
            .dimensions(left + 14, top + 132, (panelWidth - 36) / 2, 22).tooltip(Tooltip.of(Text.literal("Отправить введённую команду в игровую консоль"))).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("СПРАВКА / МАНУАЛ"), b -> runNex("MANU"))
            .dimensions(left + 22 + (panelWidth - 36) / 2, top + 132, (panelWidth - 36) / 2, 22).tooltip(Tooltip.of(Text.literal("Список команд и синтаксис выбранного языка"))).build());
        String[] commands = voraSelected ? new String[]{"MIRA", "NAVA", "JOBS", "DARA", "SELI", "UPRA stealth"} : new String[]{"STAT", "NODR", "CONTRACTS", "MINE", "SELR", "UPGR stealth"};
        for (int i = 0; i < commands.length; i++) {
            final String command = commands[i];
            int col = i % 2, row = i / 2;
            addDrawableChild(ButtonWidget.builder(Text.literal(command), b -> runNex(translateVora(command)))
                .dimensions(left + 14 + col * ((panelWidth - 36) / 2 + 8), top + 169 + row * 27, (panelWidth - 36) / 2, 21).tooltip(Tooltip.of(Text.literal("Быстрый запуск команды " + command + " — наведите, чтобы увидеть подсказку"))).build());
        }
    }

    private String translateVora(String text) {
        if (!voraSelected) return text;
        String[] p = text.trim().split("\\s+", 2);
        String head = p[0].toUpperCase(java.util.Locale.ROOT);
        String mapped = switch (head) {
            case "LUMA" -> "MANU"; case "KERI" -> "SEEK"; case "DORA" -> "PORTA";
            case "NAVA" -> "NODR"; case "MIRA" -> "STAT"; case "TARA" -> "CRACK";
            case "DARA" -> "MINE"; case "SELI" -> "SELR"; case "UPRA" -> "UPGR";
            case "JOBS" -> "CONTRACTS"; case "RUNA" -> "RUN"; case "SAVA" -> "SCAN";
            case "PIRA" -> "PING"; case "FILA" -> "FILES"; case "REVA" -> "READ";
            case "WORA" -> "WHO"; case "FENA" -> "FLOW"; case "VESA" -> "SITE";
            default -> head;
        };
        return mapped + (p.length > 1 ? " " + p[1] : "");
    }

    private void initAutomation(int left, int top, int panelWidth) {
        addDrawableChild(ButtonWidget.builder(Text.literal("КУПИТЬ АВТОМАТИЗАЦИЮ — 35 КРЕДИТОВ"), b -> runNex("UPGR AUTOM"))
            .dimensions(left + 14, top + 76, panelWidth - 28, 21)
            .tooltip(Tooltip.of(Text.literal("Покупает модуль AUTOM уровня 1. Он открывает готовые сценарии и FLOW."))).build());
        input = new TextFieldWidget(textRenderer, left + 14, top + 102, panelWidth - 28, 20, Text.literal("Сценарий автоматизации"));
        input.setMaxLength(240);
        input.setPlaceholder(Text.literal("SEEK vault; PORTA vault.arena; STAT"));
        addDrawableChild(input);
        addDrawableChild(ButtonWidget.builder(Text.literal("ЗАПУСТИТЬ СВОЙ СЦЕНАРИЙ"), b -> submit("arena nex FLOW "))
            .dimensions(left + 14, top + 130, panelWidth - 28, 22)
            .tooltip(Tooltip.of(Text.literal("Запускает введённые команды. Требуется AUTOM; максимум 6 игровых команд."))).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("ГОТОВЫЙ: ОБЗОР"), b -> runNex("FLOW SEEK arena; NODR; STAT"))
            .dimensions(left + 14, top + 166, panelWidth - 28, 22)
            .tooltip(Tooltip.of(Text.literal("Поиск сайтов, просмотр виртуальной карты и состояния профиля."))).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("ГОТОВЫЙ: РЕСУРСЫ"), b -> runNex("FLOW MINE; SELR; STAT"))
            .dimensions(left + 14, top + 194, panelWidth - 28, 22)
            .tooltip(Tooltip.of(Text.literal("Игровая добыча, обмен осколков и проверка баланса."))).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("ГОТОВЫЙ: АУДИТ САЙТОВ"), b -> runNex("FLOW PORTA shop.arena; PORTA news.arena; PORTA grid.arena"))
            .dimensions(left + 14, top + 222, panelWidth - 28, 22)
            .tooltip(Tooltip.of(Text.literal("Проверяет виртуальные службы на трёх сайтах без ручного ввода."))).build());
    }
    private void submit(String prefix) {
        String value = input == null ? "" : input.getText().trim();
        if (!value.isEmpty()) send(prefix + translateVora(value));
    }
    private void runNex(String instruction) { send("arena nex " + instruction); }
    private void send(String command) {
        if (client == null || client.player == null || client.getNetworkHandler() == null) return;
        client.getNetworkHandler().sendChatCommand(command);
        close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Keep the world visible behind the custom workstation; do not use vanilla dim overlay.
        int panelWidth = Math.min(520, this.width - 24);
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(18, (this.height - 340) / 2);
        context.fill(left, top, left + panelWidth, top + 340, 0xEE101923);
        context.fill(left, top, left + panelWidth, top + 3, 0xFF29D6C5);
        context.drawTextWithShadow(textRenderer, Text.literal("CYBER ARENA // ИГРОВАЯ СТАНЦИЯ"), left + 14, top + 13, 0xFF55E6D2);
        context.drawTextWithShadow(textRenderer, Text.literal("ВИРТУАЛЬНЫЙ МИР / ИГРОВЫЕ ДАННЫЕ"), left + 14, top + 29, 0xFFB8C8D8);

        if (page == 6) {
            int idx = siteIndex(selectedSite);
            context.drawTextWithShadow(textRenderer, Text.literal("ARENA:// " + selectedSite + " / СПИСОК ФАЙЛОВ"), left + 14, top + 108, 0xFF70D8FF);
            for (int i = 0; i < FILE_NAMES[idx].length; i++) {
                int y = top + 143 + i * 43;
                context.fill(left + 14, y - 4, left + panelWidth - 14, y + 26, 0xFF172634);
                context.drawTextWithShadow(textRenderer, Text.literal(FILE_NAMES[idx][i]), left + 22, y + 5, 0xFFBDEBFF);
                context.drawTextWithShadow(textRenderer, Text.literal("ИГРОВАЯ ЗАПИСЬ / МЕТАДАННЫЕ"), left + 22, y + 16, 0xFF8295A8);
            }
        } else if (page == 3) {
            int idx = siteIndex(selectedSite);
            context.drawTextWithShadow(textRenderer, Text.literal("ARENA:// " + selectedSite), left + 14, top + 116, 0xFF70D8FF);
            context.drawTextWithShadow(textRenderer, Text.literal(TITLES[idx]), left + 14, top + 139, 0xFFBDEBFF);
            drawWrapped(context, DESCRIPTIONS[idx], left + 14, top + 160, panelWidth - 28, 0xFFD0DCE6);
            drawWrapped(context, SITE_CONTENT[idx], left + 14, top + 183, panelWidth - 28, 0xFF9BD4A8);
            context.drawTextWithShadow(textRenderer, Text.literal("УРОВЕНЬ ЗАЩИТЫ: " + RISKS[idx]), left + 14, top + 207, 0xFFFFC66D);
            context.drawTextWithShadow(textRenderer, Text.literal("ВИРТУАЛЬНЫЕ СЛУЖБЫ"), left + 14, top + 218, 0xFF70D8FF);
            for (int i = 0; i < SERVICES.length; i++)
                context.drawTextWithShadow(textRenderer, Text.literal(SERVICES[i]), left + 22, top + 255 + i * 13, i == 0 ? 0xFF79E6A4 : 0xFFFFC66D);
        } else if (page == 0 || page == 4 || page == 5) {
            String heading = page == 4 ? "SAVED BOOKMARKS" : page == 5 ? "RECENTLY VISITED" : "VIRTUAL WEB DIRECTORY";
            context.drawTextWithShadow(textRenderer, Text.literal(heading), left + 14, top + 98, 0xFF70D8FF);
            List<String> sites = visibleSites();
            int rowY = top + (page == 0 ? 137 : 111);
            int pages = Math.max(1, (sites.size() + SITES_PER_PAGE - 1) / SITES_PER_PAGE);
            directoryPage = Math.max(0, Math.min(directoryPage, pages - 1));
            int offset = page == 0 ? directoryPage * SITES_PER_PAGE : 0;
            for (int i = offset; i < Math.min(offset + SITES_PER_PAGE, sites.size()); i++) {
                int idx = siteIndex(sites.get(i)), y = rowY + (i - offset) * 35;
                context.fill(left + 14, y - 3, left + panelWidth - 14, y + 27, 0xFF172634);
                context.drawTextWithShadow(textRenderer, Text.literal(sites.get(i) + " / " + RISKS[idx]), left + 22, y + 1, 0xFFBDEBFF);
                context.drawTextWithShadow(textRenderer, Text.literal(TITLES[idx]), left + 22, y + 14, 0xFF8295A8);
            }
            if (sites.isEmpty()) context.drawTextWithShadow(textRenderer, Text.literal("No entries yet. Open a site or change your search."), left + 18, rowY + 12, 0xFF8295A8);
        } else if (page == 1) {
            context.drawTextWithShadow(textRenderer, Text.literal(voraSelected ? "FLEX > // ВТОРОЙ ИГРОВОЙ ЯЗЫК" : "NX > // ИГРОВАЯ ПСЕВДОКОНСОЛЬ"), left + 14, top + 78, 0xFF70D8FF);
            String hints = voraSelected ? "LUMA help | KERI search | DORA services | NAVA map | MIRA status | TARA extract" : "MANU help | SEEK search | PORTA services | NODR map | STAT status | CRACK extract";
            drawWrapped(context, "NEXT COMMANDS: " + hints, left + 14, top + 239, panelWidth - 28, 0xFFD0DCE6);
            drawWrapped(context, voraSelected ? "FLEX — альтернативный словарь команд. Доступность расширенных функций зависит от игрового прогресса." : "NX — базовый язык игровых команд. Используй подсказки или справку MANU.", left + 14, top + 266, panelWidth - 28, 0xFF8295A8);
        } else if (page == 7) {
            context.drawTextWithShadow(textRenderer, Text.literal("NX // КОНСОЛЬ САЙТА: " + selectedSite), left + 14, top + 78, 0xFF70D8FF);
            drawWrapped(context, "Команды работают в контексте выбранного виртуального сайта. Обычная консоль остаётся отдельной вкладкой.", left + 14, top + 224, panelWidth - 28, 0xFFD0DCE6);
        } else {
            context.drawTextWithShadow(textRenderer, Text.literal("АВТОМАТИЗАЦИЯ // ГОТОВЫЕ СЦЕНАРИИ"), left + 14, top + 78, 0xFF70D8FF);
            context.drawTextWithShadow(textRenderer, Text.literal("Команды разделяются ; | До 6 шагов за запуск"), left + 14, top + 258, 0xFFD0DCE6);
            context.drawTextWithShadow(textRenderer, Text.literal("Только игровые команды. Внешние команды запрещены."), left + 14, top + 274, 0xFF8295A8);
        }
        super.render(context, mouseX, mouseY, delta);
        context.drawTextWithShadow(textRenderer, Text.literal("Все сайты, файлы и службы — вымышленные игровые данные."), left + 14, top + 321, 0xFF8295A8);
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
            else if (page == 7) submitSiteConsole();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    @Override public boolean shouldPause() { return false; }
}
