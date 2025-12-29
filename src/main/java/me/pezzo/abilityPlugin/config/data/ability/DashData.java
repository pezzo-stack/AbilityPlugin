package me.pezzo.abilityPlugin.config.data.ability;

import me.pezzo.abilityPlugin.config.data.AbilityData;
import org.bukkit.Material;

public class DashData extends AbilityData {
    private double boost;

    public DashData(String name, String lore, Material item, double boost, long cooldown) {
        super(name, lore, item, cooldown);
        this.boost = boost;
    }

    public double getBoost() { return boost; }
}
