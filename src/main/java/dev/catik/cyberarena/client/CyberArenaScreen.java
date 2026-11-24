package dev.catik.cyberarena.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
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
        "craft.arena", "music.arena", "travel.arena", "legal.arena", "archive2.arena", "robot.arena", "food.arena", "power.arena", "sports.arena", "social.arena",
        "code.arena", "docks.arena", "museum2.arena", "energy.arena", "weather2.arena", "jobs.arena", "housing.arena", "transit.arena", "wildlife.arena", "archive3.arena"
    };
    private static final String[] TITLES = {
        "РЫНОК АРЕНЫ", "ЕЖЕДНЕВНЫЕ НОВОСТИ", "УПРАВЛЕНИЕ СЕТЬЮ", "АРХИВ ХРАНИЛИЩА", "ЛАБОРАТОРИЯ", "ОТКРЫТЫЙ ФОРУМ", "БАНК АРЕНЫ", "МЕТРО СВЯЗЬ", "ПРОГНОЗ ПОГОДЫ", "ГОРОДСКАЯ КЛИНИКА",
        "ЗАВОД", "ЦИФРОВОЙ МУЗЕЙ", "УНИВЕРСИТЕТ", "ПОРТОВЫЙ УЗЕЛ", "СТРАЖ", "ИГРОВОЙ КЛУБ", "ЗЕЛЁНАЯ ФЕРМА", "ПИКСЕЛЬНЫЕ НОВОСТИ", "КОСМИЧЕСКАЯ ЛАБОРАТОРИЯ", "АУКЦИОН",
        "МАСТЕРСКАЯ", "МУЗЫКАЛЬНЫЙ АРХИВ", "ПУТЕШЕСТВИЯ", "ПРАВОВОЙ СПРАВОЧНИК", "БАНК ПАМЯТИ", "АВТОМАТИКА", "ПРОДОВОЛЬСТВИЕ", "ЭНЕРГОСЕТЬ", "СПОРТИВНАЯ АРЕНА", "СОЦИАЛЬНАЯ СЕТЬ",
        "КОДОВАЯ МАСТЕРСКАЯ", "ВЕРФЬ", "ЗАЛ АРТЕФАКТОВ", "ЛАБОРАТОРИЯ СИНТЕЗА", "КЛИМАТИЧЕСКИЙ ЦЕНТР", "БИРЖА РАБОТЫ", "ДОМА И УЧАСТКИ", "ЖЕЛЕЗНОДОРОЖНАЯ СЕТЬ", "МИР ДИКОЙ ПРИРОДЫ", "ГЛУБОКИЙ АРХИВ"
    };
    private static final String[] DESCRIPTIONS = {
        "Витрина игрового рынка, предложения и заметки торговцев.", "Лента новостей города, события и публичные предупреждения.", "Состояние энергосети, нагрузка районов и ремонтные работы.", "Архив записей, защищённые данные и подсказки восстановления.", "Исследования, прототипы и заметки учёных.", "Обсуждения игроков, темы сообщества и правила форума.", "Игровой банк, счета и история операций.", "Карта станций метро и расписание поездов.", "Прогноз погоды, климатическая карта и предупреждения.", "Медицинские исследования и список снабжения.",
        "Производственные линии, состояние роботов и сменный график.", "Выставки, экспонаты и культурный архив.", "Учебные курсы, библиотека и карта кампуса.", "Грузовые ведомости, причалы и расписание судов.", "Система игровой защиты, датчики и ложные сигналы.", "Развлекательные игры и расписание выплат.", "Календарь урожая, запасы силосов и прогноз погоды.", "Новости студии и расписание передач.", "Космические миссии, орбитальная карта и телеметрия.", "Каталог лотов, история ставок и расчёты.",
        "Рецепты, чертежи и проекты гильдий.", "Музыкальные наборы и расписание звуковых событий.", "Маршруты, места отдыха и планы экспедиций.", "Судебные дела, правила и игровые решения.", "Исторические записи и восстановленные фрагменты.", "Каталог роботов, испытания и заводские прототипы.", "Рецепты, запасы продуктов и меню доставки.", "Выработка электростанций и плановые отключения.", "Турнирная таблица, матчи и календарь событий.", "Публикации игровых групп и репутация жителей.",
        "Каталог проектов, версии пакетов и журнал сборок.", "Прибытие судов, грузовые проверки и карты порта.", "Описание реликвий, реставрация и музейные экспозиции.", "Показатели реактора, испытания и отчёты безопасности.", "Прогнозы регионов, датчики и архив штормов.", "Вакансии, профили кандидатов и договоры.", "Объявления о домах, планы районов и история ремонта.", "Маршруты поездов, состояние составов и задержки.", "Виды животных, карты среды обитания и отчёты смотрителей.", "Журналы экспедиций, древние артефакты и закрытые архивы."
    };
    private static final String[] SITE_CONTENT = {
        "Доска рынка: инструменты, специальные предложения и заметки торговцев.", "Городская лента: главные события, происшествия и публичные оповещения.", "Сеть: нагрузка районов, состояние узлов и окна обслуживания.", "Архив: категории записей, правила доступа и подсказки восстановления.", "Лаборатория: эксперименты, прототипы и отчёты исследователей.", "Форум: обсуждения, группы игроков и правила сообщества.", "Банк: игровые счета, курсы обмена и уведомления об операциях.", "Метро: схема станций, расписание и изменения движения.", "Погода: прогноз, карта климата и предупреждения о штормах.", "Клиника: исследования, запасы и расписание приёма.",
        "Завод: сборочные линии, роботы и смены персонала.", "Музей: список экспонатов, карта залов и история коллекции.", "Университет: курсы, библиотека и карта учебных корпусов.", "Порт: список грузов, причалы и логистические уведомления.", "Страж: датчики, защитные правила и журнал происшествий.", "Клуб: игровые автоматы, правила и расписание турниров.", "Ферма: урожай, запасы зерна и прогноз погоды.", "Медиа: последние статьи, студийные передачи и эфирная сетка.", "Космос: миссии, орбиты и сводка телеметрии.", "Аукцион: лоты, история ставок и завершённые сделки.",
        "Мастерская: рецепты, чертежи и проекты строительства.", "Музыка: звуковые наборы, композиции и заметки студии.", "Путешествия: лесные стоянки, горные маршруты и списки снаряжения.", "Право: правила арены, обзоры дел и публичные решения.", "Хронология: древние руины, события сервера и исторические находки.", "Роботы: помощники, результаты испытаний и заводские модели.", "Кухня: хлеб, рагу, печёный картофель и списки ингредиентов.", "Энергия: выработка станции, прогноз нагрузки и обслуживание.", "Спорт: таблица лиги, результаты матчей и будущие события.", "Сообщество: группы жителей, публикации и репутация.",
        "Каталог кода: проекты, версии пакетов, журнал сборок и список задач.", "Верфь: манифесты судов, осмотр грузов и расписание экипажей.", "Артефакты: описание реликвий, история владельцев и реставрация.", "Реактор: телеметрия, испытания, отчёты безопасности и резервные журналы.", "Климат: штормы, станции датчиков, модели погоды и предупреждения.", "Работа: вакансии, анкеты, собеседования и шаблоны договоров.", "Жильё: объявления, владельцы, карты районов и заявки на ремонт.", "Железная дорога: расписание, станции, ремонт путей и задержки.", "Природа: виды животных, среда обитания, миграция и заповедники.", "Архив: экспедиции, закрытые артефакты, временная шкала и записи находок."
    };
    private static final String[] RISKS = {
        "LOW", "LOW", "MEDIUM", "HIGH", "EXPERT", "LOW", "HIGH", "MEDIUM", "LOW", "MEDIUM", "HIGH", "LOW", "MEDIUM", "MEDIUM", "EXPERT", "HIGH", "LOW", "MEDIUM", "EXPERT", "HIGH",
        "LOW", "MEDIUM", "LOW", "HIGH", "MEDIUM", "EXPERT", "LOW", "HIGH", "MEDIUM", "HIGH",
        "MEDIUM", "HIGH", "LOW", "EXPERT", "MEDIUM", "LOW", "HIGH", "MEDIUM", "LOW", "EXPERT"
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
        {"groups.idx", "posts.dat", "reputation.log"},
        {"projects.idx", "packages.dat", "builds.log", "contributors.db", "issues.enc"},
        {"vessels.map", "cargo.db", "crew.idx", "berths.log", "customs.enc"},
        {"artifacts.idx", "provenance.db", "restoration.log", "donors.enc", "gallery.map"},
        {"reactors.cfg", "output.dat", "safety.log", "isotopes.enc", "grid.map"},
        {"climate.idx", "stormtracks.map", "sensors.db", "warnings.log", "models.enc"},
        {"vacancies.idx", "profiles.db", "contracts.dat", "interviews.log", "payroll.enc"},
        {"listings.idx", "owners.db", "mortgages.enc", "repairs.log", "districts.map"},
        {"routes.map", "tickets.db", "fleet.idx", "delays.log", "signals.enc"},
        {"species.idx", "habitats.map", "tracking.db", "rangers.log", "reserves.enc"},
        {"relics.idx", "fragments.db", "chronology.map", "expeditions.log", "vault.enc"}
    };
    private static final Set<String> BOOKMARKS = new LinkedHashSet<>();
    private static final List<String> HISTORY = new ArrayList<>();
    private static final List<String> TERMINAL_OUTPUT = new ArrayList<>();
    private static final List<String> COMMAND_HISTORY = new ArrayList<>();
    private static final List<String> COMPLETION_MATCHES = new ArrayList<>();
    private static int historyCursor = -1;
    private static int completionCursor = 0;
    private static int directoryPage = 0;
    private static boolean voraSelected = false;
    private static final int SITES_PER_PAGE = 5;

    private final int page;
    private final String selectedSite;
    private final String query;
    private TextFieldWidget input;
    private int terminalScroll = 0;
    private int browserScroll = 0;
    private static final String[] NX_COMMANDS = {"MANU","STAT","HELP","SEEK","NODR","PORTA","CRACK","FILES","READ","WHO","WHOIS","PING","MINE","SELR","UPGR","FLOW","SITE","RAID","DUEL","DEFEND","COUNTER","FARM","VPN","IP","POLICE","NET","SABOTAGE","CONTRACTS","AUTOM","HOME","DEFENSE","LOGS"};

    public CyberArenaScreen() { this(0, "", ""); }
    public CyberArenaScreen(int initialPage) { this(initialPage, "", ""); }
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

    /** Custom button visuals for Cyber Arena; no vanilla ButtonWidget skin. */
    private static final class ButtonWidget extends ClickableWidget {
        private final java.util.function.Consumer<ButtonWidget> action;
        ButtonWidget(int x, int y, int width, int height, Text label, java.util.function.Consumer<ButtonWidget> action) {
            super(x, y, width, height, label);
            this.action = action;
        }
        static Builder builder(Text label, java.util.function.Consumer<ButtonWidget> action) { return new Builder(label, action); }
        @Override protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            boolean hovered = isHovered();
            int fill = !active ? 0xFF17212B : hovered ? 0xFF213947 : 0xFF182B37;
            int border = !active ? 0xFF43515B : hovered ? 0xFF55E6D2 : 0xFF287F88;
            context.fill(getX(), getY(), getX()+width, getY()+height, fill);
            context.fill(getX(), getY(), getX()+width, getY()+1, border);
            context.fill(getX(), getY()+height-1, getX()+width, getY()+height, border);
            context.fill(getX(), getY(), getX()+1, getY()+height, border);
            context.fill(getX()+width-1, getY(), getX()+width, getY()+height, border);
            net.minecraft.client.font.TextRenderer renderer = net.minecraft.client.MinecraftClient.getInstance().textRenderer;
            context.drawCenteredTextWithShadow(renderer, getMessage(), getX()+width/2, getY()+(height-renderer.fontHeight)/2, active ? (hovered ? 0xFF72F7E2 : 0xFFE0F0F5) : 0xFF71808B);
        }
        @Override public void onClick(double mouseX, double mouseY) { if (active && action != null) action.accept(this); }
        @Override protected void appendClickableNarrations(NarrationMessageBuilder builder) { }
        static final class Builder {
            private final Text label;
            private final java.util.function.Consumer<ButtonWidget> action;
            private int x,y,w,h;
            private Tooltip tooltip;
            Builder(Text label, java.util.function.Consumer<ButtonWidget> action) { this.label=label;this.action=action; }
            Builder dimensions(int x,int y,int w,int h) { this.x=x;this.y=y;this.w=w;this.h=h;return this; }
            Builder tooltip(Tooltip tooltip) { this.tooltip=tooltip;return this; }
            ButtonWidget build() { ButtonWidget b=new ButtonWidget(x,y,w,h,label,action);if(tooltip!=null)b.setTooltip(tooltip);return b; }
        }
    }


    @Override
    protected void init() {
        if (page == 1) { initTerminalFullScreen(); return; }
        int panelWidth = Math.max(220, this.width - 16);
        int panelHeight = Math.max(180, this.height - 16);
        int left = 8;
        int top = 8;
        int tabGap = 5, tabWidth = (panelWidth - 28 - tabGap * 3) / 4;
        addDrawableChild(ButtonWidget.builder(Text.literal("БРАУЗЕР"), b -> navigate(0)).dimensions(left + 12, top + 48, tabWidth, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("КОНСОЛЬ"), b -> navigate(1)).dimensions(left + 14 + tabWidth + tabGap, top + 48, tabWidth, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("АВТОМАТИЗАЦИЯ"), b -> navigate(2)).dimensions(left + 14 + (tabWidth + tabGap) * 2, top + 48, tabWidth, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("УЛУЧШЕНИЯ"), b -> navigate(8)).dimensions(left + 14 + (tabWidth + tabGap) * 3, top + 48, tabWidth, 20).build());
        if (page == 0 || page == 3 || page == 4 || page == 5 || page == 6) initBrowser(left, top, panelWidth);
        else if (page == 7) initSiteConsole(left, top, panelWidth);
        else if (page == 8) initUpgrades(left, top, panelWidth);
        else initAutomation(left, top, panelWidth);
    }

    private void initTerminalFullScreen() {
        int left = 8, width = this.width - 16, tabWidth = Math.max(70, (width - 36) / 4);
        String[] labels = {"БРАУЗЕР", "NX / FLEX", "АВТОМАТИЗАЦИЯ", "УЛУЧШЕНИЯ"};
        int[] pages = {0, 1, 2, 8};
        for (int i = 0; i < labels.length; i++) {
            final int destination = pages[i];
            addDrawableChild(ButtonWidget.builder(Text.literal(labels[i]), b -> navigate(destination)).dimensions(left + i * (tabWidth + 4), 16, tabWidth, 20).build());
        }
        input = new TextFieldWidget(textRenderer, left + 14, this.height - 42, width - 112, 22, Text.literal("NX command"));
        input.setMaxLength(240);
        input.setPlaceholder(Text.literal(voraSelected ? "FLEX> введи команду..." : "nx@arena:~$ введи команду..."));
        input.setChangedListener(this::refreshCompletions);
        addDrawableChild(input);
        addDrawableChild(ButtonWidget.builder(Text.literal("ВВОД ↵"), b -> submitTerminal()).dimensions(left + width - 90, this.height - 42, 76, 22).build());
        input.setFocused(true);
        setInitialFocus(input);
        if (TERMINAL_OUTPUT.isEmpty()) {
            TERMINAL_OUTPUT.add("CYBER ARENA TERMINAL // " + (voraSelected ? "FLEX" : "NX"));
            TERMINAL_OUTPUT.add("Закрытая игровая среда. Все адреса, узлы и службы вымышлены.");
            TERMINAL_OUTPUT.add("MANU — справка | TAB — автодополнение | ↑/↓ — история | Ctrl+R — поиск");
            TERMINAL_OUTPUT.add("");
        }
        refreshCompletions(input.getText());
    }

    private void refreshCompletions(String value) {
        COMPLETION_MATCHES.clear();
        if (value == null) return;
        int split = value.lastIndexOf(' ');
        String prefix = split >= 0 ? value.substring(split + 1) : value;
        String head = split >= 0 ? value.substring(0, split).trim().toUpperCase(java.util.Locale.ROOT) : "";
        List<String> options = new ArrayList<>();
        if (head.isEmpty()) {
            for (String command : NX_COMMANDS) options.add(voraSelected ? flexAlias(command) : command);
        } else {
            String[] args = switch (head) {
                case "FARM" -> new String[]{"BUY","UPGRADE","REPAIR","STATUS"};
                case "VPN" -> new String[]{"BUY","ON","OFF","STATUS"};
                case "NET" -> new String[]{"DISCOVER","SCAN","ENUM","EXPLOIT"};
                case "DEFEND" -> new String[]{"STATUS","HELP","TYPE","ROUTE","MOVE"};
                case "UPGR" -> new String[]{"FIREWALL","AUTOM","STEALTH","EXTRACT","MINER","CPU","STORAGE","BREAKER","GUARD"};
                case "SABOTAGE", "NET EXPLOIT" -> new String[]{"FARM","POWER","SUPPLIES","DATA","ALARM","LOCK","FALSECALL","RAIDERS"};
                default -> new String[0];
            };
            for (String option : args) options.add(option);
        }
        for (String option : options) if (option.toLowerCase(java.util.Locale.ROOT).startsWith(prefix.toLowerCase(java.util.Locale.ROOT))) COMPLETION_MATCHES.add(option);
        completionCursor = 0;
    }

    private String flexAlias(String command) {
        return switch (command) {
            case "MANU" -> "LUMA"; case "SEEK" -> "KERI"; case "PORTA" -> "DORA"; case "NODR" -> "NAVA";
            case "STAT" -> "MIRA"; case "CRACK" -> "TARA"; case "MINE" -> "DARA"; case "SELR" -> "SELI";
            case "UPGR" -> "UPRA"; case "RAID" -> "RIVA"; case "FLOW" -> "FENA"; case "SITE" -> "VESA";
            default -> command;
        };
    }

    private void submitTerminal() {
        if (input == null) return;
        String value = input.getText().trim();
        if (value.isEmpty()) return;
        String command = translateVora(value);
        TERMINAL_OUTPUT.add("§a" + (voraSelected ? "flex" : "nx") + "@arena:~$ §f" + value);
        COMMAND_HISTORY.add(value);
        if (COMMAND_HISTORY.size() > 100) COMMAND_HISTORY.remove(0);
        historyCursor = -1;
        if (client != null && client.getNetworkHandler() != null) client.getNetworkHandler().sendChatCommand("arena nex " + command);
        input.setText("");
        terminalScroll = 0;
    }

    public static boolean isTerminalOpen() {
        return net.minecraft.client.MinecraftClient.getInstance().currentScreen instanceof CyberArenaScreen screen && screen.page == 1;
    }
    public static void appendOutput(String message) {
        if (message == null || message.isBlank()) return;
        TERMINAL_OUTPUT.add(message);
        while (TERMINAL_OUTPUT.size() > 250) TERMINAL_OUTPUT.remove(0);
    }

    private void initBrowser(int left, int top, int panelWidth) {
        addDrawableChild(ButtonWidget.builder(Text.literal("КАТАЛОГ"), b -> navigate(0))
            .dimensions(left + 14, top + 76, (panelWidth - 36) / 3, 19).tooltip(Tooltip.of(Text.literal("Вернуться к списку сайтов"))).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("ЗАКЛАДКИ " + BOOKMARKS.size()), b -> navigate(4))
            .dimensions(left + 22 + (panelWidth - 36) / 3, top + 76, (panelWidth - 36) / 3, 19).tooltip(Tooltip.of(Text.literal("Показать сайты, сохранённые в закладки"))).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("ИСТОРИЯ"), b -> navigate(5))
            .dimensions(left + 30 + 2 * (panelWidth - 36) / 3, top + 76, (panelWidth - 36) / 3, 19).tooltip(Tooltip.of(Text.literal("Показать недавно открытые сайты"))).build());

        if (page == 3) {
            initSitePage(left, top, panelWidth);
            return;
        }
        if (page == 6) {
            initFilePage(left, top, panelWidth);
            return;
        }
        if (page == 0) {
            input = new TextFieldWidget(textRenderer, left + 14, top + 113, panelWidth - 128, 20, Text.literal("Поиск"));
            input.setMaxLength(48);
            input.setPlaceholder(Text.literal("Поиск вымышленных сайтов .arena..."));
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
        addDrawableChild(ButtonWidget.builder(Text.literal("ПРОСМОТР"), b -> openSite(site))
            .dimensions(left + panelWidth - Math.min(104, panelWidth / 3), y, Math.min(104, panelWidth / 3), 20)
            .tooltip(Tooltip.of(Text.literal("Только открыть и просмотреть страницу"))).build());
    }

    private void openAdjacentSite(int direction) {
        int current = siteIndex(selectedSite);
        int next = Math.floorMod(current + direction, SITES.length);
        openSite(SITES[next]);
    }

    private void initSitePage(int left, int top, int panelWidth) {
        addDrawableChild(ButtonWidget.builder(Text.literal("← НАЗАД"), b -> navigate(0))
            .dimensions(left + 14, top + 78, 90, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal(BOOKMARKS.contains(selectedSite) ? "УБРАТЬ ЗАКЛАДКУ" : "В ЗАКЛАДКИ"), b -> {
            if (BOOKMARKS.contains(selectedSite)) BOOKMARKS.remove(selectedSite);
            else BOOKMARKS.add(selectedSite);
            if (client != null) client.setScreen(new CyberArenaScreen(3, selectedSite, ""));
        }).dimensions(left + 110, top + 78, 118, 20).tooltip(Tooltip.of(Text.literal("Сохранить страницу в закладках"))).build());
        int navHalf = (panelWidth - 36) / 2;
        int navY = Math.max(top + 120, this.height - 66);
        addDrawableChild(ButtonWidget.builder(Text.literal("← ПРЕДЫДУЩИЙ САЙТ"), b -> openAdjacentSite(-1))
            .dimensions(left + 14, navY, navHalf, 18)
            .tooltip(Tooltip.of(Text.literal("Перейти к предыдущему сайту каталога"))).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("СЛЕДУЮЩИЙ САЙТ →"), b -> openAdjacentSite(1))
            .dimensions(left + 22 + navHalf, navY, navHalf, 18)
            .tooltip(Tooltip.of(Text.literal("Перейти к следующему сайту каталога"))).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("ПРОСМОТР ЗАПИСЕЙ"), b -> {
            if (client != null) client.setScreen(new CyberArenaScreen(6, selectedSite, ""));
        }).dimensions(left + 14, this.height - 42, panelWidth - 28, 22)
            .tooltip(Tooltip.of(Text.literal("Просмотреть открытые описания записей. Игровые действия выполняются в консоли."))).build());
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
        addDrawableChild(ButtonWidget.builder(Text.literal("ПРОФИЛЬ УЗЛА"), b -> runNex("WHO " + selectedSite))
            .dimensions(left + 22 + (panelWidth - 36) / 2, top + 134, (panelWidth - 36) / 2, 22)
            .tooltip(Tooltip.of(Text.literal("Получить сведения об игровом узле через консоль"))).build());
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
            case "PIRA" -> "PING"; case "FILA" -> "FILES"; case "REVA" -> "READ"; case "RIVA" -> "RAID";
            case "WORA" -> "WHO"; case "FENA" -> "FLOW"; case "VESA" -> "SITE";
            default -> head;
        };
        return mapped + (p.length > 1 ? " " + p[1] : "");
    }

    private void initAutomation(int left, int top, int panelWidth) {
        addDrawableChild(ButtonWidget.builder(Text.literal("ПРИОБРЕСТИ / УЛУЧШИТЬ AUTOM"), b -> runNex("UPGR AUTOM"))
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
    private void initUpgrades(int left, int top, int panelWidth) {
        String[] modules = {"FIREWALL", "AUTOM", "STEALTH", "EXTRACT", "MINER", "CPU", "STORAGE", "BREAKER", "GUARD"};
        String[] labels = {"ЗАЩИТА", "АВТОМАТИЗАЦИЯ", "СКРЫТНОСТЬ", "ИЗВЛЕЧЕНИЕ", "МАЙНИНГ", "ПРОЦЕССОР", "ХРАНИЛИЩЕ", "ЭЛЕКТРОЩИТОК", "ОХРАНА"};
        int gap = 8, colWidth = (panelWidth - 36) / 2;
        for (int i = 0; i < modules.length; i++) {
            final String module = modules[i];
            int col = i % 2, row = i / 2;
            addDrawableChild(ButtonWidget.builder(Text.literal("УЛУЧШИТЬ: " + labels[i]), b -> runNex("UPGR " + module))
                .dimensions(left + 14 + col * (colWidth + gap), top + 110 + row * 29, colWidth, 24)
                .tooltip(Tooltip.of(Text.literal("Купить следующий уровень " + module + ". Стоимость зависит от текущего уровня; максимум 5."))).build());
        }
        addDrawableChild(ButtonWidget.builder(Text.literal("МОЙ ПРОФИЛЬ И БАЛАНС"), b -> runNex("STAT"))
            .dimensions(left + 14, top + 266, panelWidth - 28, 22)
            .tooltip(Tooltip.of(Text.literal("Показать кредиты, очки репутации и уровни модулей"))).build());
    }

    private void submit(String prefix) {
        String value = input == null ? "" : input.getText().trim();
        if (!value.isEmpty()) send(prefix + translateVora(value));
    }
    private void runNex(String instruction) { send("arena nex " + instruction); }
    private void send(String command) {
        if (client == null || client.player == null || client.getNetworkHandler() == null) return;
        client.getNetworkHandler().sendChatCommand(command);
        if (page != 1) close();
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // No vanilla dimming or blur: keep the Minecraft world sharp behind this UI.
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (page == 1) { renderTerminal(context, mouseX, mouseY, delta); return; }
        // Keep the world visible behind the custom workstation; do not use vanilla dim overlay.
        int panelWidth = Math.max(220, this.width - 16);
        int panelHeight = Math.max(180, this.height - 16);
        int left = 8;
        int top = 8;
        context.fill(left, top, left + panelWidth, top + panelHeight, 0xEE101923);
        context.fill(left, top, left + panelWidth, top + 3, 0xFF29D6C5);
        context.drawTextWithShadow(textRenderer, Text.literal("CYBER ARENA // ИГРОВАЯ СТАНЦИЯ"), left + 14, top + 13, 0xFF55E6D2);
        context.drawTextWithShadow(textRenderer, Text.literal("ВИРТУАЛЬНАЯ СЕТЬ • ТОЛЬКО ИГРОВАЯ СИМУЛЯЦИЯ"), left + 14, top + 29, 0xFFB8C8D8);

        if (page == 6) {
            int idx = siteIndex(selectedSite);
            context.drawTextWithShadow(textRenderer, Text.literal("АРХИВ:// " + selectedSite + " / СПИСОК ЗАПИСЕЙ"), left + 14, top + 108, 0xFF70D8FF);
            for (int i = 0; i < FILE_NAMES[idx].length; i++) {
                int y = top + 143 + i * 43;
                context.fill(left + 14, y - 4, left + panelWidth - 14, y + 26, 0xFF172634);
                context.drawTextWithShadow(textRenderer, Text.literal(FILE_NAMES[idx][i]), left + 22, y + 5, 0xFFBDEBFF);
                context.drawTextWithShadow(textRenderer, Text.literal("ИГРОВАЯ ЗАПИСЬ / МЕТАДАННЫЕ"), left + 22, y + 16, 0xFF8295A8);
            }
        } else if (page == 3) {
            int idx = siteIndex(selectedSite);
            int accent = siteAccent(idx);
            String category = siteCategory(idx);
            int contentTop = top + 110;
            int contentBottom = top + panelHeight - 90;
            int contentHeight = Math.max(90, contentBottom - contentTop);
            int contentOffset = browserScroll * 8;
            context.drawTextWithShadow(textRenderer, Text.literal("АДРЕС: https://" + selectedSite + "   •   IP: " + virtualSiteIp(idx)), left + 14, top + 96, 0xFFBDEBFF);
            context.enableScissor(left + 10, contentTop, left + panelWidth - 10, contentBottom);
            context.fill(left + 10, contentTop - contentOffset, left + panelWidth - 10, contentTop + contentHeight - contentOffset, 0xFF142431);
            context.fill(left + 10, contentTop - contentOffset, left + panelWidth - 10, contentTop + 4 - contentOffset, accent);
            context.fill(left + 10, contentTop - contentOffset, left + 14, contentTop + contentHeight - contentOffset, accent);
            context.drawTextWithShadow(textRenderer, Text.literal(category + "  //  " + selectedSite), left + 22, contentTop + 12 - contentOffset, accent);
            context.drawTextWithShadow(textRenderer, Text.literal(TITLES[idx]), left + 22, contentTop + 36 - contentOffset, 0xFFF0F6FA);
            drawWrapped(context, DESCRIPTIONS[idx], left + 22, contentTop + 58 - contentOffset, panelWidth - 44, 0xFFD0DCE6);
            drawWrapped(context, SITE_CONTENT[idx], left + 22, contentTop + 92 - contentOffset, panelWidth - 44, 0xFFB5D9C8);
            context.fill(left + 22, contentTop + 142 - contentOffset, left + panelWidth - 22, contentTop + 164 - contentOffset, 0xFF1B2D3A);
            context.drawTextWithShadow(textRenderer, Text.literal("УРОВЕНЬ ЗАЩИТЫ: " + translateRisk(RISKS[idx]) + "  •  СОСТОЯНИЕ: ДОСТУПЕН"), left + 30, contentTop + 149 - contentOffset, accent);
            context.drawTextWithShadow(textRenderer, Text.literal("ПУБЛИЧНАЯ ИНФОРМАЦИЯ"), left + 22, contentTop + 184 - contentOffset, 0xFF70D8FF);
            drawWrapped(context, "Это демонстрационный игровой ресурс. Его домен и IP существуют только внутри мира Cyber Arena. Для исследования узлов, поиска слабых мест, атаки и настройки защиты используй консоль NX/FLEX.", left + 22, contentTop + 202 - contentOffset, panelWidth - 44, 0xFFB5C5D2);
            context.disableScissor();
            context.drawTextWithShadow(textRenderer, Text.literal("ПРОКРУТКА: КОЛЁСИКО МЫШИ"), left + 14, top + panelHeight - 38, 0xFF8295A8);
        } else if (page == 0 || page == 4 || page == 5) {
            String heading = page == 4 ? "СОХРАНЁННЫЕ САЙТЫ" : page == 5 ? "ИСТОРИЯ ПОСЕЩЕНИЙ" : "КАТАЛОГ ВИРТУАЛЬНЫХ САЙТОВ";
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
            if (sites.isEmpty()) context.drawTextWithShadow(textRenderer, Text.literal("Пока пусто. Откройте сайт или измените запрос."), left + 18, rowY + 12, 0xFF8295A8);
        } else if (page == 1) {
            context.drawTextWithShadow(textRenderer, Text.literal(voraSelected ? "FLEX > // ВТОРОЙ ИГРОВОЙ ЯЗЫК" : "NX > // ИГРОВАЯ ПСЕВДОКОНСОЛЬ"), left + 14, top + 78, 0xFF70D8FF);
            String hints = voraSelected ? "LUMA — справка | KERI — поиск | NAVA — карта | MIRA — статус | TARA — анализ" : "HELP — справка | SEEK — поиск | NODR — карта | STAT — статус | NET — сеть";
            drawWrapped(context, "NEXT COMMANDS: " + hints, left + 14, top + 239, panelWidth - 28, 0xFFD0DCE6);
            drawWrapped(context, voraSelected ? "FLEX — альтернативный игровой язык команд. Все операции выполняются только внутри мира Cyber Arena." : "NX — язык игровых команд. Введи HELP, чтобы получить полный список команд и описание каждой операции.", left + 14, top + 266, panelWidth - 28, 0xFF8295A8);
        } else if (page == 7) {
            context.drawTextWithShadow(textRenderer, Text.literal("NX // КОНСОЛЬ САЙТА: " + selectedSite), left + 14, top + 78, 0xFF70D8FF);
            drawWrapped(context, "Команды работают в контексте выбранного виртуального сайта. Обычная консоль остаётся отдельной вкладкой.", left + 14, top + 224, panelWidth - 28, 0xFFD0DCE6);
        } else if (page == 8) {
            context.drawTextWithShadow(textRenderer, Text.literal("МАГАЗИН МОДУЛЕЙ // ПРОКАЧКА ОПЕРАТОРА"), left + 14, top + 82, 0xFF70D8FF);
            drawWrapped(context, "Покупай уровни за игровые кредиты. Каждый модуль усиливает определённую часть системы. Максимум — 5 уровней. Баланс и текущие уровни смотри в профиле.", left + 14, top + 94, panelWidth - 28, 0xFFD0DCE6);
        } else {
            context.drawTextWithShadow(textRenderer, Text.literal("АВТОМАТИЗАЦИЯ // ГОТОВЫЕ СЦЕНАРИИ"), left + 14, top + 78, 0xFF70D8FF);
            context.drawTextWithShadow(textRenderer, Text.literal("Команды разделяются ; | До 6 шагов за запуск"), left + 14, top + 258, 0xFFD0DCE6);
            context.drawTextWithShadow(textRenderer, Text.literal("Только игровые команды. Внешние команды запрещены."), left + 14, top + 274, 0xFF8295A8);
        }
        super.render(context, mouseX, mouseY, delta);
        if (panelHeight >= 300 && page != 3) context.drawTextWithShadow(textRenderer, Text.literal("Сайты предназначены только для просмотра. Команды — в консоли."), left + 14, top + panelHeight - 16, 0xFF8295A8);
    }


    private void renderTerminal(DrawContext context, int mouseX, int mouseY, float delta) {
        int left = 8, top = 8, width = this.width - 16, height = this.height - 16;
        context.fill(left, top, left + width, top + height, 0xF20A1119);
        context.fill(left, top, left + width, top + 3, 0xFF29D6C5);
        context.drawTextWithShadow(textRenderer, Text.literal("CYBER ARENA // TERMINAL | " + (voraSelected ? "FLEX SHELL" : "NX SHELL")), left + 12, top + 42, 0xFF55E6D2);
        context.drawTextWithShadow(textRenderer, Text.literal("СЕАНС: ИГРОВАЯ СИМУЛЯЦИЯ     УЗЕЛ: ARENA     СТАТУС: АКТИВЕН"), left + 12, top + 56, 0xFF8295A8);
        int outputTop = top + 76, outputBottom = height - 62;
        context.fill(left + 8, outputTop, left + width - 8, outputBottom, 0xFF0D1720);
        int lineHeight = 12, maxLines = Math.max(1, (outputBottom - outputTop - 10) / lineHeight);
        int start = Math.max(0, TERMINAL_OUTPUT.size() - maxLines - terminalScroll);
        int end = Math.min(TERMINAL_OUTPUT.size(), start + maxLines);
        int y = outputTop + 7;
        for (int i = start; i < end; i++) {
            String line = TERMINAL_OUTPUT.get(i).replaceAll("§[0-9a-fk-or]", "");
            int color = line.contains("ERROR") || line.contains("ОШИБКА") ? 0xFFFF7777 : line.contains("УСПЕХ") ? 0xFF72E6B4 : 0xFFD0DCE6;
            drawWrapped(context, line, left + 16, y, width - 34, color);
            y += lineHeight;
        }
        if (input != null && input.isFocused() && !input.getText().isBlank()) {
            int suggestY = height - 57;
            context.fill(left + 8, suggestY - 3, left + width - 8, height - 34, 0xFF142431);
            String heading = COMPLETION_MATCHES.isEmpty() ? "НЕТ СОВПАДЕНИЙ" : "ВОЗМОЖНЫЕ ДОПОЛНЕНИЯ (" + COMPLETION_MATCHES.size() + ")";
            context.drawTextWithShadow(textRenderer, Text.literal(heading), left + 14, suggestY, 0xFF70D8FF);
            StringBuilder row = new StringBuilder();
            for (int i = 0; i < Math.min(8, COMPLETION_MATCHES.size()); i++) {
                if (i > 0) row.append("   ");
                row.append(i == completionCursor ? "> " : "").append(COMPLETION_MATCHES.get(i));
            }
            context.drawTextWithShadow(textRenderer, Text.literal(row.toString()), left + 14, suggestY + 13, 0xFFBDEBFF);
        }
        context.drawTextWithShadow(textRenderer, Text.literal(voraSelected ? "flex@arena:~>" : "nx@arena:~$"), left + 14, height - 23, 0xFF55E6D2);
        context.drawTextWithShadow(textRenderer, Text.literal("TAB: доп.  ↑/↓: история  CTRL+R: поиск  ENTER: выполнить"), left + width - 320, height - 23, 0xFF8295A8);
        super.render(context, mouseX, mouseY, delta);
    }

    private String virtualSiteIp(int index) { return "198.51.100." + (20 + Math.floorMod(index * 17 + 31, 200)); }

    private int siteAccent(int index) {
        return switch (index % 10) {
            case 0 -> 0xFFFFB84D; case 1 -> 0xFF65C8FF; case 2 -> 0xFF62E6B5;
            case 3 -> 0xFFB58CFF; case 4 -> 0xFFFF7E9D; case 5 -> 0xFF70D8FF;
            case 6 -> 0xFF7DE0A3; case 7 -> 0xFFFFA66B; case 8 -> 0xFF8CB8FF;
            default -> 0xFFE4C96B;
        };
    }

    private String siteCategory(int index) {
        return switch (index) {
            case 0, 19, 20, 26 -> "МАГАЗИНЫ И ТОРГОВЛЯ";
            case 1, 17, 21 -> "НОВОСТИ И МЕДИА";
            case 2, 9, 10, 27 -> "ИНФРАСТРУКТУРА";
            case 3, 6, 14, 15 -> "ЗАЩИЩЁННЫЕ СИСТЕМЫ";
            case 4, 12, 18, 25 -> "НАУКА И ОБРАЗОВАНИЕ";
            case 5, 29 -> "СООБЩЕСТВА";
            case 7, 22 -> "ТРАНСПОРТ И ПУТЕШЕСТВИЯ";
            case 8, 16 -> "ПОГОДА И ПРИРОДА";
            case 11, 24 -> "КУЛЬТУРА И АРХИВЫ";
            default -> "СПРАВОЧНЫЙ ПОРТАЛ";
        };
    }

    private String translateRisk(String risk) {
        return switch (risk) {
            case "LOW" -> "НИЗКИЙ"; case "MEDIUM" -> "СРЕДНИЙ";
            case "HIGH" -> "ВЫСОКИЙ"; default -> "ЭКСПЕРТНЫЙ";
        };
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
        if (page == 1 && input != null) {
            if (keyCode == 258 && input.isFocused()) {
                if (!COMPLETION_MATCHES.isEmpty()) {
                    String value = input.getText();
                    int split = value.lastIndexOf(' ');
                    String prefix = split >= 0 ? value.substring(0, split + 1) : "";
                    input.setText(prefix + COMPLETION_MATCHES.get(completionCursor));
                    completionCursor = (completionCursor + 1) % COMPLETION_MATCHES.size();
                }
                return true;
            }
            if (keyCode == 265 && input.isFocused()) {
                if (!COMMAND_HISTORY.isEmpty()) {
                    historyCursor = Math.min(COMMAND_HISTORY.size() - 1, historyCursor + 1);
                    input.setText(COMMAND_HISTORY.get(COMMAND_HISTORY.size() - 1 - historyCursor));
                }
                return true;
            }
            if (keyCode == 264 && input.isFocused()) {
                if (historyCursor > 0) { historyCursor--; input.setText(COMMAND_HISTORY.get(COMMAND_HISTORY.size() - 1 - historyCursor)); }
                else if (historyCursor == 0) { historyCursor = -1; input.setText(""); }
                return true;
            }
            if (keyCode == 82 && Screen.hasControlDown() && input.isFocused()) {
                String query = input.getText().toLowerCase(java.util.Locale.ROOT);
                for (int i = COMMAND_HISTORY.size() - 1; i >= 0; i--) {
                    if (COMMAND_HISTORY.get(i).toLowerCase(java.util.Locale.ROOT).contains(query)) {
                        input.setText(COMMAND_HISTORY.get(i));
                        appendOutput("§7(reverse-i-search) " + query + ": " + COMMAND_HISTORY.get(i));
                        break;
                    }
                }
                return true;
            }
            if (keyCode == 257 && input.isFocused()) { submitTerminal(); return true; }
            if (keyCode == 266) { terminalScroll = Math.min(TERMINAL_OUTPUT.size(), terminalScroll + 8); return true; }
            if (keyCode == 267) { terminalScroll = Math.max(0, terminalScroll - 8); return true; }
        }
        if (keyCode == 257 && input != null && input.isFocused()) {
            if (page == 0) {
                String q = input.getText().trim();
                if (client != null) client.setScreen(new CyberArenaScreen(0, "", q));
            } else if (page == 2) submit("arena nex FLOW ");
            else if (page == 7) submitSiteConsole();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    @Override public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (page == 1) { terminalScroll = Math.max(0, Math.min(TERMINAL_OUTPUT.size(), terminalScroll + (verticalAmount > 0 ? 4 : -4))); return true; }
        if (page == 3 || page == 6) { browserScroll = Math.max(0, Math.min(40, browserScroll + (verticalAmount < 0 ? 3 : -3))); return true; }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
    @Override public boolean shouldPause() { return false; }
}
