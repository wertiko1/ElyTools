package me.wertiko.elyTools.commands;

import me.wertiko.elyTools.ElyTools;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class SizeCommandExecutor implements CommandExecutor, TabCompleter {
    private final ElyTools plugin;
    private final double minSize, maxSize, stepSize;
    private final FileConfiguration config;
    private final List<Double> sizes = new ArrayList<>(16);
    private final List<String> sizesStr = new ArrayList<>(16);

    public SizeCommandExecutor(@NotNull ElyTools plugin) {
        plugin.getLogger().info("Включена команда /size");
        this.plugin = plugin;
        config = plugin.config;
        minSize = config.getDouble("size.sizeMin", 0.25);
        maxSize = config.getDouble("size.sizeMax", 2.0) + 0.000000001;
        stepSize = config.getDouble("size.sizeStep", 0.25);
        fillSizes();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду может использовать только игрок.");
            return false;
        }

        if (!sender.hasPermission("elytools.size")) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(config.getString("noPermission", "")));
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(config.getString("size.messages.usage", "")));
            return false;
        }

        double size;
        try {
            size = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(config.getString("size.messages.failMessage", "").replace("%size%", args[0])));
            return false;
        }

        if (!sizes.contains(size)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(config.getString("size.messages.invalidSize", "Недопустимый размер! Выберите одно из: ") + String.join(", ", sizesStr)));
            return false;
        }

        Player player = (Player) sender;
        player.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(size);
        sender.sendMessage(MiniMessage.miniMessage().deserialize(config.getString("size.messages.successMessage", "").replace("%size%", args[0])));
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("size") && args.length == 1) {
            suggestions.addAll(sizesStr);
        }
        return suggestions;
    }

    private void fillSizes() {
        for (double i = minSize; i < maxSize; i += stepSize) {
            double roundedSize = Math.round(i * 1000000) / 1000000.0;
            sizes.add(roundedSize);
            sizesStr.add(String.valueOf(roundedSize));
            if (sizes.size() > 1000) break;
        }
    }
}
