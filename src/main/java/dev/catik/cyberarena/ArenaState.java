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
import java.util.Map;
import java.util.UUID;

public final class ArenaState extends PersistentState {
    private static final String STORAGE_KEY = "cyber_arena_profiles";
    private static final Type<ArenaState> TYPE = new Type<>(
        ArenaState::new,
        ArenaState::fromNbt,
        DataFixTypes.LEVEL
    );

    private final Map<UUID, PlayerProfile> profiles = new HashMap<>();

    public static ArenaState get(MinecraftServer server) {
        ServerWorld world = server.getOverworld();
        return world.getPersistentStateManager().getOrCreate(TYPE, STORAGE_KEY);
    }

    public PlayerProfile getProfile(ServerPlayerEntity player) {
        return profiles.computeIfAbsent(player.getUuid(), id ->
            new PlayerProfile(player.getGameProfile().getName())
        );
    }

    public boolean discoverNode(ServerPlayerEntity player, String nodeId) {
        PlayerProfile profile = getProfile(player);
        if (!profile.discoveredNodes.add(nodeId)) {
            return false;
        }
        profile.credits += 25;
        profile.reputation += 5;
        markDirty();
        return true;
    }

    public record ProfileView(int credits, int reputation, int discoveredNodes) {
    }

    public ProfileView view(ServerPlayerEntity player) {
        PlayerProfile profile = getProfile(player);
        return new ProfileView(profile.credits, profile.reputation, profile.discoveredNodes.size());
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList entries = new NbtList();
        profiles.forEach((uuid, profile) -> {
            NbtCompound entry = new NbtCompound();
            entry.putUuid("uuid", uuid);
            entry.putString("name", profile.name);
            entry.putInt("credits", profile.credits);
            entry.putInt("reputation", profile.reputation);

            NbtList discovered = new NbtList();
            profile.discoveredNodes.forEach(node -> {
                NbtCompound nodeEntry = new NbtCompound();
                nodeEntry.putString("id", node);
                discovered.add(nodeEntry);
            });
            entry.put("discovered_nodes", discovered);
            entries.add(entry);
        });
        nbt.put("profiles", entries);
        return nbt;
    }

    private static ArenaState fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        ArenaState state = new ArenaState();
        NbtList entries = nbt.getList("profiles", NbtCompound.COMPOUND_TYPE);
        for (int i = 0; i < entries.size(); i++) {
            NbtCompound entry = entries.getCompound(i);
            if (!entry.containsUuid("uuid")) {
                continue;
            }
            UUID uuid = entry.getUuid("uuid");
            PlayerProfile profile = new PlayerProfile(entry.getString("name"));
            profile.credits = Math.max(0, entry.getInt("credits"));
            profile.reputation = Math.max(0, entry.getInt("reputation"));

            NbtList discovered = entry.getList("discovered_nodes", NbtCompound.COMPOUND_TYPE);
            for (int j = 0; j < discovered.size(); j++) {
                String nodeId = discovered.getCompound(j).getString("id");
                if (!nodeId.isBlank()) {
                    profile.discoveredNodes.add(nodeId);
                }
            }
            state.profiles.put(uuid, profile);
        }
        return state;
    }

    private static final class PlayerProfile {
        private final String name;
        private int credits = 100;
        private int reputation;
        private final java.util.Set<String> discoveredNodes = new java.util.HashSet<>();

        private PlayerProfile(String name) {
            this.name = name;
        }
    }
}
