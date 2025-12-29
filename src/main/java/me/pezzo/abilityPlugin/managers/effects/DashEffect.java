package me.pezzo.abilityPlugin.managers.effects;

import me.pezzo.abilityPlugin.AbilityPlugin;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DashEffect {

    private static final int TRAIL_TICKS = 6;
    private final double multiplier;
    private final Player owner;

    public DashEffect(Player owner, double multiplier) {
        this.owner = owner;
        this.multiplier = multiplier;
    }

    public void start() {
        Vector dir = owner.getLocation().getDirection().multiply(multiplier);
        dir.setY(0.2);
        owner.setVelocity(dir);
        owner.getWorld().spawnParticle(Particle.CLOUD, owner.getLocation().clone().add(0, 0.5, 0), 10, 0.3, 0.3, 0.3, 0.05);
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1.2f);

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (!owner.isOnline()) {
                    cancel();
                    return;
                }

                if (tick >= TRAIL_TICKS) {
                    owner.getWorld().spawnParticle(Particle.SMOKE_NORMAL, owner.getLocation().clone().add(0, 0.5, 0), 6, 0.4, 0.2, 0.4, 0.02);
                    cancel();
                    return;
                }
                Location loc = owner.getLocation().clone().add(0, 0.5, 0);
                owner.getWorld().spawnParticle(Particle.CLOUD, loc, 6, 0.2, 0.2, 0.2, 0.02);
                owner.getWorld().spawnParticle(Particle.CRIT, loc, 2, 0.1, 0.1, 0.1, 0.0);
                tick++;
            }
        }.runTaskTimer(AbilityPlugin.getInstance(), 0L, 1L);
    }
}
