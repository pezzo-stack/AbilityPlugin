package me.pezzo.abilityPlugin.managers.effects;

import me.pezzo.abilityPlugin.AbilityPlugin;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BlackholeEffect {

    private static final int DURATION_TICKS = 400; // 20 secondi
    private static final int DAMAGE_INTERVAL_TICKS = 60; // danno ogni 3s
    private static final double MAX_PULL = 2.5;

    private final Player owner;
    private final Location center;
    private final double damageValue;
    private final double rangeValue;

    public BlackholeEffect(Player owner, double damageValue, double rangeValue) {
        this.owner = owner;
        this.center = owner.getLocation().clone().add(0, 1.0, 0);
        this.damageValue = damageValue;
        this.rangeValue = rangeValue;
    }

    public void start() {
        owner.getWorld().playSound(center, Sound.ENTITY_WITHER_SPAWN, 1f, 0.8f);

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (!owner.isOnline()) {
                    cancel();
                    return;
                }

                if (tick >= DURATION_TICKS) {
                    // Effetto finale esplosivo
                    center.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, center.clone(), 3, 0.5, 0.5, 0.5, 0);
                    center.getWorld().spawnParticle(Particle.SMOKE_LARGE, center.clone(), 80, 2.0, 2.0, 2.0, 0.15);
                    owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_WITHER_DEATH, 1f, 0.7f);
                    cancel();
                    return;
                }

                // Effetti visivi principali
                center.getWorld().spawnParticle(Particle.PORTAL, center.clone(), 50, 1.2, 1.2, 1.2, 0.08);
                center.getWorld().spawnParticle(Particle.REVERSE_PORTAL, center.clone(), 30, 0.9, 0.9, 0.9, 0.06);
                center.getWorld().spawnParticle(Particle.END_ROD, center.clone(), 25, 1.5, 1.5, 1.5, 0.03);

                // Cerchio rotante di particelle intorno al buco nero
                double angle = (tick * 6.0) % 360.0;
                drawParticleCircle(angle);

                // Attrazione entità
                for (Entity e : center.getWorld().getNearbyEntities(center, rangeValue, rangeValue, rangeValue)) {
                    if (e.equals(owner) || e.isDead()) continue;

                    Location eloc = e.getLocation().clone();
                    Vector toCenter = center.toVector().subtract(eloc.toVector());
                    double distance = toCenter.length();
                    if (distance <= 0.001) continue;

                    double strengthFactor = 1.0 + ((rangeValue - Math.min(distance, rangeValue)) / rangeValue);
                    double pull = Math.min(0.8 * strengthFactor, MAX_PULL);

                    Vector vel = toCenter.normalize().multiply(pull);
                    vel.setY(Math.max(vel.getY(), -0.3) + 0.15);

                    e.setVelocity(vel);

                    // Particelle intorno all'entità attratta
                    Location particleLoc = eloc.clone().add(0, 0.5, 0);
                    e.getWorld().spawnParticle(Particle.SPELL_WITCH, particleLoc, 3, 0.3, 0.3, 0.3, 0.02);

                    // Danno ogni 3 secondi
                    if (tick % DAMAGE_INTERVAL_TICKS == 0 && e instanceof LivingEntity) {
                        ((LivingEntity) e).damage(damageValue, owner);
                    }
                }

                tick++;
            }

            private void drawParticleCircle(double angle) {
                double radius = 5.0;
                for (int i = 0; i < 20; i++) {
                    double theta = Math.toRadians(angle + (i * 18.0));
                    double x = Math.cos(theta) * radius;
                    double z = Math.sin(theta) * radius;
                    Location particleLoc = center.clone().add(x, 0, z);
                    center.getWorld().spawnParticle(Particle.SOUL, particleLoc, 1, 0, 0, 0, 0);
                }
            }
        }.runTaskTimer(AbilityPlugin.getInstance(), 0L, 1L);
    }
}
