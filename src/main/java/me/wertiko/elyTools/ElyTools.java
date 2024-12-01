package me.wertiko.elyTools;

import me.wertiko.elyTools.commands.DescriptionCommands;
import me.wertiko.elyTools.commands.SizeCommandExecutor;
import me.wertiko.elyTools.listeners.PlayerClickListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class ElyTools extends JavaPlugin {
    public DatabaseManager databaseManager;
    public String prefix;
    public FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        config = getConfig();
        prefix = config.getString("prefix", "ElyTools");

        databaseManager = new DatabaseManager(this);

        getServer().getPluginManager().registerEvents(new PlayerClickListener(this), this);
        Objects.requireNonNull(getCommand("size")).setExecutor(new SizeCommandExecutor(this));
        Objects.requireNonNull(getCommand("size")).setTabCompleter(new SizeCommandExecutor(this));
        Objects.requireNonNull(getCommand("description")).setExecutor(new DescriptionCommands(this));
        Objects.requireNonNull(getCommand("description")).setTabCompleter(new DescriptionCommands(this));
    }

    @Override
    public void onDisable() {
        databaseManager.disconnect();
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("elytools.reload")) {
            sender.sendMessage(prefix + config.getString("noPermission", "У вас нет прав на выполнение этой команды!"));
            return true;
        }

        reloadConfig();
        config = getConfig();
        prefix = config.getString("prefix", "ElyTools");
        sender.sendMessage(prefix + "Конфигурация перезагружена!");

        return true;

    }
}
