package io.github.thesowut.hearthstone.commands;

import io.github.thesowut.hearthstone.handler.Hearthstone;
import io.github.thesowut.hearthstone.helpers.HearthstoneHelper;
import io.github.thesowut.hearthstone.helpers.PluginHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HearthstoneCommands implements CommandExecutor {
    private final FileConfiguration _config;
    private final HearthstoneHelper _hearthstoneHelper;
    private final PluginHelper _pluginHelper;
    private final Hearthstone _main;

    public HearthstoneCommands(
            FileConfiguration config,
            HearthstoneHelper hearthstoneHelper,
            PluginHelper pluginhelper,
            Hearthstone main
    ) {
        this._config = config;
        this._hearthstoneHelper = hearthstoneHelper;
        this._pluginHelper = pluginhelper;
        this._main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(_pluginHelper.title + ChatColor.RED + "Must be a player to do that!");
            return true;
        }

        Player player = ((Player) sender).getPlayer();
        String command = cmd.getName().toLowerCase();

        switch (command) {
            case "get":
                if (player.getInventory().contains(_hearthstoneHelper.hearthstoneItem)) {
                    player.sendMessage(_pluginHelper.title + ChatColor.RED + "You can only carry a single Hearthstone!");
                    break;
                }

                player.getInventory().addItem(_hearthstoneHelper.hearthstoneItem);
                player.sendMessage(_pluginHelper.title + ChatColor.GREEN + "A Hearthstone appears in your pocket!");
                break;
            case "sethome":
                if (!this._hearthstoneHelper.canUseHearthstone(player)) {
                    player.sendMessage(_pluginHelper.title + ChatColor.RED + "Must be grounded to perform that!");
                    break;
                }

                _config.set(player.getName().toLowerCase(), player.getLocation());
                player.sendMessage(_pluginHelper.title + ChatColor.GREEN + "You new home has been set.");
                _main.saveConfig();
                break;
        }
        return true;
    }
}