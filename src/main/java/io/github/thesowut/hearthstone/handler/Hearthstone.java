package io.github.thesowut.hearthstone.handler;

import io.github.thesowut.hearthstone.commands.HearthstoneCommands;
import io.github.thesowut.hearthstone.helpers.FileHelper;
import io.github.thesowut.hearthstone.helpers.HearthstoneHelper;
import io.github.thesowut.hearthstone.helpers.PluginHelper;
import io.github.thesowut.hearthstone.listeners.InteractionListener;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class Hearthstone extends JavaPlugin {
    private final FileConfiguration _config = this.getConfig();
    private final PluginHelper _pluginHelper = new PluginHelper();
    private final FileHelper _fileHelper = new FileHelper(this, _pluginHelper);
    private final HearthstoneHelper _hearthstoneHelper = new HearthstoneHelper(_pluginHelper, _fileHelper, this);
    private final InteractionListener _interactionListener = new InteractionListener(_fileHelper, _hearthstoneHelper, _pluginHelper);
    private final HearthstoneCommands _hearthstoneCommands =
            new HearthstoneCommands(_hearthstoneHelper, _pluginHelper, _fileHelper);

    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage(_pluginHelper.title + ChatColor.GREEN + "Plugin enabled.");
        getServer().getPluginManager().registerEvents(_interactionListener, this);
        this.loadDataFiles();
        this.setDefaultConfig();

        // Register all commands
        ArrayList<String> pluginCommands = _hearthstoneCommands.getCommands();
        for (String command : pluginCommands) {
            getCommand(command).setExecutor(_hearthstoneCommands);
        }
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(_pluginHelper.title + ChatColor.RED + "Plugin disabled.");
        HandlerList.unregisterAll(_interactionListener);
    }

    /**
     * Create configurations of additional data files.
     * If present, load them.
     */
    private void loadDataFiles() {
        _fileHelper.setup();
        _fileHelper.getHomes().options().copyDefaults(true);
        _fileHelper.getCooldowns().options().copyDefaults(true);
        _fileHelper.saveHomes();
        _fileHelper.saveCooldowns();
    }

    private void setDefaultConfig() {
        // TODO add comment explanation
        _config.addDefault(String.valueOf(FileHelper.Configuration.cooldown), 120);
        // TODO add comment explanation
        _config.addDefault(String.valueOf(FileHelper.Configuration.cast_time), 5);
        _config.options().copyDefaults(true);
        saveConfig();
    }
}
