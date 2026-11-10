package com.gfb.realisticstamina;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public sealed interface DebugTarget {

    boolean matches(UUID player);

    DebugTarget add(DebugTarget other);

    DebugTarget remove(DebugTarget other);

    boolean isEmpty();

    record ALL() implements DebugTarget {
        @Override
        public  boolean matches(UUID player) {
            return true;
        }

        @Override
        public DebugTarget add(DebugTarget other) {
            return this;
        }

        @Override
        public DebugTarget remove(DebugTarget other) {
            if (other instanceof ALL) {
                return new Players(Set.of());
            }
            return this;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }

    record Players(Set<UUID> ids) implements DebugTarget {
        public Players {
            ids = Set.copyOf(ids);
        }

        @Override
        public boolean matches(UUID player) {
            return ids.contains(player);
        }

        @Override
        public DebugTarget add(DebugTarget other) {
            if (other instanceof  Players p) {
                Set<UUID> combined = new HashSet<>(ids);
                combined.addAll(p.ids());
                return new Players(combined);
            }
            return other;
        }

        @Override
        public DebugTarget remove(DebugTarget other) {
            if (other instanceof Players p) {
                Set<UUID> remaining = new HashSet<>(ids);
                remaining.removeAll(p.ids());
                return new Players(remaining);
            }
            return new Players(Set.of());
        }

        @Override
        public boolean isEmpty() {
            return ids.isEmpty();
        }
    }
}
