package me.pezzo.abilityPlugin.managers;

import me.pezzo.abilityPlugin.AbilityPlugin;
import me.pezzo.abilityPlugin.config.AbilityConfig;
import me.pezzo.abilityPlugin.config.data.ability.BlackholeData;
import me.pezzo.abilityPlugin.config.data.ability.DashData;
import me.pezzo.abilityPlugin.managers.effects.BlackholeEffect;
import me.pezzo.abilityPlugin.managers.effects.DashEffect;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AbilityManager {

    private final CooldownManager cooldownManager = new CooldownManager();
    private final AbilityConfig abilityConfig;

    public AbilityManager(AbilityPlugin plugin, AbilityConfig config) {
        this.abilityConfig = config;
    }

    public void executeDash(Player player) {
        UUID id = player.getUniqueId();
        DashData dashData = abilityConfig.getDashData();
        if (dashData == null) {
            player.sendMessage("§cConfigurazione Dash non trovata!");
            return;
        }

        if (!cooldownManager.tryUse(id, "dash", dashData.getCooldown())) {
            long remain = cooldownManager.getRemainingMillis(id, "dash");
            double seconds = Math.ceil(remain / 100.0) / 10.0;
            player.sendMessage("§cDash in cooldown: " + seconds + "s");
            return;
        }

        player.sendMessage("§bHai usato il Dash!");
        new DashEffect(player, dashData.getBoost()).start();
    }

    public void executeBlackhole(Player player) {
        UUID id = player.getUniqueId();
        BlackholeData blackholeData = abilityConfig.getBlackholeData();
        if (blackholeData == null) {
            player.sendMessage("§cConfigurazione Blackhole non trovata!");
            return;
        }

        if (!cooldownManager.tryUse(id, "blackhole", blackholeData.getCooldown())) {
            long remain = cooldownManager.getRemainingMillis(id, "blackhole");
            double seconds = Math.ceil(remain / 100.0) / 10.0;
            player.sendMessage("§cBlackhole in cooldown: " + seconds + "s");
            return;
        }

        player.sendMessage("§5Hai creato un Blackhole!");
        new BlackholeEffect(player, blackholeData.getDamage(), blackholeData.getRange()).start();
    }
}
