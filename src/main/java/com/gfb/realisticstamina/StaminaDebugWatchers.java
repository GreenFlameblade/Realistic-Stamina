package com.gfb.realisticstamina;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StaminaDebugWatchers {
    // watcher's UUID -> Set of watched target UUIDs
    private final Map<UUID, DebugTarget> watchers = new ConcurrentHashMap<>();

    public void subscribe(UUID watcher, DebugTarget target) {
        watchers.merge(watcher, target, DebugTarget::add);
    }

    public void unsubscribe(UUID watcher, DebugTarget target) {
        watchers.computeIfPresent(watcher, (id, current) -> {
            DebugTarget remaining = current.remove(target);
            return remaining.isEmpty() ? null : remaining;
        });
    }

    public Map<UUID, DebugTarget> getWatchers() {
        return Collections.unmodifiableMap(watchers);
    }
}
