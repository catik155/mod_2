package dev.catik.cyberarena;

import net.minecraft.server.network.ServerPlayerEntity;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** NEX is a closed, fictional command language. It never performs external I/O. */
public final class NexEngine {
    private static final Map<String, String> NODES = Map.ofEntries(
        Map.entry("relay-01.arena", "PUBLIC RELAY"),
        Map.entry("market-02.arena", "EXCHANGE TERMINAL"),
        Map.entry("archive-03.arena", "DATA ARCHIVE"),
        Map.entry("north-04.arena", "NORTH DATA HUB"),
        Map.entry("south-05.arena", "SOUTH DATA HUB"),
        Map.entry("civic-06.arena", "CIVIC SERVICES"),
        Map.entry("orbit-07.arena", "ORBITAL SIMULATOR"),
        Map.entry("forge-08.arena", "INDUSTRIAL FORGE"),
        Map.entry("med-09.arena", "MEDICAL RESEARCH"),
        Map.entry("ghost-10.arena", "ANONYMOUS RELAY"),
        Map.entry("harbor-11.arena", "HARBOR RELAY"),
        Map.entry("campus-12.arena", "CAMPUS NODE"),
        Map.entry("tower-13.arena", "SKY TOWER"),
        Map.entry("garden-14.arena", "GREEN NODE"),
        Map.entry("deep-15.arena", "DEEP ARCHIVE")
    );
    private static final Map<String, String> SITES = Map.ofEntries(
        Map.entry("shop.arena", "MARKET / TRADE / EASY"),
        Map.entry("news.arena", "DAILY / MEDIA / PUBLIC"),
        Map.entry("grid.arena", "GRID / ENERGY / MEDIUM"),
        Map.entry("vault.arena", "VAULT / ARCHIVE / HARD"),
        Map.entry("labs.arena", "LABS / RESEARCH / EXPERT"),
        Map.entry("forum.arena", "FORUM / COMMUNITY / EASY"),
        Map.entry("bank.arena", "BANK / FINANCE / HARD"),
        Map.entry("metro.arena", "METRO / TRANSIT / MEDIUM"),
        Map.entry("weather.arena", "SKYCAST / WEATHER / EASY"),
        Map.entry("clinic.arena", "CLINIC / HEALTH DATA / MEDIUM"),
        Map.entry("factory.arena", "FACTORIUM / INDUSTRY / HARD"),
        Map.entry("museum.arena", "MUSEUM / CULTURE / EASY"),
        Map.entry("uni.arena", "CAMPUS / EDUCATION / MEDIUM"),
        Map.entry("port.arena", "HARBOR / LOGISTICS / MEDIUM"),
        Map.entry("security.arena", "SENTINEL / DEFENSE / EXPERT"),
        Map.entry("casino.arena", "LUCKY BYTE / ENTERTAINMENT / HARD"),
        Map.entry("farm.arena", "GREENFIELD / AGRICULTURE / EASY"),
        Map.entry("media.arena", "PIXEL PRESS / MEDIA / MEDIUM"),
        Map.entry("space.arena", "STARLAB / SPACE / EXPERT"),
        Map.entry("auction.arena", "BIDNET / AUCTIONS / HARD"),
        Map.entry("craft.arena", "CRAFT / BUILD / EASY"),
        Map.entry("music.arena", "SONIC / AUDIO / MEDIUM"),
        Map.entry("travel.arena", "VOYAGER / ROUTES / EASY"),
        Map.entry("legal.arena", "LEXICON / COURTS / HARD"),
        Map.entry("archive2.arena", "MEMORY / OLD RECORDS / MEDIUM"),
        Map.entry("robot.arena", "AUTOMATA / ROBOTICS / EXPERT"),
        Map.entry("food.arena", "PANTRY / DELIVERY / EASY"),
        Map.entry("power.arena", "VOLTAGE / POWER / HARD"),
        Map.entry("sports.arena", "ARENA SPORTS / EVENTS / MEDIUM"),
        Map.entry("code.arena", "CODE FORGE / DEVELOPMENT / MEDIUM"),
        Map.entry("docks.arena", "DOCKYARD / SHIPPING / HARD"),
        Map.entry("museum2.arena", "ARTIFACT / ANTIQUES / EASY"),
        Map.entry("energy.arena", "FUSION / POWER RESEARCH / EXPERT"),
        Map.entry("weather2.arena", "CLIMANET / CLIMATE DATA / MEDIUM"),
        Map.entry("jobs.arena", "WORKLINK / EMPLOYMENT / EASY"),
        Map.entry("housing.arena", "HOMESTEAD / PROPERTY / MEDIUM"),
        Map.entry("transit.arena", "RAILNET / TRANSPORT / HARD"),
        Map.entry("wildlife.arena", "WILDLIFE / ECOLOGY / EASY"),
        Map.entry("archive3.arena", "DEEP VAULT / HISTORY / EXPERT"),
        Map.entry("social.arena", "SOCIAL BYTE / COMMUNITY / HARD")
    );
    private static final Map<String, Integer> DIFFICULTY = Map.ofEntries(
        Map.entry("shop.arena",1), Map.entry("news.arena",2), Map.entry("grid.arena",3),
        Map.entry("vault.arena",4), Map.entry("labs.arena",5), Map.entry("forum.arena",1),
        Map.entry("bank.arena",4), Map.entry("metro.arena",3), Map.entry("weather.arena",1),
        Map.entry("clinic.arena",3), Map.entry("factory.arena",4), Map.entry("museum.arena",1),
        Map.entry("uni.arena",2), Map.entry("port.arena",3), Map.entry("security.arena",5),
        Map.entry("casino.arena",4), Map.entry("farm.arena",1), Map.entry("media.arena",2),
        Map.entry("space.arena",5), Map.entry("auction.arena",4),
        Map.entry("craft.arena",1), Map.entry("music.arena",2), Map.entry("travel.arena",1),
        Map.entry("legal.arena",4), Map.entry("archive2.arena",3), Map.entry("robot.arena",5),
        Map.entry("food.arena",1), Map.entry("power.arena",4), Map.entry("sports.arena",2),
        Map.entry("code.arena",3),
        Map.entry("docks.arena",4),
        Map.entry("museum2.arena",1),
        Map.entry("energy.arena",5),
        Map.entry("weather2.arena",3),
        Map.entry("jobs.arena",1),
        Map.entry("housing.arena",2),
        Map.entry("transit.arena",4),
        Map.entry("wildlife.arena",1),
        Map.entry("archive3.arena",5),
        Map.entry("social.arena",4)
    );
    private static final Map<String, List<String>> FILES = Map.ofEntries(
        Map.entry("shop.arena", List.of("catalog.idx", "offers.dat", "ledger.log")),
        Map.entry("news.arena", List.of("bulletins.txt", "dispatch.log", "public.key")),
        Map.entry("grid.arena", List.of("load.map", "relay.cfg", "grid.log")),
        Map.entry("vault.arena", List.of("records.enc", "ledger.bak", "access.log")),
        Map.entry("labs.arena", List.of("research.dat", "prototype.idx", "lab-notes.txt")),
        Map.entry("forum.arena", List.of("threads.idx", "members.db", "rules.txt")),
        Map.entry("bank.arena", List.of("rates.dat", "accounts.enc", "audit.log")),
        Map.entry("metro.arena", List.of("timetable.dat", "stations.map", "signals.log")),
        Map.entry("weather.arena", List.of("forecast.dat", "climate.map", "alerts.log")),
        Map.entry("clinic.arena", List.of("research.idx", "supply.dat", "patient.enc")),
        Map.entry("factory.arena", List.of("assembly.cfg", "robotics.dat", "shift.log")),
        Map.entry("museum.arena", List.of("exhibits.idx", "archive.dat", "visitors.log")),
        Map.entry("uni.arena", List.of("courses.idx", "library.db", "campus.map")),
        Map.entry("port.arena", List.of("cargo.idx", "berths.map", "manifest.enc")),
        Map.entry("security.arena", List.of("rules.cfg", "sensors.map", "incident.log")),
        Map.entry("casino.arena", List.of("games.idx", "payouts.dat", "vault.enc")),
        Map.entry("farm.arena", List.of("harvest.map", "silos.dat", "weather.log")),
        Map.entry("media.arena", List.of("stories.idx", "studio.cfg", "broadcast.log")),
        Map.entry("space.arena", List.of("telemetry.dat", "orbit.map", "mission.enc")),
        Map.entry("auction.arena", List.of("lots.idx", "bids.dat", "settlement.log")),
        Map.entry("craft.arena", List.of("recipes.idx", "blueprints.dat", "guild.log")),
        Map.entry("music.arena", List.of("tracks.idx", "soundpacks.dat", "studio.log")),
        Map.entry("travel.arena", List.of("routes.map", "destinations.idx", "travel.log")),
        Map.entry("legal.arena", List.of("codes.idx", "cases.dat", "court.log")),
        Map.entry("archive2.arena", List.of("timeline.map", "fragments.dat", "catalog.log")),
        Map.entry("robot.arena", List.of("robots.idx", "tests.dat", "factory.log")),
        Map.entry("food.arena", List.of("recipes.idx", "supply.dat", "orders.log")),
        Map.entry("power.arena", List.of("plants.map", "energy.dat", "load.log")),
        Map.entry("sports.arena", List.of("leagues.idx", "matches.dat", "events.log")),
        Map.entry("code.arena", List.of("projects.idx", "packages.dat", "builds.log", "contributors.db", "issues.enc")),
        Map.entry("docks.arena", List.of("vessels.map", "cargo.db", "crew.idx", "berths.log", "customs.enc")),
        Map.entry("museum2.arena", List.of("artifacts.idx", "provenance.db", "restoration.log", "donors.enc", "gallery.map")),
        Map.entry("energy.arena", List.of("reactors.cfg", "output.dat", "safety.log", "isotopes.enc", "grid.map")),
        Map.entry("weather2.arena", List.of("climate.idx", "stormtracks.map", "sensors.db", "warnings.log", "models.enc")),
        Map.entry("jobs.arena", List.of("vacancies.idx", "profiles.db", "contracts.dat", "interviews.log", "payroll.enc")),
        Map.entry("housing.arena", List.of("listings.idx", "owners.db", "mortgages.enc", "repairs.log", "districts.map")),
        Map.entry("transit.arena", List.of("routes.map", "tickets.db", "fleet.idx", "delays.log", "signals.enc")),
        Map.entry("wildlife.arena", List.of("species.idx", "habitats.map", "tracking.db", "rangers.log", "reserves.enc")),
        Map.entry("archive3.arena", List.of("relics.idx", "fragments.db", "chronology.map", "expeditions.log", "vault.enc")),
        Map.entry("social.arena", List.of("groups.idx", "posts.dat", "reputation.log"))
    );
    private static final Map<String, List<String>> PORTS = Map.ofEntries(
        Map.entry("shop.arena", List.of("021|WEB-GATE|OPEN|Public storefront", "044|TRADE-VAULT|GUARDED|Offer records", "071|OWNER-CORE|LOCKED|Owner controls", "088|CACHE-NODE|OPEN|Public cache")),
        Map.entry("news.arena", List.of("021|WEB-GATE|OPEN|News pages", "032|FEED-CAST|OPEN|Bulletin feed", "044|ARCHIVE|GUARDED|Dispatch archive", "071|EDITOR-CORE|LOCKED|Editorial controls")),
        Map.entry("grid.arena", List.of("021|GRID-VIEW|OPEN|Public grid status", "044|RELAY-DATA|GUARDED|Relay telemetry", "071|CONTROL-CORE|LOCKED|Grid control simulation", "093|BACKUP-STORE|GUARDED|Backup snapshots")),
        Map.entry("vault.arena", List.of("021|ENTRY-GATE|OPEN|Vault landing page", "044|DATA-VAULT|GUARDED|Encrypted archive", "071|ADMIN-CORE|LOCKED|Administrator console", "099|COLD-STORE|LOCKED|Cold backup storage")),
        Map.entry("labs.arena", List.of("021|LAB-PORTAL|OPEN|Research index", "044|PROTOTYPE|GUARDED|Prototype manifests", "071|DIRECTOR-CORE|LOCKED|Research control", "099|SPECIMEN-VAULT|LOCKED|Protected research data")),
        Map.entry("forum.arena", List.of("021|THREAD-GATE|OPEN|Public threads", "032|CHAT-RELAY|OPEN|Community relay", "044|MEMBER-DB|GUARDED|Member index")),
        Map.entry("bank.arena", List.of("021|BANK-FRONT|OPEN|Public rates", "044|LEDGER|GUARDED|Simulated ledger", "071|TREASURY|LOCKED|Treasury console", "099|COLD-VAULT|LOCKED|Backup vault")),
        Map.entry("metro.arena", List.of("021|ROUTE-MAP|OPEN|Public route map", "032|TIMETABLE|OPEN|Transit schedule", "044|SIGNAL-BOX|GUARDED|Signal telemetry", "071|TRAFFIC-CORE|LOCKED|Transit control")),
        Map.entry("weather.arena", List.of("021|SKY-FEED|OPEN|Forecast feed", "032|CLIMATE-MAP|OPEN|Climate map", "044|ALERT-STORE|GUARDED|Weather alerts")),
        Map.entry("clinic.arena", List.of("021|CARE-PORTAL|OPEN|Public clinic portal", "044|SUPPLY-DB|GUARDED|Supply records", "071|RESEARCH-CORE|LOCKED|Research console")),
        Map.entry("factory.arena", List.of("021|FACTORY-VIEW|OPEN|Factory status", "044|ROBOTICS|GUARDED|Robot telemetry", "071|ASSEMBLY-CORE|LOCKED|Production control", "099|BLUEPRINT-VAULT|LOCKED|Blueprint storage")),
        Map.entry("museum.arena", List.of("021|EXHIBIT-GATE|OPEN|Exhibit catalog", "032|AUDIO-GUIDE|OPEN|Public guide", "044|ARCHIVE-BOX|GUARDED|Archive index")),
        Map.entry("uni.arena", List.of("021|CAMPUS-WEB|OPEN|Campus portal", "032|LIBRARY|OPEN|Library catalog", "044|COURSE-DB|GUARDED|Course records", "071|REGISTRAR|LOCKED|Registrar console")),
        Map.entry("port.arena", List.of("021|HARBOR-MAP|OPEN|Public harbor map", "044|CARGO-DB|GUARDED|Cargo manifests", "071|BERTH-CORE|LOCKED|Berth control")),
        Map.entry("security.arena", List.of("021|SENTINEL-INFO|OPEN|Public status", "044|SENSOR-MESH|GUARDED|Sensor simulation", "071|DEFENSE-CORE|LOCKED|Defense controls", "099|HONEY-NODE|LOCKED|Decoy service")),
        Map.entry("casino.arena", List.of("021|GAME-LOBBY|OPEN|Public game lobby", "044|PAYOUT-BOX|GUARDED|Payout records", "071|HOUSE-CORE|LOCKED|House controls")),
        Map.entry("farm.arena", List.of("021|FIELD-REPORT|OPEN|Farm status", "032|WEATHER-FEED|OPEN|Weather feed", "044|SILO-DB|GUARDED|Storage records")),
        Map.entry("media.arena", List.of("021|PRESS-ROOM|OPEN|Public stories", "032|BROADCAST|OPEN|Broadcast feed", "044|STUDIO-DB|GUARDED|Studio schedule")),
        Map.entry("space.arena", List.of("021|MISSION-BOARD|OPEN|Mission board", "044|TELEMETRY|GUARDED|Flight telemetry", "071|FLIGHT-CORE|LOCKED|Mission control", "099|ORBIT-VAULT|LOCKED|Mission archive")),
        Map.entry("auction.arena", List.of("021|BID-BOARD|OPEN|Auction lots", "044|BID-LEDGER|GUARDED|Bid records", "071|SETTLEMENT|LOCKED|Settlement control")),
        Map.entry("craft.arena", List.of("021|CRAFT-GATE|OPEN|Public recipe catalog", "032|GUILD-RELAY|OPEN|Guild notices", "044|BLUEPRINT-DB|GUARDED|Build plans", "071|CRAFT-CORE|LOCKED|Workshop control")),
        Map.entry("music.arena", List.of("021|SONIC-WEB|OPEN|Public music index", "032|AUDIO-FEED|OPEN|Sound pack feed", "044|STUDIO-DB|GUARDED|Studio schedule", "071|MIX-CORE|LOCKED|Audio lab control")),
        Map.entry("travel.arena", List.of("021|ROUTE-GATE|OPEN|Public routes", "032|DEST-FEED|OPEN|Destination catalog", "044|EXPEDITION-DB|GUARDED|Expedition reports", "071|VOYAGER-CORE|LOCKED|Travel operations")),
        Map.entry("legal.arena", List.of("021|LEX-WEB|OPEN|Public rules index", "032|CASE-FEED|OPEN|Case bulletins", "044|COURT-DB|GUARDED|Case metadata", "071|JUDICIAL-CORE|LOCKED|Court simulation")),
        Map.entry("archive2.arena", List.of("021|MEMORY-GATE|OPEN|Public timeline", "032|FRAGMENT-FEED|OPEN|Archive fragments", "044|HISTORY-DB|GUARDED|Historical records", "071|MEMORY-CORE|LOCKED|Archive management")),
        Map.entry("robot.arena", List.of("021|ROBOT-WEB|OPEN|Robot catalog", "044|TEST-DB|GUARDED|Testing metadata", "071|AUTOMATA-CORE|LOCKED|Robot lab simulation", "099|SYNTH-VAULT|LOCKED|Protected designs")),
        Map.entry("food.arena", List.of("021|PANTRY-WEB|OPEN|Public recipes", "032|DELIVERY-FEED|OPEN|Delivery listings", "044|SUPPLY-DB|GUARDED|Supply metadata", "071|KITCHEN-CORE|LOCKED|Kitchen control")),
        Map.entry("power.arena", List.of("021|VOLT-WEB|OPEN|Public power status", "044|ENERGY-DB|GUARDED|Energy records", "071|PLANT-CORE|LOCKED|Plant control", "099|GRID-VAULT|LOCKED|Backup energy archive")),
        Map.entry("sports.arena", List.of("021|SPORTS-WEB|OPEN|Public league board", "032|MATCH-FEED|OPEN|Match reports", "044|EVENT-DB|GUARDED|Event records", "071|LEAGUE-CORE|LOCKED|League management")),
        Map.entry("code.arena", List.of("021|CODE-HOME|OPEN|Project directory", "032|PACKAGE-FEED|OPEN|Release feed", "044|BUILD-DB|GUARDED|Build metadata", "071|MAINTAINER-CORE|LOCKED|Repository simulation")),
        Map.entry("docks.arena", List.of("021|HARBOR-VIEW|OPEN|Vessel board", "032|CARGO-FEED|OPEN|Cargo notices", "044|MANIFEST-DB|GUARDED|Cargo manifests", "071|PORT-CORE|LOCKED|Port authority simulation")),
        Map.entry("museum2.arena", List.of("021|GALLERY-WEB|OPEN|Artifact catalog", "032|EXHIBIT-FEED|OPEN|Exhibition feed", "044|PROVENANCE-DB|GUARDED|Artifact records", "071|CURATOR-CORE|LOCKED|Museum archive simulation")),
        Map.entry("energy.arena", List.of("021|FUSION-INFO|OPEN|Public reactor status", "044|OUTPUT-DB|GUARDED|Energy telemetry", "071|REACTOR-CORE|LOCKED|Reactor control simulation", "099|SAFETY-VAULT|LOCKED|Safety archive")),
        Map.entry("weather2.arena", List.of("021|CLIMATE-WEB|OPEN|Climate overview", "032|STORM-FEED|OPEN|Storm notices", "044|SENSOR-DB|GUARDED|Sensor records", "071|MODEL-CORE|LOCKED|Forecast model simulation")),
        Map.entry("jobs.arena", List.of("021|WORK-BOARD|OPEN|Vacancy board", "032|CAREER-FEED|OPEN|Career notices", "044|PROFILE-DB|GUARDED|Candidate records", "071|HR-CORE|LOCKED|Recruitment simulation")),
        Map.entry("housing.arena", List.of("021|HOME-BOARD|OPEN|Property listings", "032|DISTRICT-FEED|OPEN|District notices", "044|OWNER-DB|GUARDED|Owner records", "071|PROPERTY-CORE|LOCKED|Property registry simulation")),
        Map.entry("transit.arena", List.of("021|RAIL-MAP|OPEN|Route map", "032|TICKET-FEED|OPEN|Service notices", "044|FLEET-DB|GUARDED|Fleet records", "071|SIGNAL-CORE|LOCKED|Rail control simulation")),
        Map.entry("wildlife.arena", List.of("021|ECO-WEB|OPEN|Species index", "032|RANGER-FEED|OPEN|Ranger notices", "044|TRACK-DB|GUARDED|Tracking records", "071|RESERVE-CORE|LOCKED|Reserve simulation")),
        Map.entry("archive3.arena", List.of("021|RELIC-GATE|OPEN|Public relic catalog", "032|FRAGMENT-FEED|OPEN|Archive fragments", "044|CHRONICLE-DB|GUARDED|Chronology records", "071|DEEP-CORE|LOCKED|Archive control", "099|VAULT-STORE|LOCKED|Protected vault")),
        Map.entry("social.arena", List.of("021|SOCIAL-WEB|OPEN|Public community page", "032|POST-FEED|OPEN|Public posts", "044|GROUP-DB|GUARDED|Group profiles", "071|SOCIAL-CORE|LOCKED|Community admin simulation"))
    );


