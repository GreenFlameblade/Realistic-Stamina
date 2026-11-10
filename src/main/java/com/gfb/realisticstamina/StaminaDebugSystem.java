package com.gfb.realisticstamina;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.Store;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StaminaDebugSystem extends EntityTickingSystem<EntityStore> {

    private final RealisticStaminaPlugin plugin;

    private Query<EntityStore> query;

    private final Map<UUID, Float> lastStamina = new HashMap<>();

    public StaminaDebugSystem(RealisticStaminaPlugin plugin) {
        this.plugin = plugin;
    }

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        if (query == null) {
            query = Query.and(Player.getComponentType(), EntityStatMap.getComponentType());
        }
        return query;
    }

    @Override
    public void tick(float dt, int index,
                     @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
                     @Nonnull Store<EntityStore> store,
                     @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        EntityStatMap statMap = archetypeChunk.getComponent(index, EntityStatMap.getComponentType());
        PlayerRef playerRef = archetypeChunk.getComponent(index, PlayerRef.getComponentType());
        if (statMap == null || playerRef == null) {
            return;
        }

        int staminaIndex = DefaultEntityStatTypes.getStamina();
        EntityStatValue stamina = statMap.get(staminaIndex);
        if (stamina == null) {
            return;
        }
        float current = stamina.get();

        UUID uuid = playerRef.getUuid();
        Float previous = lastStamina.get(uuid);

        if (previous != null && Math.abs(current - previous) > 0.0001f) {
            for (Map.Entry<UUID, DebugTarget> entry : plugin.getDebugWatchers().getWatchers().entrySet()) {
                if (entry.getValue().matches(uuid)) {
                    PlayerRef watcher = Universe.get().getPlayer(entry.getKey());
                    if (watcher != null) {
                        watcher.sendMessage(Message.raw("[stamina] " + playerRef.getUsername() + ": " + previous + "-> " + current));
                    }
                }
            }
        }
        lastStamina.put(uuid, current);
    }
}
