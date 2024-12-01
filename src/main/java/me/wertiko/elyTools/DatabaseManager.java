package me.wertiko.elyTools;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

public class DatabaseManager {
    private final ElyTools plugin;
    private HikariDataSource dataSource;

    public DatabaseManager(@NotNull ElyTools plugin) {
        this.plugin = plugin;
        setupDataSource();
        initializeDatabase();
    }

    private void setupDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(plugin.getConfig().getString("database.url"));
        config.setUsername(plugin.getConfig().getString("database.user"));
        config.setPassword(plugin.getConfig().getString("database.password"));
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        dataSource = new HikariDataSource(config);
    }

    public void disconnect() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    public void initializeDatabase() {
        String createDescriptionTable = "CREATE TABLE IF NOT EXISTS ely_descriptions_players (" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "description TEXT," +
                "description_enabled BOOLEAN DEFAULT TRUE)";
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createDescriptionTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getDescription(Player player) {
        String query = "SELECT description FROM ely_descriptions_players WHERE uuid = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, player.getUniqueId().toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("description");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setDescription(Player player, String description) {
        String uuid = player.getUniqueId().toString();
        String query = "INSERT INTO ely_descriptions_players (uuid, description, description_enabled) VALUES (?, ?, TRUE) " +
                "ON DUPLICATE KEY UPDATE description = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, uuid);
            stmt.setString(2, description);
            stmt.setString(3, description);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isDescriptionEnabled(Player player) {
        String query = "SELECT description_enabled FROM ely_descriptions_players WHERE uuid = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, player.getUniqueId().toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("description_enabled");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void toggleDescriptionEnabled(Player player) {
        String uuid = player.getUniqueId().toString();
        boolean currentState = isDescriptionEnabled(player);
        String query = "INSERT INTO ely_descriptions_players (uuid, description_enabled) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE description_enabled = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, uuid);
            stmt.setBoolean(2, !currentState);
            stmt.setBoolean(3, !currentState);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearDescription(Player player) {
        String uuid = player.getUniqueId().toString();
        String query = "UPDATE ely_descriptions_players SET description = NULL WHERE uuid = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, uuid);
            stmt.executeUpdate();
            plugin.getLogger().info("Cleared description for player " + player.getName());
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to clear description for player " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
