package me.pezzo.abilityPlugin.managers.effects;

import me.pezzo.abilityPlugin.AbilityPlugin;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BluHollowEffect {

    private final Player owner;
    private final Location position;
    private final double damage;
    private final double radius;
    private final double speed;
    private final int durationTicks;
    private final boolean destroyBlocks;
    private Vector velocity = new Vector(0, 0, 0);

    public BluHollowEffect(Player owner, Location start, Vector initialDirection, double damage, double radius, double speed, int durationTicks, boolean destroyBlocks) {
        this.owner = owner;
        this.position = start.clone();
        this.damage = damage;
        this.radius = Math.max(0.5, radius);
        this.speed = Math.max(0.05, speed);
        this.durationTicks = Math.max(1, durationTicks);
        this.destroyBlocks = destroyBlocks;
        this.velocity = initialDirection.clone().normalize().multiply(this.speed);
    }

    public void start() {
        position.getWorld().playSound(position, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.2f, 0.7f);
        Particle.DustOptions blueDust = new Particle.DustOptions(Color.fromRGB(70, 140, 255), 1.3f);

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (!owner.isOnline()) {
                    cancel();
                    return;
                }

                if (tick >= durationTicks) {
                    position.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, position.clone(), 6, 0.8, 0.8, 0.8, 0.0);
                    position.getWorld().playSound(position, Sound.ENTITY_GENERIC_EXPLODE, 1.1f, 0.6f);
                    cancel();
                    return;
                }

                Location prev = position.clone();

                Vector playerLookPoint = owner.getEyeLocation().add(owner.getLocation().getDirection().normalize().multiply(60)).toVector();
                Vector toTarget = playerLookPoint.subtract(position.toVector());
                if (toTarget.lengthSquared() > 0.0001) {
                    Vector desired = toTarget.clone().normalize().multiply(speed);
                    velocity = velocity.clone().multiply(0.7).add(desired.clone().multiply(0.3));
                    if (velocity.length() > speed * 1.5) velocity = velocity.clone().normalize().multiply(speed * 1.5);
                }

                position.add(velocity);

                int spiralPoints = 20;
                for (int i = 0; i < spiralPoints; i++) {
                    double ang = i * (Math.PI * 2) / spiralPoints + (tick * 0.25);
                    double rx = Math.cos(ang) * (radius * 0.9) * (0.6 + 0.4 * Math.sin(tick * 0.12 + i));
                    double rz = Math.sin(ang) * (radius * 0.9) * (0.6 + 0.4 * Math.cos(tick * 0.12 + i));
                    Location p = position.clone().add(rx, Math.sin(tick * 0.12 + i) * 0.25, rz);
                    position.getWorld().spawnParticle(Particle.REDSTONE, p, 1, 0, 0, 0, 0, blueDust);
                }

                position.getWorld().spawnParticle(Particle.SPELL_WITCH, position.clone(), 28, radius * 0.9, radius * 0.9, radius * 0.9, 0.02);
                position.getWorld().playSound(position, Sound.ENTITY_EVOKER_HURT, 0.18f, 0.9f);

                Vector segment = position.toVector().subtract(prev.toVector());
                double dist = segment.length();
                int samples = Math.max(1, (int) Math.ceil(dist * 3.0));
                for (int s = 0; s <= samples; s++) {
                    double f = (double) s / (double) samples;
                    Location sampleLoc = prev.clone().add(segment.clone().multiply(f));
                    if (destroyBlocks) {
                        int checkR = (int) Math.ceil(radius);
                        for (int x = -checkR; x <= checkR; x++) {
                            for (int y = -checkR; y <= checkR; y++) {
                                for (int z = -checkR; z <= checkR; z++) {
                                    Location check = sampleLoc.clone().add(x, y, z);
                                    if (check.distance(sampleLoc) <= radius) {
                                        Block b = check.getBlock();
                                        Material m = b.getType();
                                        if (m != Material.AIR && m != Material.BEDROCK) {
                                            b.setType(Material.AIR);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    for (Entity e : sampleLoc.getWorld().getNearbyEntities(sampleLoc, radius, radius, radius)) {
                        if (e.equals(owner)) continue;
                        if (e.isDead()) continue;
                        if (e instanceof LivingEntity) {
                            LivingEntity le = (LivingEntity) e;
                            double dBefore = le.getHealth();
                            le.damage(damage, owner);
                            if (!le.isDead()) {
                                Vector push = velocity.clone().normalize().multiply(0.6);
                                push.setY(Math.max(push.getY(), 0.2));
                                le.setVelocity(push);
                            }
                        }
                    }
                }

                tick++;
            }
        }.runTaskTimer(AbilityPlugin.getInstance(), 0L, 1L);
    }
}