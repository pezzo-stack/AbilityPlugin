package me.pezzo.abilityPlugin.config;

import me.pezzo.abilityPlugin.AbilityPlugin;
import me.pezzo.abilityPlugin.config.data.AbilityData;
import me.pezzo.abilityPlugin.config.data.ability.BlackholeData;
import me.pezzo.abilityPlugin.config.data.ability.DashData;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AbilityConfig {
    private final AbilityPlugin plugin;
    private final File abilitiesFolder;
    private final Map<String, AbilityData> abilities = new HashMap<>();

    public AbilityConfig(AbilityPlugin plugin) {
        this.plugin = plugin;
        this.abilitiesFolder = new File(plugin.getDataFolder(), "abilities");

        if (!abilitiesFolder.exists()) {
            abilitiesFolder.mkdirs();
        }

        loadAbilities();
    }

    private void loadAbilities() {
        // Carica il file dash.yml
        File dashFile = new File(abilitiesFolder, "dash.yml");
        if (dashFile.exists()) {
            YamlConfiguration dashConfig = YamlConfiguration.loadConfiguration(dashFile);
            DashData dashData = new DashData(
                    dashConfig.getString("name", "&4Dash"),
                    dashConfig.getString("lore", "&fAbilita per scattare in avanti"),
                    Material.valueOf(dashConfig.getString("item", "FEATHER")),
                    dashConfig.getDouble("boost", 2.0),
                    dashConfig.getLong("cooldown", 5000)
            );
            abilities.put("dash", dashData);
        } else {
            createDefaultDashConfig(dashFile);
        }

        // Carica il file blackhole.yml
        File blackholeFile = new File(abilitiesFolder, "blackhole.yml");
        if (blackholeFile.exists()) {
            YamlConfiguration blackholeConfig = YamlConfiguration.loadConfiguration(blackholeFile);
            BlackholeData blackholeData = new BlackholeData(
                    blackholeConfig.getString("name", "&aBlackhole"),
                    blackholeConfig.getString("lore", "&fAbilita per attirare i nemici a se"),
                    Material.valueOf(blackholeConfig.getString("item", "BLACK_DYE")),
                    blackholeConfig.getDouble("damage", 3.0),
                    blackholeConfig.getDouble("range", 20.0),
                    blackholeConfig.getLong("cooldown", 180000)
            );
            abilities.put("blackhole", blackholeData);
        } else {
            createDefaultBlackholeConfig(blackholeFile);
        }
    }

    private void createDefaultDashConfig(File file) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("name", "&4Dash");
        config.set("lore", "&fAbilita per scattare in avanti");
        config.set("item", "FEATHER");
        config.set("boost", 4.0);
        config.set("cooldown", 4000);
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossibile creare il file dash.yml: " + e.getMessage());
        }
    }

    private void createDefaultBlackholeConfig(File file) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("name", "&aBlackhole");
        config.set("lore", "&fAbilita per attirare i nemici a se");
        config.set("item", "BLACK_DYE");
        config.set("damage", 4.0);
        config.set("range", 20.0);
        config.set("cooldown", 4000);
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossibile creare il file blackhole.yml: " + e.getMessage());
        }
    }

    public AbilityData getAbility(String name) {
        return abilities.get(name.toLowerCase());
    }

    public DashData getDashData() {
        AbilityData data = abilities.get("dash");
        return data instanceof DashData ? (DashData) data : null;
    }

    public BlackholeData getBlackholeData() {
        AbilityData data = abilities.get("blackhole");
        return data instanceof BlackholeData ? (BlackholeData) data : null;
    }
}
