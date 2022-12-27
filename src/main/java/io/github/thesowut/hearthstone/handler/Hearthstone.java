package io.github.thesowut.hearthstone.handler;

import io.github.thesowut.hearthstone.commands.HearthstoneCommands;
import io.github.thesowut.hearthstone.helpers.HearthstoneHelper;
import io.github.thesowut.hearthstone.helpers.PluginHelper;
import io.github.thesowut.hearthstone.listeners.HearthstoneListener;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class Hearthstone extends JavaPlugin {
    private final FileConfiguration _config = this.getConfig();
    private final PluginHelper _pluginHelper = new PluginHelper();
    private final HearthstoneHelper _hearthstoneHelper = new HearthstoneHelper(_config, _pluginHelper.title);
    private final HearthstoneListener _hearthstoneListener = new HearthstoneListener(_hearthstoneHelper);
    private final HearthstoneCommands _hearthstoneCommands =
            new HearthstoneCommands(_config, _hearthstoneHelper, _pluginHelper, this);

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getConsoleSender().sendMessage(_pluginHelper.title + ChatColor.GREEN + "Plugin enabled.");
        getServer().getPluginManager().registerEvents(_hearthstoneListener, this);

        // TODO
        // find a better way to do this
        getCommand("get").setExecutor(_hearthstoneCommands);
        getCommand("sethome").setExecutor(_hearthstoneCommands);

        // TODO
        // instead of using config for player homes, create separate file inside userdata dir
        // use config to for the following:
        // number of hearthstone usages
        // hearthstone material
        // cooldown between usages length

        _config.options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getConsoleSender().sendMessage(_pluginHelper.title + ChatColor.RED + "Plugin disabled.");
        HandlerList.unregisterAll(_hearthstoneListener);
    }
}