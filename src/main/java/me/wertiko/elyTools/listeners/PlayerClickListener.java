package me.wertiko.elyTools.listeners;

import me.wertiko.elyTools.DatabaseManager;
import me.wertiko.elyTools.ElyTools;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PlayerClickListener implements Listener {
    private final ElyTools plugin;
    private final long cooldown = 500;
    private final Map<Player, Long> lastInteractionTimes = new HashMap<>();
    private DatabaseManager databaseManager;

    public PlayerClickListener(@NotNull ElyTools plugin) {
        this.databaseManager = plugin.getDatabaseManager();
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteractEntity(@NotNull PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {
            Player clickPlayer = (Player) event.getRightClicked();
            Player clickingPlayer = event.getPlayer();

            long currentTime = System.currentTimeMillis();
            Long lastInteractionTime = lastInteractionTimes.get(clickingPlayer);

            if (lastInteractionTime != null && (currentTime - lastInteractionTime < cooldown)) {
                return;
            }

            if (this.databaseManager.isDescriptionEnabled(clickingPlayer)) {
                String description = this.databaseManager.getDescription(clickPlayer);
                String message;
                if (description != null) {
                    message = plugin.config.getString("playerClick.messages.clickingPlayer", "").replace("%player%", clickPlayer.getName()).replace("%description%", description);
                } else {
                    message = plugin.config.getString("playerClick.messages.clickWithoutDesc", "").replace("%player%", clickPlayer.getName());
                }

                clickingPlayer.sendMessage(MiniMessage.miniMessage().deserialize(message));
            } else {
                String message = plugin.config.getString("playerClick.messages.clickingPlayerNoDescription", "%player%").replace("%player%", clickPlayer.getName());
                clickingPlayer.sendActionBar(MiniMessage.miniMessage().deserialize(message));
            }


            clickPlayer.sendMessage(MiniMessage.miniMessage().deserialize(plugin.config.getString("playerClick.messages.clickPlayer", "").replace("%player%", clickingPlayer.getName())));

            lastInteractionTimes.put(clickingPlayer, currentTime);
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> lastInteractionTimes.remove(clickingPlayer), cooldown / 50);
        }
    }
}
