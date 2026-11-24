package dev.catik.cyberarena;

import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.text.Text;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class ArenaState extends PersistentState {
    private static final String STORAGE_KEY = "cyber_arena_profiles";
    private static final Type<ArenaState> TYPE = new Type<>(
        ArenaState::new, ArenaState::fromNbt, DataFixTypes.LEVEL
    );
    private final Map<UUID, PlayerProfile> profiles = new HashMap<>();
    private int nextHomeIndex = 0;

    public static ArenaState get(MinecraftServer server) {
        ServerWorld world = server.getOverworld();
        return world.getPersistentStateManager().getOrCreate(TYPE, STORAGE_KEY);
    }

    private PlayerProfile profile(ServerPlayerEntity player) {
        return profiles.computeIfAbsent(player.getUuid(), id -> { PlayerProfile p = new PlayerProfile(player.getGameProfile().getName()); p.virtualIp = Math.floorMod(id.hashCode(), 200) + 20; return p; });
    }

    public ProfileView view(ServerPlayerEntity player) {
        PlayerProfile p = profile(player);
        return new ProfileView(p.credits, p.reputation, p.dataShards, p.firewall, p.automation,
            p.stealth, p.extractor, p.miningRig, p.cpu, p.storage, p.alertLevel, p.completedContracts.size(), p.discoveredNodes.size(), p.breakerCharges, p.guardCharges, p.powerUntilTick, p.farmLevel, p.farmHealth, p.vpnOwned, p.vpnActive, p.wantedLevel, p.virtualIp);
    }

    public void ensureHome(ServerPlayerEntity player) {
        PlayerProfile p = profile(player);
        if (p.homeBuilt) return;
        ServerWorld world = player.getServer().getOverworld();
        if (p.homeIndex < 0) p.homeIndex = nextHomeIndex++;
        int gridX = p.homeIndex % 8;
        int gridZ = p.homeIndex / 8;
        BlockPos worldSpawn = world.getSpawnPos();
        int centerX = worldSpawn.getX() + (gridX - 3) * 14;
        int centerZ = worldSpawn.getZ() + (gridZ - 3) * 14;
        int floorY = findHomeFloor(world, centerX, centerZ);
        centerX = homeCandidateX;
        centerZ = homeCandidateZ;
        buildStarterHouse(world, centerX, floorY, centerZ);
        p.homeX = centerX; p.homeY = floorY; p.homeZ = centerZ; p.homeBuilt = true;
        BlockPos bedSpawn = new BlockPos(centerX, floorY + 1, centerZ);
        player.setSpawnPoint(world.getRegistryKey(), bedSpawn, 0.0F, true, false);
        player.teleport(world, centerX + 0.5, floorY + 1, centerZ + 0.5, 0.0F, 0.0F);
        markDirty();
        player.sendMessage(Text.literal("§a[CYBER ARENA] §fТвой дом и рабочая станция готовы. Точка возрождения установлена внутри."), false);
    }

    private int homeCandidateX;
    private int homeCandidateZ;

    private int findHomeFloor(ServerWorld world, int wantedX, int wantedZ) {
        for (int radius = 0; radius <= 48; radius += 4) {
            for (int dx = -radius; dx <= radius; dx += 4) {
                for (int dz = -radius; dz <= radius; dz += 4) {
                    if (radius > 0 && Math.abs(dx) != radius && Math.abs(dz) != radius) continue;
                    int x = wantedX + dx;
                    int z = wantedZ + dz;
                    int y = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
                    BlockPos ground = new BlockPos(x, y - 1, z);
                    if (world.getBlockState(ground).isSolidBlock(world, ground)) {
                        homeCandidateX = x;
                        homeCandidateZ = z;
                        return y;
                    }
                }
            }
        }
        homeCandidateX = wantedX;
        homeCandidateZ = wantedZ;
        return world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, wantedX, wantedZ);
    }

    private static void buildStarterHouse(ServerWorld world, int x, int y, int z) {
        for (int dx = -3; dx <= 3; dx++) for (int dz = -3; dz <= 3; dz++) {
            world.setBlockState(new BlockPos(x + dx, y, z + dz), Blocks.OAK_PLANKS.getDefaultState());
            for (int dy = 1; dy <= 3; dy++) {
                boolean wall = Math.abs(dx) == 3 || Math.abs(dz) == 3;
                if (!wall) world.setBlockState(new BlockPos(x + dx, y + dy, z + dz), Blocks.AIR.getDefaultState());
                else if (dz == 3 && dx == 0 && dy <= 2) world.setBlockState(new BlockPos(x + dx, y + dy, z + dz), Blocks.AIR.getDefaultState());
                else if (dy == 2 && (Math.abs(dx) == 3 || Math.abs(dz) == 3) && (Math.abs(dx) == 3 || Math.abs(dz) == 3) && ((dx == -3 && dz == 0) || (dx == 3 && dz == 0) || (dx == 0 && dz == -3))) world.setBlockState(new BlockPos(x + dx, y + dy, z + dz), Blocks.GLASS.getDefaultState());
                else world.setBlockState(new BlockPos(x + dx, y + dy, z + dz), Blocks.SPRUCE_PLANKS.getDefaultState());
            }
            world.setBlockState(new BlockPos(x + dx, y + 4, z + dz), Blocks.SPRUCE_PLANKS.getDefaultState());
        }
        world.setBlockState(new BlockPos(x + 2, y + 1, z + 2), Blocks.CRAFTING_TABLE.getDefaultState());
        world.setBlockState(new BlockPos(x - 2, y + 1, z + 2), Blocks.BARREL.getDefaultState());
        world.setBlockState(new BlockPos(x, y + 3, z), Blocks.LANTERN.getDefaultState());
    }

    public static void tickServer(MinecraftServer server) {
        ArenaState state = get(server);
        long now = server.getOverworld().getTime();
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            PlayerProfile p = state.profile(player);
            if (p.lastPassiveIncomeTick < 0) p.lastPassiveIncomeTick = now;
            if (now - p.lastPassiveIncomeTick >= 1200) {
                long cycles = Math.min(10, (now - p.lastPassiveIncomeTick) / 1200);
                int income = (int) cycles * ((2 + p.miningRig * 2) + (p.farmLevel > 0 && p.farmHealth > 0 ? 5 + p.farmLevel * 3 : 0));
                p.credits += income;
                p.lastPassiveIncomeTick += cycles * 1200;
                player.sendMessage(Text.literal("§6[МАЙНИНГ] §fПассивный доход: §a+" + income + " кредитов §7| §eMINER " + p.miningRig + "/5"), true);
                state.markDirty();
            }
            if (now - p.lastIncidentCheckTick >= 1200) {
                p.lastIncidentCheckTick = now;
                if (now - p.lastIncidentTick >= 6000 && ThreadLocalRandom.current().nextInt(100) < 35) {
                    p.lastIncidentTick = now;
                    state.triggerHomeIncident(player, p, now);
                    state.markDirty();
                }
            }
            if (p.wantedLevel >= 4 && now - p.lastPoliceCheckTick >= 1200) {
                p.lastPoliceCheckTick = now;
                if (!p.vpnActive) {
                    int fine = Math.min(p.credits, 10 + p.wantedLevel * 8);
                    p.credits -= fine;
                    p.wantedLevel = Math.max(0, p.wantedLevel - 2);
                    player.sendMessage(Text.literal("§c[ПОЛИЦИЯ ARENA] §fПатруль вычислил твой игровой адрес. Штраф: " + fine + " кредитов."), false);
                    state.markDirty();
                }
            }
            if (now % 40 == 0) state.sendOverlay(player, p, now);
        }
    }

    private void triggerHomeIncident(ServerPlayerEntity player, PlayerProfile p, long now) {
        if (ThreadLocalRandom.current().nextBoolean()) {
            if (p.breakerCharges > 0) {
                p.breakerCharges--;
                player.sendMessage(Text.literal("§e[САБОТАЖ] §fПопытка отключить питание! Электрощиток выдержал удар и сгорел. Осталось зарядов: " + p.breakerCharges), false);
            } else {
                p.powerUntilTick = now + 1200;
                player.sendMessage(Text.literal("§c[АВАРИЯ] §fПитание дома отключено на 60 секунд! Рабочая станция заблокирована. Используй REPAIR."), false);
            }
        } else {
            if (p.guardCharges > 0) {
                p.guardCharges--;
                player.sendMessage(Text.literal("§e[НАПАДЕНИЕ] §fОхрана отбила налёт, но потеряла один заряд. Осталось: " + p.guardCharges), false);
            } else {
                BlockPos home = new BlockPos(p.homeX, p.homeY + 1, p.homeZ);
                for (int i = 0; i < 2; i++) {
                    BlockPos spawn = home.add(i == 0 ? -2 : 2, 0, 1);
                    PillagerEntity raider = EntityType.PILLAGER.spawn(player.getServerWorld(), spawn, SpawnReason.EVENT);
                    if (raider != null) { raider.setPersistent(); raider.setTarget(player); }
                }
                player.sendMessage(Text.literal("§c[ВТОРЖЕНИЕ] §fРейдеры ворвались в дом! Защищай рабочую станцию."), false);
            }
        }
    }

    private void sendOverlay(ServerPlayerEntity player, PlayerProfile p, long now) {
        String power = p.powerUntilTick > now ? "§cПИТАНИЕ ОТКЛЮЧЕНО " + ((p.powerUntilTick - now + 19) / 20) + "с" : "§aПИТАНИЕ: ВКЛ";
        player.sendMessage(Text.literal("§bCYBER §7| §6" + p.credits + " кр. §7| §dREP " + p.reputation
            + " §7| §eMINER +" + (2 + p.miningRig * 2) + "/мин §7| " + power
            + " §7| §fЩиток " + p.breakerCharges + " §7| Охрана " + p.guardCharges + " §7| §bФерма " + p.farmHealth + "% §7| §cРозыск " + p.wantedLevel), true);
    }

    public boolean isPowerOut(ServerPlayerEntity player) {
        return profile(player).powerUntilTick > player.getServer().getOverworld().getTime();
    }


    public String farm(ServerPlayerEntity player, String action) {
        PlayerProfile p = profile(player);
        switch (action.toLowerCase()) {
            case "buy" -> {
                if (p.farmLevel > 0) return "§eМайнинг-ферма уже установлена. Используй FARM UPGRADE.";
                if (p.credits < 120) return "§cНужно 120 кредитов для покупки фермы.";
                p.credits -= 120; p.farmLevel = 1; p.farmHealth = 100; markDirty();
                return "§aФЕРМА УСТАНОВЛЕНА | Уровень 1 | Прочность 100% | Доход +8 кредитов/мин.";
            }
            case "upgrade" -> {
                if (p.farmLevel == 0) return "§cСначала купи ферму: FARM BUY.";
                if (p.farmLevel >= 5) return "§eФерма уже максимального 5 уровня.";
                int cost = 100 + p.farmLevel * 90;
                if (p.credits < cost) return "§cУлучшение фермы стоит " + cost + " кредитов.";
                p.credits -= cost; p.farmLevel++; p.farmHealth = Math.min(100, p.farmHealth + 20); markDirty();
                return "§aФЕРМА УЛУЧШЕНА | Уровень " + p.farmLevel + "/5 | Доход +" + (5 + p.farmLevel * 3) + "/мин.";
            }
            case "repair" -> {
                if (p.farmLevel == 0) return "§cФермы нет. Используй FARM BUY.";
                if (p.farmHealth >= 100) return "§aФерма полностью исправна.";
                int cost = Math.max(10, (100 - p.farmHealth) / 2);
                if (p.credits < cost) return "§cРемонт фермы стоит " + cost + " кредитов.";
                p.credits -= cost; p.farmHealth = 100; markDirty();
                return "§aФЕРМА ОТРЕМОНТИРОВАНА | 100% | -" + cost + " кредитов.";
            }
            case "status" -> {
                return "§bМАЙНИНГ-ФЕРМА | " + (p.farmLevel == 0 ? "не куплена" : "уровень " + p.farmLevel + "/5")
                    + " | Прочность " + p.farmHealth + "% | Доход " + (p.farmLevel > 0 && p.farmHealth > 0 ? 5 + p.farmLevel * 3 : 0) + "/мин";
            }
            default -> { return "§eFARM BUY | UPGRADE | REPAIR | STATUS"; }
        }
    }

    public String vpn(ServerPlayerEntity player, String action) {
        PlayerProfile p = profile(player);
        switch (action.toLowerCase()) {
            case "buy" -> {
                if (p.vpnOwned) return "§eVPN уже приобретён.";
                if (p.credits < 90) return "§cVPN-модуль стоит 90 кредитов.";
                p.credits -= 90; p.vpnOwned = true; markDirty();
                return "§aVPN-МОДУЛЬ КУПЛЕН. Включи его: VPN ON.";
            }
            case "on" -> {
                if (!p.vpnOwned) return "§cСначала купи VPN: VPN BUY.";
                p.vpnActive = true; p.wantedLevel = Math.max(0, p.wantedLevel - 1); markDirty();
                return "§aVPN ПОДКЛЮЧЁН | Игровой адрес скрыт за виртуальным узлом.";
            }
            case "off" -> { p.vpnActive = false; markDirty(); return "§eVPN ОТКЛЮЧЁН | Будет виден твой игровой адрес."; }
            case "status" -> { return "§bVPN | " + (p.vpnOwned ? "куплен" : "не куплен") + " | " + (p.vpnActive ? "§aподключён" : "§cотключён") + " | IP " + (p.vpnActive ? "203.0.113." + (p.virtualIp % 200 + 20) : "198.51.100." + (p.virtualIp % 200 + 20)); }
            default -> { return "§eVPN BUY | ON | OFF | STATUS"; }
        }
    }

    public String ipInfo(ServerPlayerEntity player) {
        PlayerProfile p = profile(player);
        String ip = p.vpnActive ? "203.0.113." + (p.virtualIp % 200 + 20) : "198.51.100." + (p.virtualIp % 200 + 20);
        return "§bARENA IP | " + ip + (p.vpnActive ? " §a(VPN)" : " §c(открытый игровой адрес)") + " | Зарезервированный фиктивный адрес.";
    }

    public String policeStatus(ServerPlayerEntity player) {
        PlayerProfile p = profile(player);
        return "§cПОЛИЦИЯ ARENA | Розыск " + p.wantedLevel + "/5 | "
            + (p.wantedLevel >= 4 ? "патруль ищет твой игровой адрес" : p.wantedLevel >= 2 ? "подозрение" : "ты не в розыске")
            + " | VPN " + (p.vpnActive ? "активен" : "неактивен");
    }

    public String sabotage(ServerPlayerEntity attacker, ServerPlayerEntity target, String action) {
        if (attacker.getUuid().equals(target.getUuid())) return "§cНельзя саботировать собственный дом.";
        PlayerProfile a = profile(attacker), d = profile(target);
        long now = attacker.getServer().getOverworld().getTime();
        if (now - a.lastRaidTick < 1200) return "§eПодлянки на перезарядке.";
        a.lastRaidTick = now;
        boolean traced = !a.vpnActive && ThreadLocalRandom.current().nextInt(100) < Math.max(15, 65 - a.stealth * 8);
        if (traced) a.wantedLevel = Math.min(5, a.wantedLevel + 1);
        String result = switch (action.toLowerCase()) {
            case "farm" -> {
                if (d.farmLevel == 0 || d.farmHealth == 0) yield "§eУ цели нет работающей фермы.";
                int damage = Math.min(45, d.farmHealth); d.farmHealth -= damage;
                yield "§cСАБОТАЖ ФЕРМЫ | -" + damage + "% прочности | Доход остановлен до ремонта.";
            }
            case "power" -> { d.powerUntilTick = now + 1200; yield "§cОТКЛЮЧЕНИЕ ПИТАНИЯ | Станция цели отключена на 60 секунд."; }
            case "supplies" -> {
                int stolen = Math.min(d.credits, 20 + a.extractor * 5);
                d.credits -= stolen; a.credits += stolen;
                yield "§eКРАЖА ЗАПАСОВ | Перенесено " + stolen + " игровых кредитов.";
            }
            case "alarm" -> { d.alertLevel = Math.min(5, d.alertLevel + 2); yield "§6ЛОЖНАЯ ТРЕВОГА | Сигнализация цели перегружена."; }
            default -> "§cВарианты: FARM, POWER, SUPPLIES, ALARM.";
        };
        markDirty();
        if (traced) attacker.sendMessage(Text.literal("§c[ПОЛИЦИЯ] §fТвой виртуальный след обнаружен. Розыск +" + 1 + "."), false);
        target.sendMessage(Text.literal("§c[ДОМ] §fОбнаружена попытка саботажа: " + action.toUpperCase()), false);
        return result + (traced ? " §c| След обнаружен, розыск " + a.wantedLevel + "/5." : " §a| След не обнаружен.");
    }

    public String repairPower(ServerPlayerEntity player) {
        PlayerProfile p = profile(player);
        if (p.powerUntilTick <= player.getServer().getOverworld().getTime()) return "§aПитание уже работает.";
        if (p.credits < 15) return "§cДля аварийного ремонта нужно 15 кредитов.";
        p.credits -= 15; p.powerUntilTick = 0; markDirty();
        return "§aПИТАНИЕ ВОССТАНОВЛЕНО §7| §6-15 кредитов";
    }

    public boolean discoverNode(ServerPlayerEntity player, String nodeId) {
        PlayerProfile p = profile(player);
        if (!p.discoveredNodes.add(nodeId)) return false;
        p.reputation += 1;
        markDirty();
        return true;
    }

    public String completeContract(ServerPlayerEntity player, String contractId) {
        PlayerProfile p = profile(player);
        int reward = switch (contractId) {
            case "site-audit" -> 40;
            case "patch-rush" -> 55;
            case "data-recovery" -> 70;
            default -> -1;
        };
        if (reward < 0) return "§cUnknown contract. Available: site-audit, patch-rush, data-recovery.";
        if (!p.completedContracts.add(contractId)) return "§eContract already completed. Contracts are one-time starter jobs.";
        p.credits += reward;
        p.reputation += 3;
        markDirty();
        return "§aCONTRACT COMPLETE §7| §6+" + reward + " credits §7| §d+3 reputation";
    }

    public String raid(ServerPlayerEntity attacker, ServerPlayerEntity target, long now) {
        if (attacker.getUuid().equals(target.getUuid())) return "§cYou cannot raid your own machine.";
        PlayerProfile a = profile(attacker);
        PlayerProfile d = profile(target);
        if (now - a.lastRaidTick < 1200) return "§eAttack automation is cooling down. Wait " + ((1200 - (now - a.lastRaidTick) + 19) / 20) + "s.";
        a.lastRaidTick = now;
        int chance = Math.max(8, Math.min(92, 60 + a.automation * 5 + a.stealth * 4 + a.extractor * 3 - d.firewall * 12));
        boolean success = ThreadLocalRandom.current().nextInt(100) < chance;
        if (success) {
            int creditsTaken = Math.min(d.credits, Math.max(1, d.credits / 4));
            int shardsTaken = Math.min(d.dataShards, Math.max(1, d.dataShards / 4));
            d.credits -= creditsTaken;
            a.credits += creditsTaken;
            d.dataShards -= shardsTaken;
            a.dataShards += shardsTaken + 1;
            a.reputation += 2;
            d.alertLevel = Math.min(5, d.alertLevel + 1);
            markDirty();
            return "§aSIMULATED BREACH SUCCESS §7| §f" + target.getName().getString()
                + " §7| §6+" + creditsTaken + " credits §7| §b+" + (shardsTaken + 1) + " data shards §7| §cTarget alert " + d.alertLevel + "/5";
        }
        d.alertLevel = Math.min(5, d.alertLevel + 1);
        a.reputation = Math.max(0, a.reputation - 1);
        markDirty();
        return "§cBREACH BLOCKED §7| §f" + target.getName().getString()
            + " §7| §bFirewall " + d.firewall + " §7| §cYour trace was exposed.";
    }

    public String breachSite(ServerPlayerEntity player, String site) {
        PlayerProfile p = profile(player);
        int difficulty = switch (site) { case "shop.arena", "forum.arena", "weather.arena", "museum.arena", "farm.arena" -> 1; case "news.arena", "uni.arena", "media.arena" -> 2; case "grid.arena", "metro.arena", "clinic.arena", "port.arena" -> 3; case "vault.arena", "bank.arena", "factory.arena", "casino.arena", "auction.arena" -> 4; case "labs.arena", "security.arena", "space.arena", "robot.arena" -> 5; case "legal.arena", "archive2.arena", "power.arena", "social.arena" -> 4; case "craft.arena", "travel.arena", "food.arena" -> 1; case "music.arena", "sports.arena" -> 2; default -> -1; };
        if (difficulty < 0) return "§cUnknown .arena target. Use SEEK first.";
        if (p.completedContracts.contains("breach:" + site)) return "§eThis virtual data vault is already cleared.";
        long now = player.getWorld().getTime();
        if (now - p.lastSiteRaidTick < 400) return "§eNEX cooldown: " + ((400 - (now - p.lastSiteRaidTick) + 19) / 20) + "s.";
        p.lastSiteRaidTick = now;
        int chance = Math.max(10, Math.min(95, 78 + p.automation * 4 + p.stealth * 5 + p.extractor * 4 - difficulty * 12));
        if (ThreadLocalRandom.current().nextInt(100) >= chance) { p.alertLevel = Math.min(5, p.alertLevel + 1); markDirty(); return "§cACCESS REJECTED | ALERT " + p.alertLevel + "/5. Upgrade STEALTH or AUTOM."; }
        int shards = 1 + p.extractor / 2 + (difficulty >= 4 ? 1 : 0);
        int credits = 12 + difficulty * 7;
        if (p.dataShards + shards > 10 + p.storage * 10) return "§cDATA VAULT FULL. Sell shards with SELR or upgrade STORAGE.";
        p.dataShards += shards; p.credits += credits; p.reputation += difficulty; p.completedContracts.add("breach:" + site); markDirty();
        return "§aDATA EXTRACTED | §f" + site + " §b+" + shards + " shards §6+" + credits + " credits §d+" + difficulty + " reputation";
    }

    public String mine(ServerPlayerEntity player) {
        PlayerProfile p = profile(player); long now = player.getWorld().getTime();
        if (now - p.lastMineTick < 1200) return "§eMINER BUSY. " + ((1200 - (now - p.lastMineTick) + 19) / 20) + "s remain.";
        int capacity = 10 + p.storage * 10;
        if (p.dataShards >= capacity) return "§cDATA VAULT FULL. Sell shards or upgrade STORAGE.";
        p.lastMineTick = now; int made = Math.min(capacity - p.dataShards, 2 + p.miningRig * 2 + p.cpu);
        p.dataShards += made; p.credits += made * (2 + p.miningRig); markDirty();
        return "§aMINING CYCLE §b+" + made + " shards §6+" + (made * (2 + p.miningRig)) + " credits";
    }

    public String sellData(ServerPlayerEntity player) {
        PlayerProfile p = profile(player); if (p.dataShards == 0) return "§eNo data shards to sell.";
        int amount = p.dataShards, payout = amount * 8; p.dataShards = 0; p.credits += payout; markDirty();
        return "§aDATA EXCHANGE §b-" + amount + " shards §6+" + payout + " credits";
    }

    public String upgrade(ServerPlayerEntity player, String type) {
        PlayerProfile p = profile(player);
        int level = switch (type) {
            case "firewall" -> p.firewall; case "autom", "automation" -> p.automation; case "breaker" -> p.breakerLevel; case "guard", "guards" -> p.guardLevel;
            case "stealth" -> p.stealth; case "extract", "extractor" -> p.extractor;
            case "miner", "mining" -> p.miningRig; case "cpu" -> p.cpu; case "storage" -> p.storage;
            default -> -1;
        };
        if (level < 0) return "§cUPGR types: FIREWALL, AUTOM, STEALTH, EXTRACT, MINER, CPU, STORAGE.";
        if (level >= 5) return "§eModule already at maximum level 5.";
        int cost = 35 + level * 45 + (type.equals("firewall") ? 15 : 0);
        if (p.credits < cost) return "§cINSUFFICIENT CREDITS. Required: " + cost + ".";
        p.credits -= cost;
        switch (type) {
            case "firewall" -> p.firewall++; case "autom", "automation" -> p.automation++; case "breaker" -> { p.breakerLevel++; p.breakerCharges++; } case "guard", "guards" -> { p.guardLevel++; p.guardCharges++; }
            case "stealth" -> p.stealth++; case "extract", "extractor" -> p.extractor++;
            case "miner", "mining" -> p.miningRig++; case "cpu" -> p.cpu++; case "storage" -> p.storage++;
        }
        markDirty();
        return "§aMODULE INSTALLED §f" + type.toUpperCase() + " §eLEVEL " + (level + 1) + "/5 §6-" + cost + " credits";
    }

    public String siteEvent(ServerPlayerEntity player, String site) {
        PlayerProfile p = profile(player);
        int reward = switch (site) {
            case "shop.arena" -> 18;
            case "news.arena" -> 22;
            case "vault.arena" -> 30;
            default -> -1;
        };
        if (reward < 0) return "§cUnknown simulated site. Try shop.arena, news.arena, vault.arena.";
        String key = "site:" + site;
        if (!p.completedContracts.add(key)) return "§eThis site's security incident was already resolved by you.";
        p.credits += reward;
        p.reputation += 1;
        markDirty();
        return "§aSITE INCIDENT RESOLVED §7| §f" + site + " §7| §6+" + reward + " credits";
    }

    public record ProfileView(int credits, int reputation, int dataShards, int firewall, int automation, int stealth, int extractor, int miningRig, int cpu, int storage, int alertLevel, int completedContracts, int discoveredNodes, int breakerCharges, int guardCharges, long powerUntilTick, int farmLevel, int farmHealth, boolean vpnOwned, boolean vpnActive, int wantedLevel, int virtualIp) {}

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList entries = new NbtList();
        profiles.forEach((uuid, p) -> {
            NbtCompound e = new NbtCompound();
            e.putUuid("uuid", uuid); e.putString("name", p.name);
            e.putInt("credits", p.credits); e.putInt("reputation", p.reputation);
            e.putInt("data_shards", p.dataShards); e.putInt("firewall", p.firewall);
            e.putInt("automation", p.automation); e.putInt("stealth", p.stealth); e.putInt("extractor", p.extractor); e.putInt("miner", p.miningRig); e.putInt("cpu", p.cpu); e.putInt("storage", p.storage); e.putInt("alert", p.alertLevel);
            e.putLong("last_raid", p.lastRaidTick); e.putLong("last_site_raid", p.lastSiteRaidTick); e.putLong("last_mine", p.lastMineTick);
            e.putLong("last_passive_income", p.lastPassiveIncomeTick); e.putLong("last_incident", p.lastIncidentTick); e.putLong("last_incident_check", p.lastIncidentCheckTick); e.putLong("power_until", p.powerUntilTick);
            e.putInt("breaker_level", p.breakerLevel); e.putInt("guard_level", p.guardLevel); e.putInt("breaker_charges", p.breakerCharges); e.putInt("guard_charges", p.guardCharges);
            e.putInt("farm_level", p.farmLevel); e.putInt("farm_health", p.farmHealth); e.putBoolean("vpn_owned", p.vpnOwned); e.putBoolean("vpn_active", p.vpnActive); e.putInt("wanted", p.wantedLevel); e.putInt("virtual_ip", p.virtualIp); e.putLong("last_police_check", p.lastPoliceCheckTick);\n            e.putInt("home_index", p.homeIndex); e.putInt("home_x", p.homeX); e.putInt("home_y", p.homeY); e.putInt("home_z", p.homeZ); e.putBoolean("home_built", p.homeBuilt);
            NbtList nodes = new NbtList();
            p.discoveredNodes.forEach(v -> { NbtCompound n = new NbtCompound(); n.putString("id", v); nodes.add(n); });
            e.put("nodes", nodes);
            NbtList jobs = new NbtList();
            p.completedContracts.forEach(v -> { NbtCompound c = new NbtCompound(); c.putString("id", v); jobs.add(c); });
            e.put("jobs", jobs);
            entries.add(e);
        });
        nbt.putInt("schema_version", 5);
        nbt.put("profiles", entries);
        return nbt;
    }

    private static ArenaState fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        ArenaState state = new ArenaState();
        if (nbt.getInt("schema_version") < 3) return state;
        NbtList entries = nbt.getList("profiles", NbtCompound.COMPOUND_TYPE);
        for (int i = 0; i < entries.size(); i++) {
            NbtCompound e = entries.getCompound(i);
            if (!e.containsUuid("uuid")) continue;
            PlayerProfile p = new PlayerProfile(e.getString("name"));
            p.credits = Math.max(0, e.getInt("credits")); p.reputation = Math.max(0, e.getInt("reputation"));
            p.dataShards = Math.max(0, e.getInt("data_shards")); p.firewall = clamp(e.getInt("firewall"), 0, 5);
            p.automation = clamp(e.getInt("automation"), 0, 5); p.stealth = clamp(e.getInt("stealth"), 0, 5); p.extractor = clamp(e.getInt("extractor"), 0, 5); p.miningRig = clamp(e.getInt("miner"), 0, 5); p.cpu = clamp(e.getInt("cpu"), 0, 5); p.storage = clamp(e.getInt("storage"), 0, 5); p.alertLevel = clamp(e.getInt("alert"), 0, 5); p.lastSiteRaidTick = e.getLong("last_site_raid"); p.lastMineTick = e.getLong("last_mine");
            p.lastRaidTick = e.getLong("last_raid");
            p.lastPassiveIncomeTick = e.contains("last_passive_income") ? e.getLong("last_passive_income") : -1; p.lastIncidentTick = e.getLong("last_incident"); p.lastIncidentCheckTick = e.getLong("last_incident_check"); p.powerUntilTick = e.getLong("power_until");
            p.breakerLevel = clamp(e.getInt("breaker_level"), 0, 5); p.guardLevel = clamp(e.getInt("guard_level"), 0, 5); p.breakerCharges = clamp(e.getInt("breaker_charges"), 0, 5); p.guardCharges = clamp(e.getInt("guard_charges"), 0, 5);
            p.farmLevel = clamp(e.getInt("farm_level"), 0, 5); p.farmHealth = clamp(e.getInt("farm_health"), 0, 100); p.vpnOwned = e.getBoolean("vpn_owned"); p.vpnActive = e.getBoolean("vpn_active"); p.wantedLevel = clamp(e.getInt("wanted"), 0, 5); p.virtualIp = e.contains("virtual_ip") ? clamp(e.getInt("virtual_ip"), 20, 219) : Math.floorMod(e.getUuid("uuid").hashCode(), 200) + 20; p.lastPoliceCheckTick = e.getLong("last_police_check");\n            p.homeIndex = e.contains("home_index") ? e.getInt("home_index") : -1; p.homeX = e.getInt("home_x"); p.homeY = e.getInt("home_y"); p.homeZ = e.getInt("home_z"); p.homeBuilt = e.getBoolean("home_built");
            if (p.homeIndex >= 0) state.nextHomeIndex = Math.max(state.nextHomeIndex, p.homeIndex + 1);
            readSet(e.getList("nodes", NbtCompound.COMPOUND_TYPE), p.discoveredNodes);
            readSet(e.getList("jobs", NbtCompound.COMPOUND_TYPE), p.completedContracts);
            state.profiles.put(e.getUuid("uuid"), p);
        }
        return state;
    }

    private static void readSet(NbtList list, Set<String> target) {
        for (int i = 0; i < list.size(); i++) {
            String value = list.getCompound(i).getString("id");
            if (!value.isBlank()) target.add(value);
        }
    }
    private static int clamp(int v, int min, int max) { return Math.max(min, Math.min(max, v)); }

    private static final class PlayerProfile {
        private final String name;
        private int credits = 0, reputation = 0, dataShards = 0, firewall = 0, automation = 0, stealth = 0, extractor = 0, miningRig = 0, cpu = 0, storage = 0, alertLevel = 0;
        private int breakerLevel = 0, guardLevel = 0, breakerCharges = 0, guardCharges = 0;
        private int homeIndex = -1, homeX, homeY, homeZ; private boolean homeBuilt = false;
        private long lastRaidTick = -100000, lastSiteRaidTick = -100000, lastMineTick = -100000, lastPassiveIncomeTick = -1, lastIncidentTick = 0, lastIncidentCheckTick = 0, powerUntilTick = 0;
        private final Set<String> discoveredNodes = new HashSet<>();
        private final Set<String> completedContracts = new HashSet<>();
        private PlayerProfile(String name) { this.name = name; }
    }
}