    private NexEngine() {}
    public static String execute(String raw) { return execute(raw, null); }

    public static String execute(String raw, ServerPlayerEntity player) {
        if (raw == null || raw.isBlank()) return error("EMPTY", "Enter an instruction. Type MANU.");
        String[] parts = raw.trim().split("\\s+", 2);
        String cmd = parts[0].toUpperCase(Locale.ROOT);
        String arg = parts.length > 1 ? unquote(parts[1].trim().toLowerCase(Locale.ROOT)) : "";
        if (player != null && ArenaState.get(player.getServer()).isPowerOut(player) && !Set.of("STAT", "STATUS", "MANU", "HELP", "?", "REPAIR").contains(cmd)) return error("POWER_OFF", "Рабочая станция обесточена. Дождись восстановления или введи REPAIR.");
        return switch (cmd) {
            case "MANU", "HELP", "?" -> manual();
            case "STAT", "STATUS" -> player == null ? "§aNX CORE ONLINE §7| §fExternal I/O: DISABLED" : status(player);
            case "NODR", "NODES", "MAPA" -> nodes();
            case "SEEK" -> seek(arg);
            case "WHO", "WHOIS" -> who(arg);
            case "PING" -> ping(arg);
            case "PORTA", "PORTS" -> ports(arg);
            case "FILES", "LS" -> files(arg);
            case "READ", "VIEW" -> readFile(arg);
            case "CRACK" -> player == null ? error("NO_OPERATOR", "Operator context required.") : ArenaState.get(player.getServer()).breachSite(player, arg);
            case "RAID" -> player == null ? error("NO_OPERATOR", "Operator context required.") : raidPlayer(player, arg);
            case "MINE" -> player == null ? error("NO_OPERATOR", "Operator context required.") : ArenaState.get(player.getServer()).mine(player);
            case "UPGR" -> player == null ? error("NO_OPERATOR", "Operator context required.") : ArenaState.get(player.getServer()).upgrade(player, arg);
            case "REPAIR" -> player == null ? error("NO_OPERATOR", "Operator context required.") : ArenaState.get(player.getServer()).repairPower(player);
            case "FARM" -> player == null ? error("NO_OPERATOR", "Operator context required.") : ArenaState.get(player.getServer()).farm(player, arg);
            case "VPN" -> player == null ? error("NO_OPERATOR", "Operator context required.") : ArenaState.get(player.getServer()).vpn(player, arg);
            case "IP" -> player == null ? error("NO_OPERATOR", "Operator context required.") : ArenaState.get(player.getServer()).ipInfo(player);
            case "POLICE" -> player == null ? error("NO_OPERATOR", "Operator context required.") : ArenaState.get(player.getServer()).policeStatus(player);
            case "ROUND" -> player == null ? error("NO_OPERATOR", "Operator context required.") : ArenaState.get(player.getServer()).roundStatus(player);
            case "NET", "NETWORK" -> player == null ? error("NO_OPERATOR", "Operator context required.") : ArenaState.get(player.getServer()).network(player, arg);
            case "SABOTAGE" -> error("OBSOLETE", "Прямой саботаж отключён. Используй NET DISCOVER → SCAN → ENUM → EXPLOIT.");
            case "SELR" -> player == null ? error("NO_OPERATOR", "Operator context required.") : ArenaState.get(player.getServer()).sellData(player);
            case "FLOW" -> flow(arg, player);
            case "SCAN" -> scan(arg, player);
            case "CONTRACTS", "JOBS" -> "§bNEX JOB BOARD\n§fsite-audit (40c) §7| patch-rush (55c) §7| data-recovery (70c)";
            case "RUN" -> player == null ? error("NO_OPERATOR", "Player context required.") : ArenaState.get(player.getServer()).completeContract(player, arg);
            case "SITE" -> player == null ? error("NO_OPERATOR", "Player context required.") : ArenaState.get(player.getServer()).siteEvent(player, arg);
            default -> error("UNKNOWN_OPCODE", "Unknown instruction '" + parts[0] + "'. Type MANU.");
        };
    }

