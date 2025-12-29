package me.pezzo.abilityPlugin.config.data.ability;

import me.pezzo.abilityPlugin.config.data.AbilityData;
import org.bukkit.Material;

public class BlackholeData extends AbilityData {
    private double damage;
    private double range;

    public BlackholeData(String name, String lore, Material item, double damage, double range, long cooldown) {
        super(name, lore, item, cooldown);
        this.damage = damage;
        this.range = range;
    }

    public double getDamage() { return damage; }
    public double getRange() { return range; }
}
