package me.wertiko.elyTools.commands;

import me.wertiko.elyTools.ElyTools;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class SizeCommandExecutor implements CommandExecutor, TabCompleter {
    private double minSize, maxSize, stepSize;
    private boolean needPerm;
    private FileConfiguration config;
    private final ElyTools plugin;
    ArrayList<Double> sizes = new ArrayList<>(16);
    List<String> sizesStr = new ArrayList<>(16);

    public SizeCommandExecutor(ElyTools plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
        minSize = config.getDouble("size.size-min", 0.25);
        maxSize = config.getDouble("size.size-max", 2.0) + 0.000000001;
        stepSize = config.getDouble("size.size-step", 0.25);
        needPerm = config.getBoolean("size.need-permission", true);
        fillSizes();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (needPerm && !sender.hasPermission("elytools.use")) {
            sender.sendMessage(config.getString("size.messages.no-permission", ""));
            return false;
        }
        double size;
        try {
            size = Double.parseDouble(args[0]);
        } catch (Exception e) {
            if (args.length > 0)
                sender.sendMessage(config.getString("size.messages.fail", "").replace("%size%", args[0]));
            return false;
        }
        if (!sizes.contains(size)) {
            sender.sendMessage(config.getString("size.messages.fail", ""));
            return false;
        }
        ((Player) sender).getAttribute(Attribute.GENERIC_SCALE).setBaseValue(size);
        sender.sendMessage(config.getString("size.messages.success", "").replace("%size%", args[0]));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("size")) {
            if (args.length == 1) {
                suggestions.addAll(sizesStr);
            }
        }
        return suggestions;
    }

    private void fillSizes() {
        for (double j, i = minSize; i < maxSize; i += stepSize) {
            j = (double) Math.round(i * 1000000) / 1000000;
            sizes.add(j);
            sizesStr.add(String.valueOf(j));
            if (sizes.size() > 1000) break;
        }
    }

}