    private static String manual() {
        return "§bNX // СПРАВОЧНИК ОПЕРАТОРА 2.1\n"
            + "§fSEEK <слово> §7поиск каталога | WHO <сайт> §7профиль | PING <сайт> §7виртуальный отклик\n"
            + "§fPORTA <сайт> [слот] §7службы | FILES <сайт> §7список файлов | READ <сайт> <файл> §7описание записи\n"
            + "§fCRACK <сайт> §7игровое извлечение | RAID <игрок> §7PvP-рейд и кража ресурсов | SCAN <узел> §7скан узла | NODR §7карта узлов\n"
            + "§fMINE §7проверка майнера (доход идёт автоматически) | SELR §7обмен осколков | UPGR <модуль> §7улучшение\n"
            + "§fCONTRACTS §7заказы | RUN <id> §7завершить заказ | FLOW <a; b> §7сценарий (до 6 шагов)\n"
            + "§fREPAIR §7аварийное питание | FARM BUY/UPGRADE/REPAIR/STATUS §7домашняя ферма\n"
            + "§fVPN BUY/ON/OFF/STATUS §7виртуальный VPN | IP §7игровой адрес | POLICE §7слежение и розыск\n"            + "§fROUND §7цель 10 000 кредитов, прогресс и статус патруля\n"
            + "§fNET DISCOVER/SCAN/ENUM/EXPLOIT <игрок> [модуль] §7поэтапная сетевая операция\n"
            + "§fSABOTAGE §7быстрый режим; NET — основной способ атаковать\n"
            + "§fUPGR BREAKER/GUARD §7расходуемая защита дома\n"
            + "§7Команды: HELP, PORTS, LS, VIEW, JOBS. FLOW требует модуль AUTOM.\n"
            + "§7Все цели и данные вымышлены и существуют только внутри Cyber Arena.";
    }

