package me.wertiko.elyTools.commands;

import me.wertiko.elyTools.DatabaseManager;
import me.wertiko.elyTools.ElyTools;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DescriptionCommands implements CommandExecutor, TabCompleter {
    private final ElyTools plugin;
    private DatabaseManager databaseManager;

    public DescriptionCommands(@NotNull ElyTools plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
        plugin.getLogger().info("Включена команда /description");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Команда доступна только для игроков!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(this.plugin.config.getString("playerClick.messages.invalidArgument", "Неизвестная команда. Попробуйте: toggle, set, clear")));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "clear":
                if (!sender.hasPermission("elytools.description")) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize(this.plugin.prefix + this.plugin.config.getString("noPermission", "У вас нет прав на выполнение этой команды!")));
                    return true;
                }
                this.databaseManager.clearDescription(player);
                sender.sendMessage(MiniMessage.miniMessage().deserialize(this.plugin.config.getString("playerClick.messages.descriptionCleared", "Ваше описание очищено!")));
                return true;

            case "toggle":
                plugin.databaseManager.toggleDescriptionEnabled(player);
                boolean isEnabled = this.databaseManager.isDescriptionEnabled(player);
                String messageKey = isEnabled ? "description.messages.enabled" : "description.messages.disabled";
                sender.sendMessage(MiniMessage.miniMessage().deserialize(plugin.config.getString(messageKey, "Описание переключено.")));
                return true;

            case "set":
                if (!sender.hasPermission("elytools.description")) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize(this.plugin.prefix + this.plugin.config.getString("no_permission", "У вас нет прав на выполнение этой команды!")));
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize(this.plugin.config.getString("playerClick.messages.noDescription", "Введите описание!")));
                    return false;
                }

                String description = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

                if (description.length() > 16) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize(this.plugin.config.getString("playerClick.messages.descriptionTooLong", "Описание не должно превышать 16 символов!")));
                    return false;
                }

                this.databaseManager.setDescription(player, description);
                sender.sendMessage(MiniMessage.miniMessage().deserialize(this.plugin.config.getString("playerClick.messages.descriptionUpdated", "Ваше описание обновлено!")));
                return true;

            default:
                sender.sendMessage(MiniMessage.miniMessage().deserialize(this.plugin.config.getString("messages.invalidArgument", "Неизвестная команда. Попробуйте: toggle, set, clear")));
                return true;
        }
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.add("toggle");
            suggestions.add("clear");
            suggestions.add("set");
        } else if (args.length == 2 && "set".equalsIgnoreCase(args[0])) {
            suggestions.add("<описание>");
        }
        return suggestions;
    }
}
