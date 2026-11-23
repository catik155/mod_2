package dev.catik.cyberarena;

import net.minecraft.datafixer.DataFixTypes;
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
import java.util.concurrent.ThreadLocalRandom;

public final class ArenaState extends PersistentState {
    private static final String STORAGE_KEY = "cyber_arena_profiles";
    private static final Type<ArenaState> TYPE = new Type<>(
        ArenaState::new, ArenaState::fromNbt, DataFixTypes.LEVEL
    );
    private final Map<UUID, PlayerProfile> profiles = new HashMap<>();

    public static ArenaState get(MinecraftServer server) {
        ServerWorld world = server.getOverworld();
        return world.getPersistentStateManager().getOrCreate(TYPE, STORAGE_KEY);
    }

    private PlayerProfile profile(ServerPlayerEntity player) {
        return profiles.computeIfAbsent(player.getUuid(), id -> new PlayerProfile(player.getGameProfile().getName()));
    }

    public ProfileView view(ServerPlayerEntity player) {
        PlayerProfile p = profile(player);
        return new ProfileView(p.credits, p.reputation, p.dataShards, p.firewall, p.automation,
            p.alertLevel, p.completedContracts.size(), p.discoveredNodes.size());
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
        int chance = Math.max(10, Math.min(90, 65 + a.automation * 8 - d.firewall * 12));
        boolean success = ThreadLocalRandom.current().nextInt(100) < chance;
        if (success) {
            int creditsTaken = Math.min(d.credits, Math.max(1, d.credits / 4));
            d.credits -= creditsTaken;
            a.credits += creditsTaken;
            a.dataShards += 1;
            a.reputation += 2;
            d.alertLevel = Math.min(5, d.alertLevel + 1);
            markDirty();
            return "§aSIMULATED BREACH SUCCESS §7| §f" + target.getName().getString()
                + " §7| §6+" + creditsTaken + " credits §7| §b+1 data shard §7| §cTarget alert " + d.alertLevel + "/5";
        }
        d.alertLevel = Math.min(5, d.alertLevel + 1);
        a.reputation = Math.max(0, a.reputation - 1);
        markDirty();
        return "§cBREACH BLOCKED §7| §f" + target.getName().getString()
            + " §7| §bFirewall " + d.firewall + " §7| §cYour trace was exposed.";
    }

    public String upgrade(ServerPlayerEntity player, String type) {
        PlayerProfile p = profile(player);
        switch (type) {
            case "firewall" -> {
                int cost = 50 + p.firewall * 45;
                if (p.firewall >= 5) return "§eFirewall is already at maximum level 5.";
                if (p.credits < cost) return "§cNot enough credits. Required: " + cost + ". Earn credits from /arena contracts.";
                p.credits -= cost; p.firewall++;
                markDirty();
                return "§aFirewall upgraded to level " + p.firewall + ".";
            }
            case "automation" -> {
                int cost = 65 + p.automation * 55;
                if (p.automation >= 5) return "§eAutomation is already at maximum level 5.";
                if (p.credits < cost) return "§cNot enough credits. Required: " + cost + ".";
                p.credits -= cost; p.automation++;
                markDirty();
                return "§aAttack automation upgraded to level " + p.automation + ". Raid success chance improves.";
            }
            default -> { return "§cUnknown upgrade. Use firewall or automation."; }
        }
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

    public record ProfileView(int credits, int reputation, int dataShards, int firewall,
                              int automation, int alertLevel, int completedContracts, int discoveredNodes) {}

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList entries = new NbtList();
        profiles.forEach((uuid, p) -> {
            NbtCompound e = new NbtCompound();
            e.putUuid("uuid", uuid); e.putString("name", p.name);
            e.putInt("credits", p.credits); e.putInt("reputation", p.reputation);
            e.putInt("data_shards", p.dataShards); e.putInt("firewall", p.firewall);
            e.putInt("automation", p.automation); e.putInt("alert", p.alertLevel);
            e.putLong("last_raid", p.lastRaidTick);
            NbtList nodes = new NbtList();
            p.discoveredNodes.forEach(v -> { NbtCompound n = new NbtCompound(); n.putString("id", v); nodes.add(n); });
            e.put("nodes", nodes);
            NbtList jobs = new NbtList();
            p.completedContracts.forEach(v -> { NbtCompound c = new NbtCompound(); c.putString("id", v); jobs.add(c); });
            e.put("jobs", jobs);
            entries.add(e);
        });
        nbt.putInt("schema_version", 2);
        nbt.put("profiles", entries);
        return nbt;
    }

    private static ArenaState fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        ArenaState state = new ArenaState();
        if (nbt.getInt("schema_version") < 2) return state;
        NbtList entries = nbt.getList("profiles", NbtCompound.COMPOUND_TYPE);
        for (int i = 0; i < entries.size(); i++) {
            NbtCompound e = entries.getCompound(i);
            if (!e.containsUuid("uuid")) continue;
            PlayerProfile p = new PlayerProfile(e.getString("name"));
            p.credits = Math.max(0, e.getInt("credits")); p.reputation = Math.max(0, e.getInt("reputation"));
            p.dataShards = Math.max(0, e.getInt("data_shards")); p.firewall = clamp(e.getInt("firewall"), 0, 5);
            p.automation = clamp(e.getInt("automation"), 0, 5); p.alertLevel = clamp(e.getInt("alert"), 0, 5);
            p.lastRaidTick = e.getLong("last_raid");
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
        private int credits = 0, reputation = 0, dataShards = 0, firewall = 0, automation = 0, alertLevel = 0;
        private long lastRaidTick = Long.MIN_VALUE / 2;
        private final Set<String> discoveredNodes = new HashSet<>();
        private final Set<String> completedContracts = new HashSet<>();
        private PlayerProfile(String name) { this.name = name; }
    }
}