    private static String error(String code, String message) { return "§cNEX/" + code + " §7| §f" + message; }
    private static String unquote(String value) {
        if (value.length() >= 2 && ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))))
            return value.substring(1, value.length() - 1);
        return value;
    }

    private static String flow(String script, ServerPlayerEntity player) {
        if (player == null) return error("NO_OPERATOR", "FLOW requires an operator session.");
        if (script.isBlank()) return error("FLOW_SYNTAX", "Use FLOW opcode; opcode; opcode.");
        if (script.length() > 240) return error("FLOW_LIMIT", "Routine limit is 240 characters.");
        String[] steps = script.split(";", -1);
        if (steps.length > 6) return error("FLOW_LIMIT", "Maximum 6 instructions per routine.");
        if (ArenaState.get(player.getServer()).view(player).automation() < 1)
            return error("AUTOMATION_LOCKED", "Automation is locked. Purchase AUTOM module first: UPGR AUTOM (35 credits).");
        StringBuilder result = new StringBuilder("§bNEX FLOW // ").append(steps.length).append(" STEPS");
        Set<String> allowed = Set.of("SEEK","WHO","PING","PORTA","PORTS","FILES","LS","READ","VIEW","CRACK","MINE","UPGR","SELR","SCAN","STAT","NODR","MAPA","CONTRACTS","JOBS");
        for (int i = 0; i < steps.length; i++) {
            String step = steps[i].trim();
            if (step.isEmpty()) return error("FLOW_SYNTAX", "Step " + (i + 1) + " is empty.");
            String opcode = step.split("\\s+", 2)[0].toUpperCase(Locale.ROOT);
            if (!allowed.contains(opcode)) return error("FLOW_BLOCKED", opcode + " is not permitted in routines.");
            String output = execute(step, player);
            result.append("\n§7[").append(i + 1).append("] §f").append(output);
            if (output.startsWith("§c")) {
                result.append("\n§eFLOW HALTED at step ").append(i + 1).append(".");
                break;
            }
        }
        return result.toString();
    }

    private static String nodes() {
        return "§bARENA NODE MAP §7| §f" + NODES.size() + " virtual nodes\n§f"
            + NODES.entrySet().stream().map(e -> e.getKey() + " §7- " + e.getValue()).collect(Collectors.joining("\n§f"));
    }

    private static String seek(String query) {
        if (query.isBlank()) return error("SEEK_SYNTAX", "Enter a search word.");
        String result = SITES.entrySet().stream()
            .filter(e -> e.getKey().contains(query) || e.getValue().toLowerCase(Locale.ROOT).contains(query))
            .sorted(Map.Entry.comparingByKey())
            .map(e -> e.getKey() + " §7- " + e.getValue())
            .collect(Collectors.joining("\n§f"));
        return result.isEmpty() ? "§eNEX/NO_MATCH §7| No .arena entries found." : "§bNEX WEB INDEX §7| §f" + SITES.size() + " total sites\n§f" + result;
    }

    private static String who(String site) {
        if (!SITES.containsKey(site)) return error("NO_HOST", "Unknown host. Use SEEK.");
        return "§bHOST PROFILE §7| §f" + site + "\n§fCLASS: " + SITES.get(site)
            + "\n§fSIMULATED SLOTS: " + PORTS.get(site).size() + "\n§fRECORDS: " + FILES.get(site).size()
            + "\n§7Host identity exists only in the Cyber Arena simulation.";
    }

    private static String ping(String site) {
        if (!SITES.containsKey(site)) return error("NO_HOST", "Unknown host. Use SEEK.");
        int latency = 18 + Math.floorMod(site.hashCode(), 83);
        return "§aARENA PING §7| §f" + site + " §7| §f" + latency + " ms §7| §aSIMULATED RESPONSE";
    }

    private static String ports(String args) {
        String[] parts = args.split("\\s+", 2);
        String site = parts[0];
        List<String> entries = PORTS.get(site);
        if (entries == null) return error("NO_HOST", "Unknown host. Use SEEK.");
        if (parts.length == 2) {
            String wanted = parts[1];
            return entries.stream().filter(p -> p.startsWith(wanted + "|") || p.startsWith(String.format("%03d|", parsePort(wanted))))
                .findFirst().map(p -> "§bSERVICE SLOT §7| §f" + formatPort(p))
                .orElseGet(() -> error("NO_SLOT", "This fictional slot is not registered on that host."));
        }
        return "§bPORTA REPORT §7| §f" + site + " §7| §f" + entries.size() + " service slots\n§f"
            + entries.stream().map(NexEngine::formatPort).collect(Collectors.joining("\n§f"))
            + "\n§7Slot numbers are fictional Minecraft services, not real TCP/UDP ports.";
    }

    private static int parsePort(String value) {
        try { return Integer.parseInt(value); } catch (NumberFormatException ignored) { return -1; }
    }
    private static String formatPort(String entry) {
        String[] p = entry.split("\\|", 4);
        return "§e" + p[0] + " §f" + p[1] + " §7/ §f" + p[2] + " §8- " + p[3];
    }

    private static String files(String site) {
        List<String> entries = FILES.get(site);
        if (entries == null) return error("NO_HOST", "Unknown host. Use SEEK.");
        return "§bNEX FILE INDEX §7| §f" + site + " §7| §f" + entries.size() + " records\n§f"
            + String.join("\n§f", entries) + "\n§7Use READ " + site + " <file>.";
    }

    private static String readFile(String args) {
        String[] parts = args.split("\\s+", 2);
        if (parts.length != 2) return error("READ_SYNTAX", "Use READ <site.arena> <file>.");
        List<String> entries = FILES.get(parts[0]);
        if (entries == null || !entries.contains(parts[1])) return error("NO_RECORD", "Record is not listed on this host.");
        String file = parts[1];
        String description = switch (file) {
            case "catalog.idx" -> "Public catalog index: 24 virtual product entries.";
            case "offers.dat" -> "Public offers and rotating game contract hints.";
            case "ledger.log" -> "Public simulated exchange activity.";
            case "bulletins.txt" -> "Public bulletin archive and fictional network notices.";
            case "dispatch.log" -> "Simulated message routing events.";
            case "public.key" -> "Public verification fingerprint for the service.";
            case "load.map" -> "Fictional district demand and supply values.";
            case "relay.cfg" -> "Three simulated relay service endpoints.";
            case "grid.log" -> "Virtual load-balancing events.";
            case "records.enc", "ledger.bak", "patient.enc", "manifest.enc", "accounts.enc", "prototype.idx", "specimen.enc", "mission.enc", "orbit.map", "vault.enc" -> "Protected record manifest. Payload requires an appropriate in-game contract or progression unlock.";
            default -> "Fictional public metadata record. No real personal data is stored.";
        };
        return "§bNEX RECORD §7| §f" + parts[0] + "/" + file + "\n§f" + description;
    }

    private static String status(ServerPlayerEntity player) {
        ArenaState.ProfileView p = ArenaState.get(player.getServer()).view(player);
        return "§aNX CORE 2.2 ONLINE §7| §fВНЕШНИЙ ДОСТУП ОТКЛЮЧЁН"
            + "\n§6Кредиты: " + p.credits() + " §7| §dREP: " + p.reputation() + " §7| §bОсколки: " + p.dataShards()
            + "\n§eFIREWALL " + p.firewall() + "/5 §7| AUTOM " + p.automation() + "/5 §7| STEALTH " + p.stealth() + "/5"
            + "\n§dEXTRACT " + p.extractor() + "/5 §7| MINER " + p.miningRig() + "/5 §7| CPU " + p.cpu() + "/5 §7| STORAGE " + p.storage() + "/5"
            + "\n§cЩиток: " + p.breakerCharges() + " зарядов §7| §aОхрана: " + p.guardCharges() + " зарядов"
            + "\n§bФерма: " + p.farmLevel() + "/5 (" + p.farmHealth() + "%) §7| VPN " + (p.vpnActive() ? "ON" : "OFF") + " §7| Розыск " + p.wantedLevel() + "/5"
            + "\n§7Игровой IP: " + (p.vpnActive() ? "203.0.113." : "198.51.100.") + (p.virtualIp() % 200 + 20);
    }

    private static String raidPlayer(ServerPlayerEntity attacker, String targetName) {
        if (targetName == null || targetName.isBlank()) return error("RAID_SYNTAX", "Use RAID <online_player>.");
        ServerPlayerEntity target = attacker.getServer().getPlayerManager().getPlayerList().stream().filter(p -> p.getGameProfile().getName().equalsIgnoreCase(targetName)).findFirst().orElse(null);
        if (target == null) return error("RAID_TARGET", "Player must be online on this server.");
        return ArenaState.get(attacker.getServer()).raid(attacker, target, attacker.getWorld().getTime());
    }

    private static String sabotagePlayer(ServerPlayerEntity attacker, String args) {
        String[] parts = args.split("\\s+", 2);
        if (parts.length != 2) return error("SABOTAGE_SYNTAX", "Use SABOTAGE <online_player> <FARM|POWER|SUPPLIES|ALARM>.");
        ServerPlayerEntity target = attacker.getServer().getPlayerManager().getPlayerList().stream().filter(p -> p.getGameProfile().getName().equalsIgnoreCase(parts[0])).findFirst().orElse(null);
        if (target == null) return error("SABOTAGE_TARGET", "Target player must be online.");
        return ArenaState.get(attacker.getServer()).sabotage(attacker, target, parts[1]);
    }

    private static String scan(String id, ServerPlayerEntity player) {
        String type = NODES.get(id);
        if (type == null) return error("NO_NODE", "Node not found. Use NODR.");
        if (player == null) return "§aSCAN COMPLETE §7| §f" + id + " §7| §f" + type;
        boolean fresh = ArenaState.get(player.getServer()).discoverNode(player, id);
        return "§aSCAN COMPLETE §7| §f" + id + " §7| §f" + type
            + (fresh ? "\n§d+1 reputation for mapping this simulated node." : "\n§7Already mapped.");
    }
}
