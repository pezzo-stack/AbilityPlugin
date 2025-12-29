package me.pezzo.abilityPlugin.managers.effects;

import me.pezzo.abilityPlugin.AbilityPlugin;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BlackholeEffect {

    private static final int DURATION_TICKS = 400;
    private static final int DAMAGE_INTERVAL_TICKS = 40;
    private static final double MAX_PULL = 3.5;

    private final Player owner;
    private final Location center;
    private final double damageValue;
    private final double rangeValue;

    public BlackholeEffect(Player owner, Location center, double damageValue, double rangeValue) {
        this.owner = owner;
        this.center = center.clone();
        this.damageValue = damageValue;
        this.rangeValue = rangeValue;
    }

    public void start() {
        owner.getWorld().playSound(center, Sound.ENTITY_WITHER_SPAWN, 1f, 0.7f);
        Particle.DustOptions redDust = new Particle.DustOptions(Color.fromRGB(255, 35, 40), 1.6f);

        new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                if (!owner.isOnline()) {
                    cancel();
                    return;
                }
                if (tick >= DURATION_TICKS) {
                    center.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, center.clone(), 4, 0.6, 0.6, 0.6, 0);
                    center.getWorld().spawnParticle(Particle.REDSTONE, center.clone(), 100, 2.2, 2.2, 2.2, 0, redDust);
                    center.getWorld().playSound(center, Sound.ENTITY_WITHER_DEATH, 1f, 0.6f);
                    cancel();
                    return;
                }
                center.getWorld().spawnParticle(Particle.REDSTONE, center.clone(), 80, 1.6, 1.6, 1.6, 0, redDust);
                center.getWorld().spawnParticle(Particle.SMOKE_LARGE, center.clone(), 60, 1.1, 1.1, 1.1, 0.12);
                double angle = (tick * 8.0) % 360.0;
                drawParticleSpiral(angle, redDust);
                for (Entity e : center.getWorld().getNearbyEntities(center, rangeValue, rangeValue, rangeValue)) {
                    if (e.equals(owner) || e.isDead()) continue;
                    Location eloc = e.getLocation().clone();
                    Vector toCenter = center.toVector().subtract(eloc.toVector());
                    double distance = toCenter.length();
                    if (distance <= 0.001) continue;
                    double strengthFactor = 1.0 + ((rangeValue - Math.min(distance, rangeValue)) / rangeValue);
                    double pull = Math.min(1.0 * strengthFactor, MAX_PULL);
                    Vector vel = toCenter.normalize().multiply(pull);
                    vel.setY(Math.max(vel.getY(), -0.5) + 0.12);
                    e.setVelocity(vel);
                    Location particleLoc = eloc.clone().add(0, 0.5, 0);
                    e.getWorld().spawnParticle(Particle.SPELL_WITCH, particleLoc, 6, 0.3, 0.3, 0.3, 0.02);
                    if (tick % DAMAGE_INTERVAL_TICKS == 0 && e instanceof LivingEntity) {
                        ((LivingEntity) e).damage(damageValue, owner);
                    }
                }
                tick++;
            }

            private void drawParticleSpiral(double angle, Particle.DustOptions dust) {
                double radius = Math.max(1.5, Math.min(6.0, rangeValue / 2.0));
                for (int i = 0; i < 28; i++) {
                    double theta = Math.toRadians(angle + (i * 14.0));
                    double x = Math.cos(theta) * (radius * (1.0 - (i / 28.0) * 0.7));
                    double z = Math.sin(theta) * (radius * (1.0 - (i / 28.0) * 0.7));
                    Location particleLoc = center.clone().add(x, (i % 6) * 0.08, z);
                    center.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 1, 0, 0, 0, 0, dust);
                }
            }
        }.runTaskTimer(AbilityPlugin.getInstance(), 0L, 1L);
    }
}