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
import net.minecraft.world.GameMode;

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
        ServerWorld world = player.getServer().getOverworld();
        if (p.eliminated || p.roundWon) { player.changeGameMode(GameMode.SPECTATOR); return; }
        if (p.homeBuilt) {
            refreshHomeDisplays(player, p);
            BlockPos homeSpawn = new BlockPos(p.homeX, p.homeY + 1, p.homeZ);
            player.setSpawnPoint(world.getRegistryKey(), homeSpawn, 0.0F, true, false);
            player.teleport(world, p.homeX + 0.5, p.homeY + 1, p.homeZ + 0.5, player.getYaw(), player.getPitch());
            return;
        }
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
        refreshHomeDisplays(player, p);
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
            if (p.eliminated || p.roundWon) { if (player.interactionManager.getGameMode() != GameMode.SPECTATOR) player.changeGameMode(GameMode.SPECTATOR); continue; }
            if (now - p.lastPassiveIncomeTick >= 1200) {
                long cycles = Math.min(10, (now - p.lastPassiveIncomeTick) / 1200);
                int income = (int) cycles * ((2 + p.miningRig * 2) + (p.farmLevel > 0 && p.farmHealth > 0 ? 5 + p.farmLevel * 3 : 0));
                p.credits += income;
                p.lastPassiveIncomeTick += cycles * 1200;
                player.sendMessage(Text.literal("§6[МАЙНИНГ] §fПассивный доход: §a+" + income + " кредитов §7| §eMINER " + p.miningRig + "/5"), true);
                state.markDirty();
            }
            if (p.credits >= 10000) {
                p.credits = 10000; p.roundWon = true; state.markDirty();
                player.sendMessage(Text.literal("§a[CYBER ARENA] §fЦЕЛЬ ДОСТИГНУТА: 10 000 КРЕДИТОВ. ТЫ ВЫИГРАЛ РАУНД!"), false);
                player.changeGameMode(GameMode.SPECTATOR); continue;
            }
            if (now - p.lastIncidentCheckTick >= 1200) {
                p.lastIncidentCheckTick = now;
                if (now - p.lastIncidentTick >= 6000 && ThreadLocalRandom.current().nextInt(100) < 35) {
                    p.lastIncidentTick = now;
                    state.triggerHomeIncident(player, p, now);
                    state.markDirty();
                }
            }
            if (p.networkHeat > 0 && now - p.lastNetworkActivityTick >= 1200) {
                p.networkHeat = Math.max(0, p.networkHeat - 1);
                p.lastNetworkActivityTick = now;
                state.markDirty();
            }
            if ((p.wantedLevel > 0 || p.networkHeat >= 2) && now - p.lastPoliceCheckTick >= 600) {
                p.lastPoliceCheckTick = now;
                int baseChance = 12 + p.wantedLevel * 13 + p.networkHeat * 8;
                int detectionChance = Math.max(3, Math.min(90, baseChance - (p.vpnActive ? 35 : 0) - p.stealth * 3));
                if (ThreadLocalRandom.current().nextInt(100) < detectionChance) {
                    boolean attributed = !p.vpnActive || ThreadLocalRandom.current().nextInt(100) < 18;
                    if (attributed) p.wantedLevel = Math.min(5, p.wantedLevel + 1);
                    if (p.wantedLevel >= 4 && attributed && p.homeBuilt && !p.policeStakeout) {
                        p.policeStakeout = true;
                        BlockPos patrolPos = new BlockPos(p.homeX + 5, p.homeY + 1, p.homeZ + 5);
                        var officer = EntityType.IRON_GOLEM.spawn(player.getServerWorld(), patrolPos, SpawnReason.EVENT);
                        if (officer != null) { officer.setPersistent(); officer.setCustomName(Text.literal("§cПОЛИЦИЯ ARENA")); officer.setCustomNameVisible(true); }
                        player.sendMessage(Text.literal("§c[ПОЛИЦИЯ ARENA] §fТвой адрес установлен. Патруль занял позицию у дома. Если войдёшь внутрь — задержание и выбывание из раунда."), false);
                    } else {
                        player.sendMessage(Text.literal("§6[ПОЛИЦИЯ ARENA] §fСетевые журналы показывают подозрительную активность: " + p.lastNetworkEvent + ". " + (attributed ? "Источник частично установлен." : "VPN скрыл источник, но событие осталось в журнале.")), false);
                    }
                    state.markDirty();
                }
            }
            if (p.policeStakeout && !p.eliminated && !p.roundWon && isInsideHome(player, p)) {
                p.eliminated = true; state.markDirty();
                player.sendMessage(Text.literal("§c[ПОЛИЦИЯ ARENA] §fТЕБЯ ЗАДЕРЖАЛИ. СЕТЬ ПОЛНОСТЬЮ РАСКРЫТА. ТЫ ВЫБЫЛ ИЗ РАУНДА."), false);
                player.changeGameMode(GameMode.SPECTATOR);
                for (ServerPlayerEntity other : server.getPlayerManager().getPlayerList()) if (!other.getUuid().equals(player.getUuid()))
                    other.sendMessage(Text.literal("§c[ARENA] §fИгрок " + player.getGameProfile().getName() + " задержан полицией и выбыл из раунда."), false);
                continue;
            }
            if (now % 40 == 0) state.sendOverlay(player, p, now);
        }
    }

    private static boolean isInsideHome(ServerPlayerEntity player, PlayerProfile p) {
        return p.homeBuilt && player.getBlockX() >= p.homeX - 3 && player.getBlockX() <= p.homeX + 3
            && player.getBlockZ() >= p.homeZ - 3 && player.getBlockZ() <= p.homeZ + 3
            && player.getY() >= p.homeY && player.getY() <= p.homeY + 6;
    }

    private void triggerHomeIncident(ServerPlayerEntity player, PlayerProfile p, long now) {
        if (p.farmLevel > 0 && p.farmHealth > 0 && ThreadLocalRandom.current().nextInt(4) == 0) {
            int damage = Math.min(25, p.farmHealth);
            p.farmHealth -= damage;
            if (p.farmHealth == 0) playerBreakFarm(player, p);
            player.sendMessage(Text.literal("§c[ДОМ] §fДиверсия повредила майнинг-ферму: -" + damage + "% прочности. Доход остановлен, используй FARM REPAIR."), false);
            return;
        }
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
            + " §7| §bФерма +" + (p.farmLevel > 0 && p.farmHealth > 0 ? 5 + p.farmLevel * 3 : 0) + "/мин" + " §7| §fЩиток " + p.breakerCharges + " §7| Охрана " + p.guardCharges + " §7| §bФерма " + p.farmHealth + "% §7| §cРозыск " + p.wantedLevel
            + " §7| §aЦель 10k: " + Math.min(100, p.credits / 100) + "%"
            + (p.policeStakeout ? " §c| ПАТРУЛЬ У ДОМА" : "")), true);
    }

    public boolean isPowerOut(ServerPlayerEntity player) {
        return profile(player).powerUntilTick > player.getServer().getOverworld().getTime();
    }


    private void playerBreakFarm(ServerPlayerEntity player, PlayerProfile p) {
        if (!p.homeBuilt) return;
        ServerWorld world = player.getServer().getOverworld();
        BlockPos machine = new BlockPos(p.homeX + 2, p.homeY + 1, p.homeZ - 2);
        world.setBlockState(machine, Blocks.AIR.getDefaultState());
        world.setBlockState(machine.up(), Blocks.AIR.getDefaultState());
        world.setBlockState(machine.up(2), Blocks.AIR.getDefaultState());
    }

    private void refreshHomeDisplays(ServerPlayerEntity player, PlayerProfile p) {
        if (!p.homeBuilt) return;
        ServerWorld w = player.getServer().getOverworld();
        placeUpgradeDisplay(w, p, -2, -2, p.firewall, Blocks.IRON_BARS);
        placeUpgradeDisplay(w, p, 0, -2, p.automation, Blocks.REDSTONE_LAMP);
        placeUpgradeDisplay(w, p, 2, 0, p.stealth, Blocks.TINTED_GLASS);
        placeUpgradeDisplay(w, p, -2, 0, p.extractor, Blocks.HOPPER);
        placeUpgradeDisplay(w, p, 0, 0, p.cpu, Blocks.NOTE_BLOCK);
        placeUpgradeDisplay(w, p, 2, 1, p.miningRig, Blocks.OBSERVER);
        placeUpgradeDisplay(w, p, 0, 2, p.storage, Blocks.CHEST);
        placeUpgradeDisplay(w, p, -2, 2, p.vpnOwned ? (p.vpnActive ? 2 : 1) : 0, Blocks.ENDER_CHEST);
        placeUpgradeDisplay(w, p, 1, 2, p.breakerLevel, Blocks.LIGHTNING_ROD);
        placeUpgradeDisplay(w, p, -1, 2, p.guardLevel, Blocks.IRON_BARS);
        buildFarm(player, p);
    }

    private void placeUpgradeDisplay(ServerWorld world, PlayerProfile p, int dx, int dz, int level, net.minecraft.block.Block block) {
        BlockPos pos = new BlockPos(p.homeX + dx, p.homeY + 1, p.homeZ + dz);
        if (level <= 0) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            world.setBlockState(pos.up(), Blocks.AIR.getDefaultState());
            return;
        }
        world.setBlockState(pos, block.getDefaultState());
        world.setBlockState(pos.up(), level >= 3 ? block.getDefaultState() : Blocks.AIR.getDefaultState());
    }

    private void buildFarm(ServerPlayerEntity player, PlayerProfile p) {
        if (!p.homeBuilt || p.farmLevel == 0 || p.farmHealth <= 0) return;
        ServerWorld world = player.getServer().getOverworld();
        BlockPos machine = new BlockPos(p.homeX + 2, p.homeY + 1, p.homeZ - 2);
        world.setBlockState(machine, Blocks.IRON_BLOCK.getDefaultState());
        world.setBlockState(machine.up(), Blocks.BLAST_FURNACE.getDefaultState());
        world.setBlockState(machine.up(2), Blocks.REDSTONE_LAMP.getDefaultState());
    }

    public String farm(ServerPlayerEntity player, String action) {
        PlayerProfile p = profile(player);
        switch (action.toLowerCase()) {
            case "buy" -> {
                if (p.farmLevel > 0) return "§eМайнинг-ферма уже установлена. Используй FARM UPGRADE.";
                if (p.credits < 120) return "§cНужно 120 кредитов для покупки фермы.";
                p.credits -= 120; p.farmLevel = 1; p.farmHealth = 100; refreshHomeDisplays(player, p); markDirty();
                return "§aФЕРМА УСТАНОВЛЕНА | Уровень 1 | Прочность 100% | Доход +8 кредитов/мин.";
            }
            case "upgrade" -> {
                if (p.farmLevel == 0) return "§cСначала купи ферму: FARM BUY.";
                if (p.farmLevel >= 5) return "§eФерма уже максимального 5 уровня.";
                int cost = 100 + p.farmLevel * 90;
                if (p.credits < cost) return "§cУлучшение фермы стоит " + cost + " кредитов.";
                p.credits -= cost; p.farmLevel++; p.farmHealth = Math.min(100, p.farmHealth + 20); refreshHomeDisplays(player, p); markDirty();
                return "§aФЕРМА УЛУЧШЕНА | Уровень " + p.farmLevel + "/5 | Доход +" + (5 + p.farmLevel * 3) + "/мин.";
            }
            case "repair" -> {
                if (p.farmLevel == 0) return "§cФермы нет. Используй FARM BUY.";
                if (p.farmHealth >= 100) return "§aФерма полностью исправна.";
                int cost = Math.max(10, (100 - p.farmHealth) / 2);
                if (p.credits < cost) return "§cРемонт фермы стоит " + cost + " кредитов.";
                p.credits -= cost; p.farmHealth = 100; refreshHomeDisplays(player, p); markDirty();
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
                p.credits -= 90; p.vpnOwned = true; refreshHomeDisplays(player, p); markDirty();
                return "§aVPN-МОДУЛЬ КУПЛЕН. Включи его: VPN ON.";
            }
            case "on" -> {
                if (!p.vpnOwned) return "§cСначала купи VPN: VPN BUY.";
                p.vpnActive = true; p.wantedLevel = Math.max(0, p.wantedLevel - 1); refreshHomeDisplays(player, p); markDirty();
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

    public String roundStatus(ServerPlayerEntity player) {
        PlayerProfile p = profile(player);
        if (p.eliminated) return "§cРАУНД ОКОНЧЕН | Ты задержан полицией и выбыл.";
        if (p.roundWon) return "§aПОБЕДА | Цель 10 000 кредитов достигнута.";
        return "§bЦЕЛЬ РАУНДА: 10 000 КРЕДИТОВ\n§fБаланс: " + p.credits + "/10 000"
            + " §7| Осталось: " + Math.max(0, 10000 - p.credits)
            + "\n§fПрогресс: " + Math.min(100, p.credits / 100) + "%"
            + "\n§fПолиция: " + (p.policeStakeout ? "§cпатруль ждёт у дома — не возвращайся" : "§aактивное наблюдение: " + p.wantedLevel + "/5")
            + "\n§7Достигни цели раньше, чем полиция установит твой адрес.";
    }

    public String policeStatus(ServerPlayerEntity player) {
        PlayerProfile p = profile(player);
        return "§cПОЛИЦИЯ ARENA | Розыск " + p.wantedLevel + "/5 | "
            + (p.wantedLevel >= 4 ? "активное расследование и патруль" : p.wantedLevel >= 2 ? "анализ журналов и подозрение" : p.wantedLevel == 1 ? "наблюдение за сетевыми событиями" : "активного дела нет")
            + " | VPN " + (p.vpnActive ? "маскирует адрес, но не стирает события" : "адрес виден")
            + " | Сетевая нагрузка " + p.networkHeat + "/10 | Последнее событие: " + p.lastNetworkEvent
            + " | Последняя цель: " + (p.networkTarget.isBlank() ? "нет" : p.networkTarget);
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
                if (d.guardCharges > 0) { d.guardCharges--; yield "§aОХРАНА ЗАЩИТИЛА ФЕРМУ | Потрачен один заряд охраны."; }
                int damage = Math.min(45, d.farmHealth); d.farmHealth -= damage;
                if (d.farmHealth == 0) playerBreakFarm(target, d);
                yield "§cСАБОТАЖ ФЕРМЫ | -" + damage + "% прочности | Доход остановлен до ремонта.";
            }
            case "power" -> {
                if (d.breakerCharges > 0) { d.breakerCharges--; yield "§aЭЛЕКТРОЩИТОК ВЫДЕРЖАЛ | Потрачен один заряд защиты."; }
                d.powerUntilTick = now + 1200; yield "§cОТКЛЮЧЕНИЕ ПИТАНИЯ | Станция цели отключена на 60 секунд.";
            }
            case "supplies" -> {
                int stolen = Math.min(d.credits, 20 + a.extractor * 5);
                d.credits -= stolen; a.credits += stolen;
                yield "§eКРАЖА ЗАПАСОВ | Перенесено " + stolen + " игровых кредитов.";
            }
            case "alarm" -> { d.alertLevel = Math.min(5, d.alertLevel + 2); yield "§6ЛОЖНАЯ ТРЕВОГА | Сигнализация цели перегружена."; }
            case "lock" -> { d.powerUntilTick = now + 600; yield "§cБЛОКИРОВКА СТАНЦИИ | Терминал цели заблокирован на 30 секунд."; }
            case "data" -> {
                int stolen = Math.min(d.dataShards, 2 + a.extractor);
                d.dataShards -= stolen; a.dataShards += stolen;
                yield "§bКРАЖА ДАННЫХ | Перенесено " + stolen + " осколков.";
            }
            case "falsecall" -> { d.wantedLevel = Math.min(5, d.wantedLevel + 2); yield "§cЛОЖНЫЙ ВЫЗОВ ПОЛИЦИИ | Уровень розыска цели повышен."; }
            case "raiders" -> {
                BlockPos home = new BlockPos(d.homeX, d.homeY + 1, d.homeZ);
                for (int i = 0; i < 2; i++) {
                    var raider = EntityType.PILLAGER.spawn(target.getServerWorld(), home.add(i == 0 ? -2 : 2, 0, 1), SpawnReason.EVENT);
                    if (raider != null) { raider.setPersistent(); raider.setTarget(target); }
                }
                yield "§cПОДСТАВНОЙ НАЛЁТ | В дом цели проникли два рейдера.";
            }
            default -> "§cВарианты: FARM, POWER, SUPPLIES, DATA, ALARM, LOCK, FALSECALL, RAIDERS.";
        };
        markDirty();
        if (traced) attacker.sendMessage(Text.literal("§c[ПОЛИЦИЯ] §fТвой виртуальный след обнаружен. Розыск +" + 1 + "."), false);
        target.sendMessage(Text.literal("§c[ДОМ] §fОбнаружена попытка саботажа: " + action.toUpperCase()), false);
        return result + (traced ? " §c| След обнаружен, розыск " + a.wantedLevel + "/5." : " §a| След не обнаружен.");
    }

    private void recordNetworkActivity(ServerPlayerEntity player, PlayerProfile profile, String action, String targetName) {
        int intensity = switch (action) { case "DISCOVER" -> 1; case "SCAN" -> 2; case "ENUM" -> 2; case "EXPLOIT" -> 3; default -> 1; };
        profile.networkHeat = Math.min(10, profile.networkHeat + Math.max(1, intensity - (profile.vpnActive ? 1 : 0)));
        profile.lastNetworkActivityTick = player.getServer().getOverworld().getTime();
        profile.lastNetworkEvent = action + " / " + targetName;
        markDirty();
    }

    public String network(ServerPlayerEntity attacker, String instruction) {
        PlayerProfile a = profile(attacker);
        String[] parts = instruction.trim().split("\\s+", 3);
        if (parts.length < 2) return "§eNET DISCOVER <игрок> | SCAN <игрок> | ENUM <игрок> | EXPLOIT <игрок> <FARM|POWER|DATA|SUPPLIES|ALARM|LOCK|FALSECALL|RAIDERS>";
        String action = parts[0].toUpperCase();
        String targetName = parts[1];
        ServerPlayerEntity target = attacker.getServer().getPlayerManager().getPlayerList().stream().filter(p -> p.getGameProfile().getName().equalsIgnoreCase(targetName)).findFirst().orElse(null);
        if (target == null) return "§cNET/NO_HOST | Игрок должен быть онлайн в этом мире.";
        if (target.getUuid().equals(attacker.getUuid())) return "§cNET/SELF | Нельзя выбирать собственную сеть.";
        PlayerProfile d = profile(target);
        switch (action) {
            case "DISCOVER" -> {
                recordNetworkActivity(attacker, a, action, targetName);
                a.networkTarget = target.getGameProfile().getName(); a.networkStage = 1;
                String address = d.vpnActive ? "203.0.113." + (d.virtualIp % 200 + 20) + " (VPN-узел)" : "198.51.100." + (d.virtualIp % 200 + 20);
                boolean traced = !a.vpnActive && ThreadLocalRandom.current().nextInt(100) < 22;
                if (traced) a.wantedLevel = Math.min(5, a.wantedLevel + 1);
                markDirty();
                return "§bARENA DISCOVERY §7| §f" + targetName + "\n§fВиртуальный адрес: " + address + "\n§aУзел найден. Далее: NET SCAN " + targetName + (traced ? "\n§cПолиция заметила поисковый запрос." : "");
            }
            case "SCAN" -> {
                recordNetworkActivity(attacker, a, action, targetName);
                if (a.networkStage < 1 || !a.networkTarget.equalsIgnoreCase(targetName)) return "§cNET/ORDER | Сначала DISCOVER для этой цели.";
                a.networkStage = 2; int fw = d.firewall;
                boolean traced = !a.vpnActive && ThreadLocalRandom.current().nextInt(100) < 32;
                if (traced) a.wantedLevel = Math.min(5, a.wantedLevel + 1);
                markDirty();
                return "§bPORT SCAN REPORT §7| §f" + targetName + "\n§e021/WEB-GATE §7" + (fw >= 4 ? "FILTERED" : "OPEN") + "\n§e044/DATA-RELAY §7" + (fw >= 2 ? "FILTERED" : "OPEN") + "\n§e071/ADMIN-CORE §7" + (fw >= 3 ? "FILTERED" : "OPEN") + "\n§7FIREWALL " + fw + "/5\n§aДалее: NET ENUM " + targetName + (traced ? "\n§cСканирование попало в журнал полиции." : "");
            }
            case "ENUM" -> {
                recordNetworkActivity(attacker, a, action, targetName);
                if (a.networkStage < 2 || !a.networkTarget.equalsIgnoreCase(targetName)) return "§cNET/ORDER | Сначала SCAN для этой цели.";
                a.networkStage = 3; markDirty();
                return "§bSERVICE ENUMERATION §7| §f" + targetName + "\n§f021 WEB-GATE: публичный портал\n§f044 DATA-RELAY: хранилище ресурсов\n§f071 ADMIN-CORE: управление домом\n§7Сервисы вымышлены; внешних подключений нет.\n§aДалее: NET EXPLOIT " + targetName + " FARM";
            }
            case "EXPLOIT" -> {
                recordNetworkActivity(attacker, a, action, targetName);
                if (parts.length < 3) return "§eУкажи игровой модуль: FARM, POWER, DATA, SUPPLIES, ALARM, LOCK, FALSECALL или RAIDERS.";
                if (a.networkStage < 3 || !a.networkTarget.equalsIgnoreCase(targetName)) return "§cNET/ORDER | Сначала DISCOVER, SCAN и ENUM.";
                long now = attacker.getServer().getOverworld().getTime();
                if (now - a.lastNetworkActionTick < 1200) return "§eNET/COOLDOWN | Операция пока перезаряжается.";
                a.lastNetworkActionTick = now; a.networkStage = 0;
                String result = sabotage(attacker, target, parts[2].toLowerCase());
                markDirty();
                return "§bEXPLOIT SIMULATION §7| §f" + targetName + " / " + parts[2].toUpperCase() + "\n" + result;
            }
            default -> { return "§eNET DISCOVER | SCAN | ENUM | EXPLOIT"; }
        }
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
        if (!a.vpnActive && ThreadLocalRandom.current().nextInt(100) < Math.max(10, 55 - a.stealth * 7)) a.wantedLevel = Math.min(5, a.wantedLevel + 1);
        int chance = Math.max(8, Math.min(92, 60 + a.automation * 5 + a.stealth * 4 + a.extractor * 3 - d.firewall * 12 + (a.vpnActive ? 8 : 0)));
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
        if (ThreadLocalRandom.current().nextInt(100) >= chance) { p.alertLevel = Math.min(5, p.alertLevel + 1); if (!p.vpnActive) p.wantedLevel = Math.min(5, p.wantedLevel + 1); markDirty(); return "§cACCESS REJECTED | ALERT " + p.alertLevel + "/5. Upgrade STEALTH or AUTOM."; }
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
        refreshHomeDisplays(player, p);
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
            e.putInt("farm_level", p.farmLevel); e.putInt("farm_health", p.farmHealth); e.putBoolean("vpn_owned", p.vpnOwned); e.putBoolean("vpn_active", p.vpnActive); e.putInt("wanted", p.wantedLevel); e.putInt("virtual_ip", p.virtualIp); e.putLong("last_police_check", p.lastPoliceCheckTick);
            e.putString("network_target", p.networkTarget); e.putInt("network_stage", p.networkStage); e.putLong("last_network_action", p.lastNetworkActionTick);
            e.putInt("network_heat", p.networkHeat); e.putLong("last_network_activity", p.lastNetworkActivityTick); e.putString("last_network_event", p.lastNetworkEvent);
            e.putBoolean("police_stakeout", p.policeStakeout); e.putBoolean("eliminated", p.eliminated); e.putBoolean("round_won", p.roundWon);
            e.putInt("home_index", p.homeIndex); e.putInt("home_x", p.homeX); e.putInt("home_y", p.homeY); e.putInt("home_z", p.homeZ); e.putBoolean("home_built", p.homeBuilt);
            NbtList nodes = new NbtList();
            p.discoveredNodes.forEach(v -> { NbtCompound n = new NbtCompound(); n.putString("id", v); nodes.add(n); });
            e.put("nodes", nodes);
            NbtList jobs = new NbtList();
            p.completedContracts.forEach(v -> { NbtCompound c = new NbtCompound(); c.putString("id", v); jobs.add(c); });
            e.put("jobs", jobs);
            entries.add(e);
        });
        nbt.putInt("schema_version", 6);
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
            p.farmLevel = clamp(e.getInt("farm_level"), 0, 5); p.farmHealth = clamp(e.getInt("farm_health"), 0, 100); p.vpnOwned = e.getBoolean("vpn_owned"); p.vpnActive = e.getBoolean("vpn_active"); p.wantedLevel = clamp(e.getInt("wanted"), 0, 5); p.virtualIp = e.contains("virtual_ip") ? clamp(e.getInt("virtual_ip"), 20, 219) : Math.floorMod(e.getUuid("uuid").hashCode(), 200) + 20; p.lastPoliceCheckTick = e.getLong("last_police_check");
            p.networkTarget = e.getString("network_target"); p.networkStage = clamp(e.getInt("network_stage"), 0, 3); p.lastNetworkActionTick = e.getLong("last_network_action");
            p.networkHeat = clamp(e.getInt("network_heat"), 0, 10); p.lastNetworkActivityTick = e.getLong("last_network_activity");
            p.lastNetworkEvent = e.contains("last_network_event") ? e.getString("last_network_event") : "нет";
            p.policeStakeout = e.getBoolean("police_stakeout"); p.eliminated = e.getBoolean("eliminated"); p.roundWon = e.getBoolean("round_won");
            p.homeIndex = e.contains("home_index") ? e.getInt("home_index") : -1; p.homeX = e.getInt("home_x"); p.homeY = e.getInt("home_y"); p.homeZ = e.getInt("home_z"); p.homeBuilt = e.getBoolean("home_built");
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
        private int farmLevel = 0, farmHealth = 0, wantedLevel = 0, virtualIp = 42, networkStage = 0;
        private String networkTarget = "", lastNetworkEvent = "нет";
        private int networkHeat = 0;
        private long lastNetworkActivityTick = 0;
        private boolean vpnOwned = false, vpnActive = false;
        private boolean policeStakeout = false, eliminated = false, roundWon = false;
        private long lastPoliceCheckTick = 0, lastNetworkActionTick = -100000;
        private long lastRaidTick = -100000, lastSiteRaidTick = -100000, lastMineTick = -100000, lastPassiveIncomeTick = -1, lastIncidentTick = 0, lastIncidentCheckTick = 0, powerUntilTick = 0;
        private final Set<String> discoveredNodes = new HashSet<>();
        private final Set<String> completedContracts = new HashSet<>();
        private PlayerProfile(String name) { this.name = name; }
    }
}
