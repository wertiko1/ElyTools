package me.wertiko.elyTools.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class PlayerClickListener implements Listener {

    private final JavaPlugin plugin;
    private final long cooldown = 500;
    private final Map<Player, Long> lastInteractionTimes = new HashMap<>();

    public PlayerClickListener(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getLogger().info("Включен PlayerClickListener");
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {
            Player clickPlayer = (Player) event.getRightClicked();
            Player clickingPlayer = event.getPlayer();

            long currentTime = System.currentTimeMillis();
            Long lastInteractionTime = lastInteractionTimes.get(clickingPlayer);

            if (lastInteractionTime != null && (currentTime - lastInteractionTime < cooldown)) {
                return;
            }

            String description = plugin.getConfig().getString("descriptions." + clickPlayer.getUniqueId(), "нет");
            clickingPlayer.sendMessage(ChatColor.LIGHT_PURPLE + "Вы толкнули " + ChatColor.GRAY + clickPlayer.getName() + ChatColor.DARK_PURPLE + "\nОписание: " + ChatColor.GRAY + description);
            clickPlayer.sendMessage(ChatColor.LIGHT_PURPLE + "Вас толкнул " + ChatColor.GRAY + clickingPlayer.getName());
            lastInteractionTimes.put(clickingPlayer, currentTime);

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> lastInteractionTimes.remove(clickingPlayer), cooldown / 50);
        }
    }
}
