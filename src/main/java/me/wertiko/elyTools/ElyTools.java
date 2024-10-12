package me.wertiko.elyTools;

import me.wertiko.elyTools.commands.SetDescriptionCommand;
import me.wertiko.elyTools.commands.SizeCommandExecutor;
import me.wertiko.elyTools.listeners.PlayerClickListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class ElyTools extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new PlayerClickListener(this), this);
        Objects.requireNonNull(this.getCommand("description")).setExecutor(new SetDescriptionCommand(this));
        Objects.requireNonNull(getCommand("size")).setExecutor(new SizeCommandExecutor(this));
        Objects.requireNonNull(getCommand("size")).setTabCompleter(new SizeCommandExecutor(this));
    }

    @Override
    public void onDisable() {
    }
}
