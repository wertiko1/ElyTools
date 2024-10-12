package me.wertiko.elyTools.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SetDescriptionCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public SetDescriptionCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getLogger().info("Включена команда /description");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("elytools.description")) {
                player.sendMessage(ChatColor.RED + "Недостаточно прав!");
                return true;
            }
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Введите описание!");
                return false;
            }
            String description = String.join(" ", args);
            plugin.getConfig().set("descriptions." + player.getUniqueId(), description);
            plugin.saveConfig();
            player.sendMessage(ChatColor.GREEN + "Ваше описание обновлено!");
            return true;
        }
        return false;
    }
}
