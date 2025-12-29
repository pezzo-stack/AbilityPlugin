package me.pezzo.abilityPlugin;

import me.pezzo.abilityPlugin.abilities.AbilityConfig;
import me.pezzo.abilityPlugin.commands.AbilityCommand;
import me.pezzo.abilityPlugin.config.AbilityConfig;
import me.pezzo.abilityPlugin.listener.AbilityListener;
import me.pezzo.abilityPlugin.managers.AbilityManager;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitCommandHandler;

public final class AbilityPlugin extends JavaPlugin {

    private static AbilityPlugin instance;
    private BukkitCommandHandler commandHandler;
    private AbilityManager abilityManager;
    private me.pezzo.abilityPlugin.config.AbilityConfig abilityConfig;

    @Override
    public void onEnable() {
        instance = this;

        // Creare il config prima del manager
        abilityConfig = new AbilityConfig(this);
        abilityManager = new AbilityManager(this, abilityConfig);

        registerCommands();
        registerListeners();

        getLogger().info("========================================");
        getLogger().info("AbilityPlugin enabled!");
        getLogger().info("========================================");
    }

    @Override
    public void onDisable() {
        instance = null;
        abilityManager = null;
        abilityConfig = null;
        commandHandler = null;
    }

    public static AbilityPlugin getInstance() {
        return instance;
    }

    public AbilityManager getAbilityManager() {
        return abilityManager;
    }

    public AbilityConfig getAbilityConfig() {
        return abilityConfig;
    }

    private void registerCommands() {
        commandHandler = BukkitCommandHandler.create(this);
        commandHandler.register(new AbilityCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new AbilityListener(this), this);
    }
}
