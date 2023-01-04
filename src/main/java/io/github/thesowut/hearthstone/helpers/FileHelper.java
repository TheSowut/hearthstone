package io.github.thesowut.hearthstone.helpers;

import io.github.thesowut.hearthstone.handler.Hearthstone;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileHelper {
    public enum Configuration {
        cooldown,
        cast_time
    }

    private final Hearthstone _main;
    private final PluginHelper _pluginHelper;
    private File _homes;
    private File _cooldowns;
    private FileConfiguration _homesConfig;
    private FileConfiguration _cooldownsConfig;

    public FileHelper(Hearthstone main, PluginHelper pluginHelper) {
        this._main = main;
        this._pluginHelper = pluginHelper;
    }

    /**
     * Set up the data files storing user homes & cooldowns.
     */
    public void setup() {
        _homes = new File(_main.getDataFolder(), "homes.yml");
        this.createFile(_homes);

        _cooldowns = new File(_main.getDataFolder(), "cooldowns.yml");
        this.createFile(_cooldowns);

        _homesConfig = YamlConfiguration.loadConfiguration(_homes);
        _cooldownsConfig = YamlConfiguration.loadConfiguration(_cooldowns);
    }

    /**
     * Get data from homes.yml.
     *
     * @return - Homes data.
     */
    public FileConfiguration getHomes() {
        return _homesConfig;
    }

    /**
     * Get data from cooldowns.yml.
     *
     * @return - Cooldowns data.
     */
    public FileConfiguration getCooldowns() {
        return _cooldownsConfig;
    }

    /**
     * Save homes.yml.
     */
    public void saveHomes() {
        try {
            _homesConfig.save(_homes);
        } catch (IOException e) {
            _main.getServer().getLogger().severe(_pluginHelper.title + ChatColor.RED
                    + "Error saving " + _homes.getName());
        }
    }

    /**
     * Save cooldowns.yml.
     */
    public void saveCooldowns() {
        try {
            _cooldownsConfig.save(_cooldowns);
        } catch (IOException e) {
            _main.getServer().getLogger().severe(_pluginHelper.title + ChatColor.RED
                    + "Error saving " + _cooldowns.getName());
        }
    }

    /**
     * Reload the plugin data files.
     */
    public void reload() {
        _main.reloadConfig();
        _homesConfig = YamlConfiguration.loadConfiguration(_homes);
        _cooldownsConfig = YamlConfiguration.loadConfiguration(_cooldowns);
    }

    /**
     * Try creating a file.
     *
     * @param file - File to be created.
     */
    private void createFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                _main.getServer().getLogger().severe(_pluginHelper.title + ChatColor.RED + "Error creating " + file.getName());
            }
        }
    }
}
