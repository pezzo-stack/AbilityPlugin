package me.pezzo.abilityPlugin.managers;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CooldownManager {

    private final ConcurrentMap<UUID, ConcurrentMap<String, Long>> cooldowns = new ConcurrentHashMap<>();

    public boolean tryUse(UUID player, String ability, long cooldownMillis) {
        long now = System.currentTimeMillis();
        ConcurrentMap<String, Long> map = cooldowns.computeIfAbsent(player, k -> new ConcurrentHashMap<>());
        Long expires = map.get(ability);
        if (expires != null && now < expires) {
            return false;
        }
        map.put(ability, now + cooldownMillis);
        return true;
    }

    public long getRemainingMillis(UUID player, String ability) {
        long now = System.currentTimeMillis();
        Map<String, Long> map = cooldowns.get(player);
        if (map == null) return 0;
        Long expires = map.get(ability);
        if (expires == null) return 0;
        return Math.max(0, expires - now);
    }

    public void clear(UUID player) {
        cooldowns.remove(player);
    }

    public void clearAbility(UUID player, String ability) {
        Map<String, Long> map = cooldowns.get(player);
        if (map != null) map.remove(ability);
    }
}
