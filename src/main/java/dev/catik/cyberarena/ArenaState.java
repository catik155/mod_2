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
import java.util.Locale;
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
            if (p.homeVersion < 2) { buildStarterHouse(world, p.homeX, p.homeY, p.homeZ); p.homeVersion = 2; markDirty(); }
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
        int centerX = worldSpawn.getX() + (gridX - 3) * 24;
        int centerZ = worldSpawn.getZ() + (gridZ - 3) * 24;
        int floorY = findHomeFloor(world, centerX, centerZ);
        centerX = homeCandidateX;
        centerZ = homeCandidateZ;
        buildStarterHouse(world, centerX, floorY, centerZ);
        p.homeX = centerX; p.homeY = floorY; p.homeZ = centerZ; p.homeBuilt = true; p.homeVersion = 2;
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
        // Дом 11x11: просторный зал, высокие стены, панорамные окна и выразительная крыша.
        for (int dx = -5; dx <= 5; dx++) for (int dz = -5; dz <= 5; dz++) {
            world.setBlockState(new BlockPos(x + dx, y, z + dz), Blocks.DEEPSLATE_TILES.getDefaultState());
            for (int dy = 1; dy <= 4; dy++) {
                boolean wall = Math.abs(dx) == 5 || Math.abs(dz) == 5;
                BlockPos pos = new BlockPos(x + dx, y + dy, z + dz);
                if (!wall) world.setBlockState(pos, Blocks.AIR.getDefaultState());
                else if (dz == 5 && dx == 0 && dy <= 2) world.setBlockState(pos, Blocks.AIR.getDefaultState());
                else if (dy == 2 && ((Math.abs(dx) == 5 && dz % 2 == 0) || (Math.abs(dz) == 5 && dx % 2 == 0))) world.setBlockState(pos, Blocks.GLASS_PANE.getDefaultState());
                else world.setBlockState(pos, Blocks.SPRUCE_PLANKS.getDefaultState());
            }
            world.setBlockState(new BlockPos(x + dx, y + 5, z + dz), Blocks.DARK_OAK_PLANKS.getDefaultState());
        }
        // Подсветка и входная площадка.
        world.setBlockState(new BlockPos(x, y + 3, z), Blocks.LANTERN.getDefaultState());
        world.setBlockState(new BlockPos(x, y + 1, z + 5), Blocks.SPRUCE_DOOR.getDefaultState());
        world.setBlockState(new BlockPos(x - 1, y, z + 6), Blocks.STONE_BRICKS.getDefaultState());
        world.setBlockState(new BlockPos(x, y, z + 6), Blocks.STONE_BRICKS.getDefaultState());
        world.setBlockState(new BlockPos(x + 1, y, z + 6), Blocks.STONE_BRICKS.getDefaultState());
        world.setBlockState(new BlockPos(x + 4, y + 1, z + 3), Blocks.CRAFTING_TABLE.getDefaultState());
        world.setBlockState(new BlockPos(x - 4, y + 1, z + 3), Blocks.BARREL.getDefaultState());
        world.setBlockState(new BlockPos(x, y + 1, z - 4), Blocks.BOOKSHELF.getDefaultState());
    }

    public static void tickServer(MinecraftServer server) {
        ArenaState state = get(server);
        long now = server.getOverworld().getTime();
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            PlayerProfile p = state.profile(player);
            if (p.lastPassiveIncomeTick < 0) p.lastPassiveIncomeTick = now;
            if (p.eliminated || p.roundWon) { if (player.interactionManager.getGameMode() != GameMode.SPECTATOR) player.changeGameMode(GameMode.SPECTATOR); continue; }
            if (state.isPowerOut(player)) {
                // При отключённом питании не начисляем майнинг и ферму, а простой не компенсируем.
                p.lastPassiveIncomeTick = now;
            } else if (now - p.lastPassiveIncomeTick >= 1200) {
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
            // Случайные нападения на дом отключены: вторжения возможны только после PvP-атаки или расследования полиции.
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
                        // При обыске полиция повреждает инфраструктуру, но не создаёт случайные PvP-налёты.
                        p.stationHealth = Math.max(0, p.stationHealth - 25);
                        if (p.stationHealth == 0) state.breakWorkstation(player, p);
                        if (p.farmLevel > 0 && p.farmHealth > 0) {
                            p.farmHealth = Math.max(0, p.farmHealth - 20);
                            if (p.farmHealth == 0) state.playerBreakFarm(player, p);
                        }
                        state.refreshHomeDisplays(player, p);
                        player.sendMessage(Text.literal("§c[ПОЛИЦИЯ ARENA] §fТвой адрес установлен. Патруль занял позицию у дома и повредил инфраструктуру: станция -" + (p.stationHealth == 0 ? "25% (разрушена)" : "25%") + ", ферма " + p.farmHealth + "%. Если войдёшь внутрь — задержание и выбывание из раунда."), false);
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
        return p.homeBuilt && player.getBlockX() >= p.homeX - 5 && player.getBlockX() <= p.homeX + 5
            && player.getBlockZ() >= p.homeZ - 5 && player.getBlockZ() <= p.homeZ + 5
            && player.getY() >= p.homeY && player.getY() <= p.homeY + 7;
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
            + " §7| §eMINER +" + (isPowerOut(player) ? 0 : 2 + p.miningRig * 2) + "/мин §7| " + power
            + " §7| §bФерма +" + (isPowerOut(player) ? 0 : p.farmLevel > 0 && p.farmHealth > 0 ? 5 + p.farmLevel * 3 : 0) + "/мин" + " §7| §fЩиток " + p.breakerCharges + " §7| Охрана " + p.guardCharges + " §7| §bФерма " + p.farmHealth + "% §7| §cРозыск " + p.wantedLevel
            + " §7| §aЦель 10k: " + Math.min(100, p.credits / 100) + "%"
            + (p.policeStakeout ? " §c| ПАТРУЛЬ У ДОМА" : "")), true);
    }

    public boolean isPowerOut(ServerPlayerEntity player) {
        return profile(player).powerUntilTick > player.getServer().getOverworld().getTime();
    }


    private void playerBreakFarm(ServerPlayerEntity player, PlayerProfile p) {
        if (!p.homeBuilt) return;
        ServerWorld world = player.getServer().getOverworld();
        BlockPos machine = new BlockPos(p.homeX + 3, p.homeY + 1, p.homeZ - 3);
        world.setBlockState(machine, Blocks.AIR.getDefaultState());
        world.setBlockState(machine.up(), Blocks.AIR.getDefaultState());
        world.setBlockState(machine.up(2), Blocks.AIR.getDefaultState());
    }

    private void refreshHomeDisplays(ServerPlayerEntity player, PlayerProfile p) {
        if (!p.homeBuilt) return;
        ServerWorld w = player.getServer().getOverworld();
        placeUpgradeDisplay(w, p, -4, -1, p.firewall, Blocks.IRON_BARS);
        placeUpgradeDisplay(w, p, -2, -1, p.automation, Blocks.REDSTONE_LAMP);
        placeUpgradeDisplay(w, p, 0, -1, p.stealth, Blocks.TINTED_GLASS);
        placeUpgradeDisplay(w, p, 2, -1, p.extractor, Blocks.HOPPER);
        placeUpgradeDisplay(w, p, 4, -1, p.cpu, Blocks.NOTE_BLOCK);
        placeUpgradeDisplay(w, p, -4, 2, p.miningRig, Blocks.OBSERVER);
        placeUpgradeDisplay(w, p, -2, 2, p.storage, Blocks.CHEST);
        placeUpgradeDisplay(w, p, 0, 2, p.vpnOwned ? (p.vpnActive ? 2 : 1) : 0, Blocks.ENDER_CHEST);
        placeUpgradeDisplay(w, p, 2, 2, p.breakerLevel, Blocks.LIGHTNING_ROD);
        placeUpgradeDisplay(w, p, 4, 2, p.guardLevel, Blocks.IRON_BARS);
        buildWorkstation(player, p);
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

    private void buildWorkstation(ServerPlayerEntity player, PlayerProfile p) {
        if (!p.homeBuilt || p.stationHealth <= 0) return;
        if (isPowerOut(player)) { breakWorkstation(player, p); return; }
        ServerWorld world = player.getServer().getOverworld();
        BlockPos desk = new BlockPos(p.homeX - 3, p.homeY + 1, p.homeZ - 3);
        world.setBlockState(desk, Blocks.OBSIDIAN.getDefaultState());
        world.setBlockState(desk.up(), Blocks.OBSERVER.getDefaultState());
        world.setBlockState(desk.add(1, 0, 0), Blocks.IRON_BLOCK.getDefaultState());
        world.setBlockState(desk.add(1, 1, 0), Blocks.REDSTONE_LAMP.getDefaultState());
    }

    private void breakWorkstation(ServerPlayerEntity player, PlayerProfile p) {
        if (!p.homeBuilt) return;
        ServerWorld world = player.getServer().getOverworld();
        BlockPos desk = new BlockPos(p.homeX - 3, p.homeY + 1, p.homeZ - 3);
        world.setBlockState(desk, Blocks.AIR.getDefaultState());
        world.setBlockState(desk.up(), Blocks.AIR.getDefaultState());
        world.setBlockState(desk.add(1, 0, 0), Blocks.AIR.getDefaultState());
        world.setBlockState(desk.add(1, 1, 0), Blocks.AIR.getDefaultState());
    }

    private void buildFarm(ServerPlayerEntity player, PlayerProfile p) {
        if (!p.homeBuilt || p.farmLevel == 0 || p.farmHealth <= 0) return;
        ServerWorld world = player.getServer().getOverworld();
        BlockPos machine = new BlockPos(p.homeX + 3, p.homeY + 1, p.homeZ - 3);
        world.setBlockState(machine, Blocks.IRON_BLOCK.getDefaultState());
        world.setBlockState(machine.up(), Blocks.BLAST_FURNACE.getDefaultState());
        world.setBlockState(machine.up(2), p.farmLevel >= 3 ? Blocks.SEA_LANTERN.getDefaultState() : Blocks.REDSTONE_LAMP.getDefaultState());
        if (p.farmLevel >= 4) world.setBlockState(machine.add(1, 0, 0), Blocks.COPPER_BLOCK.getDefaultState());
        if (p.farmLevel >= 5) world.setBlockState(machine.add(-1, 0, 0), Blocks.IRON_BARS.getDefaultState());
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

    public String counterAttack(ServerPlayerEntity defender) {
        PlayerProfile p = profile(defender);
        long now = defender.getServer().getOverworld().getTime();
        if (p.counterTarget.isBlank() || now > p.counterUntilTick) {
            p.counterTarget = ""; p.counterBonus = 0; markDirty();
            return "§eОкно контратаки закрыто. Оно появляется только после победы в консольном противостоянии.";
        }
        String targetName = p.counterTarget;
        ServerPlayerEntity attacker = defender.getServer().getPlayerManager().getPlayerList().stream()
            .filter(other -> other.getGameProfile().getName().equalsIgnoreCase(targetName)).findFirst().orElse(null);
        int bonus = p.counterBonus;
        p.counterTarget = ""; p.counterUntilTick = 0; p.counterBonus = 0;
        markDirty();
        if (attacker == null) return "§eПротивник уже не в сети. Контратака отменена.";
        PlayerProfile a = profile(defender), d = profile(attacker);
        int attack = attackPower(a) + bonus, defense = defensePower(d);
        int chance = Math.max(5, Math.min(95, 50 + (attack - defense) * 5));
        boolean success = ThreadLocalRandom.current().nextInt(100) < chance;
        if (!success) return "§cКОНТРАТАКА НЕ ПРОШЛА §7| Атака " + attack + " / защита " + defense + ". Бонус +"+bonus+" потрачен.";
        int stolen = Math.min(d.credits, Math.max(1, d.credits / 5));
        d.credits -= stolen; a.credits += stolen; a.reputation += 1;
        markDirty();
        return "§6КОНТРАТАКА УДАЛАСЬ §7| Бонус атаки +" + bonus + " §7| Перехвачено " + stolen + " кредитов.";
    }

    public String sabotage(ServerPlayerEntity attacker, ServerPlayerEntity target, String action) {
        if (attacker.getUuid().equals(target.getUuid())) return "§cНельзя саботировать собственный дом.";
        PlayerProfile a = profile(attacker), d = profile(target);
        long now = attacker.getServer().getOverworld().getTime();
        int attack = attackPower(a), defense = defensePower(d);
        int blockChance = Math.max(5, Math.min(95, 50 + (defense - attack) * 5));
        boolean blocked = ThreadLocalRandom.current().nextInt(100) < blockChance;
        boolean traced = !a.vpnActive && ThreadLocalRandom.current().nextInt(100) < Math.max(15, 65 - a.stealth * 8);
        if (traced) a.wantedLevel = Math.min(5, a.wantedLevel + 1);
        if (blocked) {
            d.alertLevel = Math.min(5, d.alertLevel + 1);
            markDirty();
            return "§aАВТОЗАЩИТА ОТРАЗИЛА АТАКУ §7| Блокировка " + blockChance + "%. Консольное противостояние не потребовалось."
                + (traced ? " §cТвой след обнаружен." : "");
        }
        markDirty();
        return startDuel(attacker, target, action.toLowerCase(), now)
            + (traced ? " §cТвой след обнаружен, розыск " + a.wantedLevel + "/5." : "");
    }

    private void recordNetworkActivity(ServerPlayerEntity player, PlayerProfile profile, String action, String targetName) {
        long now = player.getServer().getOverworld().getTime();
        profile.networkHeat = Math.min(10, profile.networkHeat + (action.equalsIgnoreCase("EXPLOIT") ? 2 : 1));
        profile.lastNetworkActivityTick = now;
        profile.lastNetworkEvent = action.toUpperCase() + " / " + targetName;
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
                a.lastNetworkActionTick = now; a.networkStage = 0;
                String result = sabotage(attacker, target, parts[2].toLowerCase());
                markDirty();
                return "§bEXPLOIT SIMULATION §7| §f" + targetName + " / " + parts[2].toUpperCase() + "\n" + result;
            }
            default -> { return "§eNET DISCOVER | SCAN | ENUM | EXPLOIT"; }
        }
    }


    public String station(ServerPlayerEntity player, String action) {
        PlayerProfile p = profile(player);
        String[] args = action.trim().toLowerCase().split("\\s+");
        String op = args.length == 0 ? "status" : args[0];
        if (op.equals("repair")) {
            if (p.stationHealth >= 100) return "§aРабочая станция полностью исправна.";
            int cost = Math.max(20, (100 - p.stationHealth) * 2);
            if (p.credits < cost) return "§cРемонт станции стоит " + cost + " кредитов.";
            p.credits -= cost; p.stationHealth = 100; refreshHomeDisplays(player, p); markDirty();
            return "§aРАБОЧАЯ СТАНЦИЯ ВОССТАНОВЛЕНА | 100% | -" + cost + " кредитов.";
        }
        if (op.equals("remote")) {
            if (p.remoteStationLevel > 0) return "§eУдалённый терминал уже установлен.";
            if (p.credits < 450) return "§cУдалённый терминал стоит 450 кредитов.";
            p.credits -= 450; p.remoteStationLevel = 1;
            ServerWorld world = player.getServer().getOverworld();
            BlockPos antenna = new BlockPos(p.homeX, p.homeY + 6, p.homeZ);
            world.setBlockState(antenna, Blocks.LIGHTNING_ROD.getDefaultState());
            world.setBlockState(antenna.down(), Blocks.IRON_BLOCK.getDefaultState());
            markDirty();
            return "§aУДАЛЁННЫЙ ТЕРМИНАЛ УСТАНОВЛЕН | Теперь консоль доступна вне дома и при разрушенной основной станции.";
        }
        return "§bСТАНЦИЯ | Прочность " + p.stationHealth + "% | " + (p.stationHealth > 0 ? "основной терминал доступен" : "§cосновной терминал разрушен")
            + " | Удалённый терминал: " + (p.remoteStationLevel > 0 ? "§aустановлен" : "§eне куплен")
            + "\\n§fSTATION REPAIR — восстановить | STATION REMOTE — купить удалённый терминал за 450 кредитов.";
    }

    public boolean canUseWorkstation(ServerPlayerEntity player) {
        PlayerProfile p = profile(player);
        if (p.remoteStationLevel > 0) return true;
        if (p.stationHealth <= 0 || isPowerOut(player) || !p.homeBuilt) return false;
        return Math.abs(player.getBlockX() - p.homeX) <= 5 && Math.abs(player.getBlockZ() - p.homeZ) <= 5
            && player.getY() >= p.homeY && player.getY() <= p.homeY + 7;
    }

    public String repairPower(ServerPlayerEntity player) {
        PlayerProfile p = profile(player);
        if (p.powerUntilTick <= player.getServer().getOverworld().getTime()) return "§aПитание уже работает.";
        if (p.credits < 15) return "§cДля аварийного ремонта нужно 15 кредитов.";
        p.credits -= 15; p.powerUntilTick = 0; refreshHomeDisplays(player, p); markDirty();
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

    private int attackPower(PlayerProfile p) {
        return p.automation * 2 + p.stealth + p.extractor + p.cpu * 2 + (p.vpnActive ? 1 : 0);
    }

    private int defensePower(PlayerProfile p) {
        return p.firewall * 2 + p.guardLevel * 2 + p.cpu * 2 + p.alertLevel;
    }

    private boolean defendAgainst(PlayerProfile attacker, PlayerProfile defender) {
        int attack = attackPower(attacker);
        int defense = defensePower(defender);
        int blockChance = Math.max(5, Math.min(95, 50 + (defense - attack) * 5));
        return ThreadLocalRandom.current().nextInt(100) < blockChance;
    }

    private void openCounterWindow(ServerPlayerEntity defender, ServerPlayerEntity attacker, PlayerProfile profile, long now) {
        profile.counterTarget = attacker.getGameProfile().getName();
        profile.counterUntilTick = now + 200; // 10 секунд
        defender.sendMessage(Text.literal("§a[ЗАЩИТА УСПЕШНА] §fАтака отражена! Защита противника временно ослаблена. У тебя 10 секунд на COUNTER."), false);
        attacker.sendMessage(Text.literal("§c[СИСТЕМА] §fТвоя атака отражена. Защитный контур перегружен, окно уязвимости открыто."), false);
    }

    private static void resetDefense(PlayerProfile p) {
        p.defenseCode = ""; p.defenseGame = ""; p.defenseRoute = ""; p.defenseGrid = "";
        p.defenseAttempts = 3; p.defenseRouteStep = 0; p.defenseX = 0; p.defenseY = 0;
    }

    private void clearDuel(PlayerProfile a, PlayerProfile d) {
        a.duelOpponent = ""; a.duelAction = ""; a.duelMove = ""; a.duelRole = ""; resetDefense(a);
        d.duelOpponent = ""; d.duelAction = ""; d.duelMove = ""; d.duelRole = ""; resetDefense(d);
    }

    private String startDuel(ServerPlayerEntity attacker, ServerPlayerEntity defender, String action, long now) {
        PlayerProfile a = profile(attacker), d = profile(defender);
        if (!a.duelOpponent.isBlank() || !d.duelOpponent.isBlank()) return "§eОдин из игроков уже участвует в сетевом противостоянии.";
        a.duelOpponent = defender.getGameProfile().getName(); a.duelAction = action; a.duelRole = "ATTACK"; a.duelMove = ""; a.duelUntilTick = now + 600;
        d.duelOpponent = attacker.getGameProfile().getName(); d.duelAction = action; d.duelRole = "DEFEND"; d.duelMove = ""; d.duelUntilTick = now + 600;
        startDefenseChallenge(d);
        attacker.sendMessage(Text.literal("§c[КОНСОЛЬНЫЙ ПОЕДИНОК] §fАвтозащита не удержала периметр. Выбери тактику: DUEL BURST / FEINT / OVERRIDE. У тебя 30 секунд."), false);
        defender.sendMessage(Text.literal("§6[ТРЕВОГА] §fАВТОЗАЩИТА ПРОБИТА! Открой NEX и введи DEFEND STATUS. Пройди консольное испытание KERNEL, ROUTE или GRID. Есть 3 попытки и 30 секунд."), false);
        markDirty();
        return "§cАВТОЗАЩИТА ПРОБИТА §7| Началось консольное противостояние с " + defender.getGameProfile().getName()
            + ". Выбери тактику: DUEL BURST, FEINT или OVERRIDE. Исход операции пока не определён.";
    }

    private static String randomCode(int length) {
        String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) code.append(alphabet.charAt(ThreadLocalRandom.current().nextInt(alphabet.length())));
        return code.toString();
    }

    private static void startDefenseChallenge(PlayerProfile p) {
        p.defenseAttempts = 3;
        p.defenseRouteStep = 0;
        p.defenseX = 0;
        p.defenseY = 0;
        p.defenseCode = "";
        p.defenseRoute = "";
        p.defenseGrid = "";
        int mode = ThreadLocalRandom.current().nextInt(3);
        if (mode == 0) {
            p.defenseGame = "KERNEL";
            p.defenseCode = "NX-" + randomCode(5);
        } else if (mode == 1) {
            p.defenseGame = "ROUTE";
            int[] links = new int[9];
            String[] path = {"RIGHT", "RIGHT", "DOWN", "DOWN"};
            for (int i = path.length - 1; i > 0; i--) {
                int j = ThreadLocalRandom.current().nextInt(i + 1);
                String swap = path[i]; path[i] = path[j]; path[j] = swap;
            }
            int x = 0, y = 0;
            for (String direction : path) {
                int nx = x + (direction.equals("RIGHT") ? 1 : 0);
                int ny = y + (direction.equals("DOWN") ? 1 : 0);
                addRouteLink(links, x, y, nx, ny);
                x = nx; y = ny;
            }
            for (int cy = 0; cy < 3; cy++) for (int cx = 0; cx < 3; cx++) {
                if (cx < 2 && ThreadLocalRandom.current().nextInt(100) < 22) addRouteLink(links, cx, cy, cx + 1, cy);
                if (cy < 2 && ThreadLocalRandom.current().nextInt(100) < 22) addRouteLink(links, cx, cy, cx, cy + 1);
            }
            StringBuilder encoded = new StringBuilder(9);
            for (int link : links) encoded.append((char)('0' + link));
            p.defenseRoute = encoded.toString();
        } else {
            p.defenseGame = "GRID";
            p.defenseGrid = scrambleDefenseGrid();
        }
    }

    private static void addRouteLink(int[] links, int x1, int y1, int x2, int y2) {
        int a = y1 * 3 + x1, b = y2 * 3 + x2;
        if (x2 == x1 + 1) { links[a] |= 2; links[b] |= 8; }
        else if (x2 == x1 - 1) { links[a] |= 8; links[b] |= 2; }
        else if (y2 == y1 + 1) { links[a] |= 4; links[b] |= 1; }
        else if (y2 == y1 - 1) { links[a] |= 1; links[b] |= 4; }
    }

    private static String scrambleDefenseGrid() {
        char[] board = "123456780".toCharArray();
        int blank = 8, previousBlank = -1;
        for (int step = 0; step < 12; step++) {
            int[] candidates = new int[4];
            int count = 0, x = blank % 3, y = blank / 3;
            if (x > 0 && blank - 1 != previousBlank) candidates[count++] = blank - 1;
            if (x < 2 && blank + 1 != previousBlank) candidates[count++] = blank + 1;
            if (y > 0 && blank - 3 != previousBlank) candidates[count++] = blank - 3;
            if (y < 2 && blank + 3 != previousBlank) candidates[count++] = blank + 3;
            int next = candidates[ThreadLocalRandom.current().nextInt(count)];
            board[blank] = board[next]; board[next] = '0';
            previousBlank = blank; blank = next;
        }
        return new String(board);
    }

    private String defenseStatus(PlayerProfile p, long now) {
        String header = "§6ЭКСТРЕННАЯ ЗАЩИТА §7| §f" + p.defenseGame
            + "\n§fПопытки: §c" + p.defenseAttempts + "/3 §7| Осталось: §e" + Math.max(0, (p.duelUntilTick - now + 19) / 20) + " сек.";
        return switch (p.defenseGame) {
            case "KERNEL" -> header + "\n§fВосстанови контрольную последовательность из журнала:"
                + "\n§e" + p.defenseCode + "\n§7Введи: DEFEND TYPE " + p.defenseCode;
            case "ROUTE" -> {
                StringBuilder map = new StringBuilder(header).append("\n§fПроведи сигнал от §aA1 §fдо §aC3§f. Текущий узел: §e")
                    .append((char)('A' + p.defenseX)).append(p.defenseY + 1).append("\n§7Схема связей (стрелки — доступные переходы):\n");
                for (int y = 0; y < 3; y++) {
                    for (int x = 0; x < 3; x++) {
                        int mask = p.defenseRoute.charAt(y * 3 + x) - '0';
                        String exits = ((mask & 1) != 0 ? "↑" : "·") + ((mask & 2) != 0 ? "→" : "·")
                            + ((mask & 4) != 0 ? "↓" : "·") + ((mask & 8) != 0 ? "←" : "·");
                        String node = (char)('A' + x) + Integer.toString(y + 1);
                        map.append(p.defenseX == x && p.defenseY == y ? "§a[" + node + " " + exits + "] " : "§f " + node + " " + exits + "  ");
                    }
                    map.append("\n");
                }
                yield map + "§7Команда: DEFEND ROUTE UP|RIGHT|DOWN|LEFT";
            }
            case "GRID" -> {
                StringBuilder map = new StringBuilder(header).append("\n§fСобери последовательность 1–8, пустая клетка обозначена точкой:\n");
                for (int i = 0; i < 9; i++) {
                    map.append(p.defenseGrid.charAt(i) == '0' ? "§e[·] " : "§f[" + p.defenseGrid.charAt(i) + "] ");
                    if (i % 3 == 2) map.append("\n");
                }
                yield map + "§7Перемести соседнюю плитку в пустую клетку: DEFEND MOVE B2 C2";
            }
            default -> header + "\n§cОшибка режима. Введи DEFEND HELP.";
        };
    }

    private String defenseMistake(ServerPlayerEntity player, PlayerProfile p, String reason) {
        p.defenseAttempts--;
        markDirty();
        if (p.defenseAttempts <= 0) {
            player.sendMessage(Text.literal("§c[ЗАЩИТА] §fПопытки закончились. Запускается аварийный сценарий."), false);
            return duelInternal(player, "DECOY", true);
        }
        return "§cОШИБКА: " + reason + " §7| Осталось попыток: " + p.defenseAttempts + "/3. Введи DEFEND STATUS.";
    }

    public String defend(ServerPlayerEntity player, String input) {
        PlayerProfile p = profile(player);
        long now = player.getServer().getOverworld().getTime();
        if (!p.duelRole.equals("DEFEND") || p.duelOpponent.isBlank()) return "§eНет активной задачи защиты.";
        if (now > p.duelUntilTick) return duelInternal(player, "DECOY", true);
        String[] parts = input.trim().split("\\s+");
        String action = parts.length == 0 ? "STATUS" : parts[0].toUpperCase(Locale.ROOT);
        if (action.equals("STATUS") || action.equals("HELP") || action.isBlank()) return defenseStatus(p, now);
        if (action.equals("TYPE") && p.defenseGame.equals("KERNEL")) {
            String answer = parts.length > 1 ? parts[1] : "";
            if (!answer.equals(p.defenseCode)) return defenseMistake(player, p, "Последовательность не совпала.");
            return completeDefense(player, p);
        }
        if (action.equals("ROUTE") && p.defenseGame.equals("ROUTE")) {
            if (parts.length != 2) return "§eФормат: DEFEND ROUTE UP|RIGHT|DOWN|LEFT";
            String direction = parts[1].toUpperCase(Locale.ROOT);
            int bit = switch (direction) { case "UP" -> 1; case "RIGHT" -> 2; case "DOWN" -> 4; case "LEFT" -> 8; default -> 0; };
            if (bit == 0) return "§eДопустимые направления: UP, RIGHT, DOWN, LEFT.";
            int index = p.defenseY * 3 + p.defenseX;
            int mask = p.defenseRoute.charAt(index) - '0';
            if ((mask & bit) == 0) return defenseMistake(player, p, "В этом узле нет такого соединения.");
            if (direction.equals("UP")) p.defenseY--;
            if (direction.equals("RIGHT")) p.defenseX++;
            if (direction.equals("DOWN")) p.defenseY++;
            if (direction.equals("LEFT")) p.defenseX--;
            markDirty();
            if (p.defenseX == 2 && p.defenseY == 2) return completeDefense(player, p);
            return "§aПЕРЕХОД ВЫПОЛНЕН §7| Узел " + (char)('A' + p.defenseX) + (p.defenseY + 1) + ". Следующий переход: DEFEND STATUS.";
        }
        if (action.equals("MOVE") && p.defenseGame.equals("GRID")) {
            if (parts.length != 3) return "§eФормат: DEFEND MOVE <плитка> <пустая-клетка>, например DEFEND MOVE B2 C2.";
            int from = gridIndex(parts[1]), to = gridIndex(parts[2]);
            if (from < 0 || to < 0 || p.defenseGrid.length() != 9 || p.defenseGrid.charAt(from) == '0'
                || p.defenseGrid.charAt(to) != '0' || Math.abs(from % 3 - to % 3) + Math.abs(from / 3 - to / 3) != 1)
                return defenseMistake(player, p, "Неверное перемещение. Передвигать можно только соседнюю плитку в пустую клетку.");
            char[] board = p.defenseGrid.toCharArray();
            board[to] = board[from]; board[from] = '0'; p.defenseGrid = new String(board);
            markDirty();
            if (p.defenseGrid.equals("123456780")) return completeDefense(player, p);
            return "§aПЕРЕМЕЩЕНИЕ ПРИНЯТО §7| Схема обновлена. Введи DEFEND STATUS.";
        }
        return switch (p.defenseGame) {
            case "KERNEL" -> "§eЭтот режим принимает только DEFEND TYPE <код>.";
            case "ROUTE" -> "§eЭтот режим принимает только DEFEND ROUTE <направление>.";
            case "GRID" -> "§eЭтот режим принимает только DEFEND MOVE <откуда> <куда>.";
            default -> "§eВведи DEFEND STATUS.";
        };
    }

    private static int gridIndex(String coordinate) {
        if (coordinate == null || coordinate.length() != 2) return -1;
        char column = Character.toUpperCase(coordinate.charAt(0));
        char row = coordinate.charAt(1);
        if (column < 'A' || column > 'C' || row < '1' || row > '3') return -1;
        return (row - '1') * 3 + (column - 'A');
    }

    private String completeDefense(ServerPlayerEntity player, PlayerProfile p) {
        p.defenseCode = "";
        player.sendMessage(Text.literal("§a[ЗАЩИТА] §fИспытание пройдено. Консоль восстановила контроль над контуром."), false);
        markDirty();
        return duelInternal(player, "PATCH", true);
    }

    public String duel(ServerPlayerEntity player, String move) {
        return duelInternal(player, move, false);
    }

    private String duelInternal(ServerPlayerEntity player, String move, boolean fromDefenseGame) {
        PlayerProfile p = profile(player);
        if (p.duelRole.equals("DEFEND") && !fromDefenseGame)
            return "§eЗащитнику нужно пройти мини-игру: DEFEND STATUS, затем DEFEND TYPE <код>.";
        long now = player.getServer().getOverworld().getTime();
        if (p.duelOpponent.isBlank()) return "§eАктивного противостояния нет.";
        if (now > p.duelUntilTick) {
            ServerPlayerEntity expiredOpponent = player.getServer().getPlayerManager().getPlayerList().stream()
                .filter(other -> other.getGameProfile().getName().equalsIgnoreCase(p.duelOpponent)).findFirst().orElse(null);
            if (expiredOpponent == null) {
                p.duelOpponent = ""; p.duelAction = ""; p.duelMove = ""; p.duelRole = ""; resetDefense(p); markDirty();
                return "§eВремя истекло, противник отключился. Противостояние отменено.";
            }
            ServerPlayerEntity expiredAttacker = p.duelRole.equals("ATTACK") ? player : expiredOpponent;
            ServerPlayerEntity expiredDefender = p.duelRole.equals("DEFEND") ? player : expiredOpponent;
            String expiredAction = profile(expiredAttacker).duelAction;
            String effect = applyCombatEffect(expiredAttacker, expiredDefender, expiredAction, now);
            clearDuel(profile(expiredAttacker), profile(expiredDefender));
            markDirty();
            String timeoutReport = "§cВРЕМЯ ЗАЩИТЫ ИСТЕКЛО §7| Атака прошла автоматически.\n" + effect;
            expiredAttacker.sendMessage(Text.literal(timeoutReport), false);
            expiredDefender.sendMessage(Text.literal(timeoutReport), false);
            return timeoutReport;
        }
        String choice = move.trim().toUpperCase();
        Set<String> allowed = p.duelRole.equals("ATTACK") ? Set.of("BURST", "FEINT", "OVERRIDE") : Set.of("PATCH", "DECOY", "TRACE");
        if (!allowed.contains(choice)) return p.duelRole.equals("ATTACK")
            ? "§eТактики атаки: BURST, FEINT, OVERRIDE." : "§eТактики защиты: PATCH, DECOY, TRACE.";
        p.duelMove = choice;
        ServerPlayerEntity opponent = player.getServer().getPlayerManager().getPlayerList().stream()
            .filter(other -> other.getGameProfile().getName().equalsIgnoreCase(p.duelOpponent)).findFirst().orElse(null);
        if (opponent == null) return "§cПротивник отключился. Противостояние отменено.";
        PlayerProfile o = profile(opponent);
        opponent.sendMessage(Text.literal("§b[ПРОТИВОСТОЯНИЕ] §fПротивник выбрал тактику. Введи DUEL <тактика>."), false);
        if (o.duelMove.isBlank()) {
            markDirty();
            return "§bТАКТИКА ПРИНЯТА §7| Ожидаем выбор противника. Его ход ещё не раскрыт.";
        }
        ServerPlayerEntity attacker = p.duelRole.equals("ATTACK") ? player : opponent;
        ServerPlayerEntity defender = p.duelRole.equals("DEFEND") ? player : opponent;
        PlayerProfile a = profile(attacker), d = profile(defender);
        String attackMove = a.duelMove, defenseMove = d.duelMove;
        int attackScore = switch (attackMove) {
            case "BURST" -> a.automation * 2 + a.cpu + ThreadLocalRandom.current().nextInt(1, 7);
            case "FEINT" -> a.stealth * 2 + (a.vpnActive ? 2 : 0) + ThreadLocalRandom.current().nextInt(1, 7);
            default -> a.extractor * 2 + a.cpu + ThreadLocalRandom.current().nextInt(1, 7);
        };
        int defenseScore = switch (defenseMove) {
            case "PATCH" -> d.firewall * 2 + d.cpu + ThreadLocalRandom.current().nextInt(1, 7);
            case "DECOY" -> d.guardLevel * 2 + d.stealth + ThreadLocalRandom.current().nextInt(1, 7);
            default -> d.guardLevel + d.firewall * 2 + ThreadLocalRandom.current().nextInt(1, 7);
        };
        String report;
        if (defenseScore >= attackScore) {
            d.counterTarget = attacker.getGameProfile().getName(); d.counterUntilTick = now + 200; d.counterBonus = 6;
            report = "§aЗАЩИТНИК ПОБЕДИЛ §7| Атака " + attackScore + " / защита " + defenseScore
                + " §7| Контур атаки вскрыт. У защищавшегося есть 10 секунд на COUNTER с бонусом +6 к силе атаки.";
            defender.sendMessage(Text.literal(report), false);
            attacker.sendMessage(Text.literal("§c[ПОРАЖЕНИЕ] §fТвоя операция отражена. Противник получил окно контратаки."), false);
        } else {
            report = "§cАТАКУЮЩИЙ ПОБЕДИЛ §7| Атака " + attackScore + " / защита " + defenseScore
                + " §7| Защита перегружена. Основной эффект операции выполнен.";
            String effect = applyCombatEffect(attacker, defender, a.duelAction, now);
            report += "\n" + effect;
            attacker.sendMessage(Text.literal(report), false);
            defender.sendMessage(Text.literal("§c[ЗАЩИТА ПРОБИТА] §f" + report), false);
        }
        clearDuel(a, d);
        markDirty();
        return report;
    }

    private String applyCombatEffect(ServerPlayerEntity attacker, ServerPlayerEntity target, String action, long now) {
        PlayerProfile a = profile(attacker), d = profile(target);
        if (action.equals("RAID")) {
            int credits = Math.min(d.credits, Math.max(1, d.credits / 4));
            int shards = Math.min(d.dataShards, Math.max(1, d.dataShards / 4));
            d.credits -= credits; a.credits += credits; d.dataShards -= shards; a.dataShards += shards + 1; a.reputation += 2;
            return "§6ДОБЫЧА РЕЙДА §7| +" + credits + " кредитов, +" + (shards + 1) + " фрагментов данных.";
        }
        return switch (action.toLowerCase()) {
            case "station" -> {
                if (d.stationHealth <= 0) yield "§eРабочая станция уже разрушена.";
                int damage = Math.min(40, d.stationHealth); d.stationHealth -= damage;
                if (d.stationHealth == 0) breakWorkstation(target, d);
                yield "§cСТАНЦИЯ ПОВРЕЖДЕНА §7| -" + damage + "% прочности.";
            }
            case "farm" -> {
                if (d.farmLevel == 0 || d.farmHealth <= 0) yield "§eУ цели нет работающей фермы.";
                int damage = Math.min(45, d.farmHealth); d.farmHealth -= damage;
                if (d.farmHealth == 0) playerBreakFarm(target, d);
                yield "§cФЕРМА ПОВРЕЖДЕНА §7| -" + damage + "% прочности.";
            }
            case "power" -> { d.powerUntilTick = now + 1200; yield "§cПИТАНИЕ ОТКЛЮЧЕНО НА 60 СЕКУНД."; }
            case "supplies" -> { int stolen = Math.min(d.credits, 20 + a.extractor * 5); d.credits -= stolen; a.credits += stolen; yield "§eПЕРЕХВАЧЕНО " + stolen + " КРЕДИТОВ."; }
            case "data" -> { int stolen = Math.min(d.dataShards, 2 + a.extractor); d.dataShards -= stolen; a.dataShards += stolen; yield "§bПЕРЕХВАЧЕНО " + stolen + " ФРАГМЕНТОВ ДАННЫХ."; }
            case "alarm" -> { d.alertLevel = Math.min(5, d.alertLevel + 2); yield "§6СИГНАЛИЗАЦИЯ ПЕРЕГРУЖЕНА."; }
            case "lock" -> { d.powerUntilTick = now + 600; yield "§cСТАНЦИЯ ЗАБЛОКИРОВАНА НА 30 СЕКУНД."; }
            case "falsecall" -> { d.wantedLevel = Math.min(5, d.wantedLevel + 2); yield "§cУРОВЕНЬ РОЗЫСКА ЦЕЛИ ПОВЫШЕН."; }
            case "raiders" -> "§eСИМУЛЯЦИЯ НАЛЁТА: операция завершена, тревога повышена.";
            default -> "§eОперация не распознана.";
        };
    }

    public String raid(ServerPlayerEntity attacker, ServerPlayerEntity target, long now) {
        if (attacker.getUuid().equals(target.getUuid())) return "§cНельзя атаковать собственную сеть.";
        PlayerProfile a = profile(attacker), d = profile(target);
        int attack = attackPower(a), defense = defensePower(d);
        int blockChance = Math.max(5, Math.min(95, 50 + (defense - attack) * 5));
        boolean blocked = ThreadLocalRandom.current().nextInt(100) < blockChance;
        if (!a.vpnActive && ThreadLocalRandom.current().nextInt(100) < Math.max(10, 55 - a.stealth * 7))
            a.wantedLevel = Math.min(5, a.wantedLevel + 1);
        if (blocked) {
            d.alertLevel = Math.min(5, d.alertLevel + 1);
            markDirty();
            return "§aАВТОЗАЩИТА ОТРАЗИЛА АТАКУ §7| Сила защиты " + defense + ", сила атаки " + attack
                + " §7| Шанс автоматического блока " + blockChance + "%. Противостояние не потребовалось.";
        }
        markDirty();
        return startDuel(attacker, target, "RAID", now);
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
            e.putInt("farm_level", p.farmLevel); e.putInt("farm_health", p.farmHealth); e.putInt("station_health", p.stationHealth); e.putInt("remote_station", p.remoteStationLevel); e.putString("counter_target", p.counterTarget); e.putLong("counter_until", p.counterUntilTick); e.putInt("counter_bonus", p.counterBonus); e.putString("duel_opponent", p.duelOpponent); e.putString("duel_action", p.duelAction); e.putString("duel_move", p.duelMove); e.putString("duel_role", p.duelRole); e.putLong("duel_until", p.duelUntilTick); e.putString("defense_code", p.defenseCode); e.putInt("defense_attempts", p.defenseAttempts); e.putString("defense_game", p.defenseGame); e.putString("defense_route", p.defenseRoute); e.putInt("defense_route_step", p.defenseRouteStep); e.putInt("defense_x", p.defenseX); e.putInt("defense_y", p.defenseY); e.putString("defense_grid", p.defenseGrid); e.putBoolean("vpn_owned", p.vpnOwned); e.putBoolean("vpn_active", p.vpnActive); e.putInt("wanted", p.wantedLevel); e.putInt("virtual_ip", p.virtualIp); e.putLong("last_police_check", p.lastPoliceCheckTick);
            e.putString("network_target", p.networkTarget); e.putInt("network_stage", p.networkStage); e.putLong("last_network_action", p.lastNetworkActionTick);
            e.putInt("network_heat", p.networkHeat); e.putLong("last_network_activity", p.lastNetworkActivityTick); e.putString("last_network_event", p.lastNetworkEvent);
            e.putBoolean("police_stakeout", p.policeStakeout); e.putBoolean("eliminated", p.eliminated); e.putBoolean("round_won", p.roundWon);
            e.putInt("home_index", p.homeIndex); e.putInt("home_version", p.homeVersion); e.putInt("home_x", p.homeX); e.putInt("home_y", p.homeY); e.putInt("home_z", p.homeZ); e.putBoolean("home_built", p.homeBuilt);
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
            p.farmLevel = clamp(e.getInt("farm_level"), 0, 5); p.farmHealth = clamp(e.getInt("farm_health"), 0, 100); p.stationHealth = e.contains("station_health") ? clamp(e.getInt("station_health"), 0, 100) : 100; p.remoteStationLevel = clamp(e.getInt("remote_station"), 0, 1); p.counterTarget = e.getString("counter_target"); p.counterUntilTick = e.getLong("counter_until"); p.counterBonus = e.getInt("counter_bonus"); p.duelOpponent = e.getString("duel_opponent"); p.duelAction = e.getString("duel_action"); p.duelMove = e.getString("duel_move"); p.duelRole = e.getString("duel_role"); p.duelUntilTick = e.getLong("duel_until"); p.defenseCode = e.getString("defense_code"); p.defenseAttempts = e.contains("defense_attempts") ? clamp(e.getInt("defense_attempts"), 0, 3) : 3; p.defenseGame = e.getString("defense_game"); p.defenseRoute = e.getString("defense_route"); p.defenseRouteStep = clamp(e.getInt("defense_route_step"), 0, 9); p.defenseX = clamp(e.getInt("defense_x"), 0, 2); p.defenseY = clamp(e.getInt("defense_y"), 0, 2); p.defenseGrid = e.getString("defense_grid"); p.vpnOwned = e.getBoolean("vpn_owned"); p.vpnActive = e.getBoolean("vpn_active"); p.wantedLevel = clamp(e.getInt("wanted"), 0, 5); p.virtualIp = e.contains("virtual_ip") ? clamp(e.getInt("virtual_ip"), 20, 219) : Math.floorMod(e.getUuid("uuid").hashCode(), 200) + 20; p.lastPoliceCheckTick = e.getLong("last_police_check");
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
        private int homeIndex = -1, homeX, homeY, homeZ, homeVersion = 0; private boolean homeBuilt = false;
        private int farmLevel = 0, farmHealth = 0, stationHealth = 100, remoteStationLevel = 0, wantedLevel = 0, virtualIp = 42, networkStage = 0;
        private String counterTarget = "";
        private long counterUntilTick = 0;
        private int counterBonus = 0;
        private String duelOpponent = "", duelAction = "", duelMove = "", duelRole = "", defenseCode = "", defenseGame = "", defenseRoute = "", defenseGrid = "";
        private int defenseAttempts = 3, defenseRouteStep = 0, defenseX = 0, defenseY = 0;
        private long duelUntilTick = 0;
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
